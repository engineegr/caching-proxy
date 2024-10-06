package ru.sakhalin2.caching_proxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class CachingProxy {

    private static final ExecutorService singleThreadExecutor = Executors.newCachedThreadPool();

    public static final int SOCKET_PORT = 8189;

    public static final int SRV_SOCKET_TIMEOUT = 10 * 10 * 1000;

    public static final int CLIENT_SOCKET_TIMEOUT = 1000;

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private final String logDirPath;
    private final FileLogWrapper fileLogger;

    private final AtomicInteger activeConnectionCount = new AtomicInteger(0);
    private final AtomicInteger successConnectionCount = new AtomicInteger(0);
    private final AtomicInteger failureConnectionCount = new AtomicInteger(0);

    public CachingProxy(String logDirPath) {
        this.logDirPath = logDirPath;
        this.fileLogger = FileLogWrapper.create(CachingProxy.class.getPackageName().concat(CachingProxy.class.getName()),
                this.logDirPath.concat("/")
                        .concat(CachingProxy.class.getCanonicalName()).concat(".log"));
    }

    public void setup(Map<CommandSwitch, String> cmdSwitchArgsMap) {
        for (var cmd : cmdSwitchArgsMap.keySet()) {
            cmd.apply(this, cmdSwitchArgsMap.get(cmd));
        }
    }

    public void loop() throws FileNotFoundException {
        try (ServerSocket srvSocket = new ServerSocket(SOCKET_PORT);) {
            srvSocket.setSoTimeout(SRV_SOCKET_TIMEOUT);
            while (true) {
                Socket clientSocket = srvSocket.accept();
                singleThreadExecutor.submit(() -> {
                    HttpRequest httpReq;
                    fileLogger.getLogger().log(Level.INFO, String.format("Parsed new request is START.\nActive Connection %d", activeConnectionCount.incrementAndGet()));
                    try (InputStream in = clientSocket.getInputStream()) {
                        clientSocket.setSoTimeout(CLIENT_SOCKET_TIMEOUT);
                        httpReq = this.parse(in);
                        fileLogger.getLogger().log(Level.INFO, String.format("Parsed new request is DONE SUCCESSFULLY.\nRequest %s.", httpReq));
                        successConnectionCount.incrementAndGet();
                    } catch (UnsupportedOperationException | IllegalArgumentException | URISyntaxException | IOException e) {
                        fileLogger.getLogger().log(Level.SEVERE, String.format("Parsed new request is DONE WITH FAILURE."));
                        failureConnectionCount.incrementAndGet();
                        fileLogger.append(e);
                    } finally {
                        fileLogger.getLogger().log(Level.INFO, String.format("\nActive Connection %d", activeConnectionCount.decrementAndGet()));
                        if (activeConnectionCount.get() == 0) {
                            report();
                        }
                    }
                    return CachingProxyHelper.EMPTY_STRING;
                });
            }
        } catch (IOException e) {
            fileLogger.append(e);
        } finally {
            report();
            singleThreadExecutor.shutdown();
        }
    }

    public void setPort(CommandSwitch cmdSwitch, String args) {
        System.out.println(String.format("Set port %d", Integer.valueOf(args)));
    }

    public void setOriginUrl(CommandSwitch cmdSwitch, String args) {
        System.out.println("Set originUrl " + args);
    }

    public void clearCache(CommandSwitch cmdSwitch, String args) {
        System.out.println("Clear cache");
    }

    private void report() {
        fileLogger.getLogger().log(Level.INFO, String.format("Summary report:\n\tSuccessfull connections: [%d]\n\tFailed connections: [%d]\n\tAll process connections: [%d]",
                successConnectionCount.get(), failureConnectionCount.get(), successConnectionCount.addAndGet(failureConnectionCount.get())));
    }

    private HttpRequest parse(InputStream socketInputStream)
            throws UnsupportedOperationException, IllegalArgumentException, URISyntaxException, IOException {
        return new RequestParser(socketInputStream, fileLogger).parseJdkHttpRequest();
    }

    public static void main(String[] args) {
        var parser = new CommandLineParser(args);
        Map<CommandSwitch, String> cmdSwitchArgsMap = parser.parse();
        var cachingProxy = new CachingProxy("./logs/");
        cachingProxy.setup(cmdSwitchArgsMap);
        try {
            cachingProxy.loop();
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

}
