package ru.sakhalin2.caching_proxy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ProxyHttpTest {

    public static final int SOCKET_PORT = 8189;

    private static final CachingProxy proxy = new CachingProxy("./logs");

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private static final FileLogWrapper fileLogger = FileLogWrapper.create(ProxyHttpTest.class.getPackageName().concat(ProxyHttpTest.class.getName()),
            "./logs/".concat(ProxyHttpTest.class.getCanonicalName()).concat(".log"));

    private static final ExecutorService proxyRunner = Executors.newCachedThreadPool();

    @BeforeClass
    public static void setup() {
        proxyRunner.submit(() -> {
            try {
                proxy.loop();
            } catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
            }
            return CachingProxyHelper.EMPTY_STRING;
        });
    }

    @AfterClass
    public static void close() {
        proxyRunner.shutdown();
    }

    @Test
    public void testGet() throws URISyntaxException, InterruptedException, IOException {
        System.out.println("testGetHttpProxy");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .version(Version.HTTP_1_1)
                    .GET()
                    .uri(new URI(String.format("http://localhost:%d", SOCKET_PORT)))
                    .build();
            Thread.sleep(5000);
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (URISyntaxException | InterruptedException | IOException e) {
            fileLogger.append(e);

        }
    }

    @Test
    public void testMultipartFormDataPost() throws URISyntaxException, InterruptedException, IOException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(new URI(String.format("http://localhost:%d", SOCKET_PORT)))
                    .build();

            fileLogger.getLogger().log(Level.INFO, "Response is ready to be sent: {0}", request.uri());
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            fileLogger.getLogger().log(Level.INFO, "Response status code: {0}", response.statusCode());
        } catch (URISyntaxException | InterruptedException | IOException e) {
            fileLogger.append(e);

        }
    }

}
