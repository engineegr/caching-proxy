package ru.sakhalin2.caching_proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestParser {

    private final InputStream socketInputStream;

    private final FileLogWrapper fileLogger;

    public static final String HOST_HEADER_NAME = "Host";

    // Example: GET / HTTP/1.1
    private static final Pattern METHOD_PATH_VERSION_REG_EXP = Pattern.compile("([A-Z]+)\\s(.+)\\s(.+)");

    private static final Pattern CONTENT_LENGTH_HEADER_REG_EXP = Pattern.compile("Content-Length:\s(.+)");

    private static final Pattern HEADER_REG_EXP = Pattern.compile("([A-Za-z-_]+):\s(.+)");

    public RequestParser(InputStream socketInputStream, FileLogWrapper fileLogger) {
        this.socketInputStream = socketInputStream;
        this.fileLogger = fileLogger;
    }

    public Request parse() throws IOException, IllegalArgumentException {
        var requestBuilder = new RequestBuilder();
        try (Scanner lineReader = new Scanner(socketInputStream, StandardCharsets.UTF_8);) {
            boolean hasBody = false;
            StringBuilder strBody = new StringBuilder();
            while (lineReader.hasNextLine()) {
                String line = lineReader.nextLine();
                boolean isMethodPathVersion = addMethodPathVersion(line, requestBuilder);
                boolean isHeader = addHeader(line, requestBuilder);
                if (!hasBody) {                    
                    Matcher matcher = CONTENT_LENGTH_HEADER_REG_EXP.matcher(line);
                    if (matcher.matches()) {
                        int contentLength = Integer.parseInt(matcher.group(1));
                        hasBody = matcher.matches() && contentLength > 0;
                    }
                }
                if (!isMethodPathVersion && !isHeader && hasBody) {
                    strBody.append(line);
                }
            }
            if (hasBody) {
                requestBuilder.addBody(strBody.toString().getBytes());
            }
        }
        return requestBuilder.build();
    }

    public HttpRequest parseJdkHttpRequest()
            throws UnsupportedOperationException, IllegalArgumentException, URISyntaxException, IOException {
        Request request = this.parse();
        if (!request.isValid()) {
            throw new IllegalArgumentException("Couldn't parse the request: " + request);
        } else {
            fileLogger.getLogger().log(Level.INFO, String.format("Parsed INTERNAL http request is DONE SUCCESSFULLY.\nRequest %s.", request));
        }
        return request.toJdkHttpRequest();
    }

    private boolean addMethodPathVersion(String requestLine, RequestBuilder requestBuilder) {
        Matcher matcher = METHOD_PATH_VERSION_REG_EXP.matcher(requestLine);
        // add method, path, version
        if (matcher.matches() && matcher.groupCount() == 3) {
            requestBuilder.addMethod(matcher.group(1));
            requestBuilder.addUri(matcher.group(2));
            requestBuilder.addVersion(matcher.group(3));
            return true;
        }
        return false;
    }

    private boolean addHeader(String requestLine, RequestBuilder requestBuilder) {
        Matcher matcher = HEADER_REG_EXP.matcher(requestLine);
        if (matcher.matches() && matcher.groupCount() == 2) {
            requestBuilder.addHeader(new Header(matcher.group(1), matcher.group(2)));
            return true;
        }
        return false;
    }

}
