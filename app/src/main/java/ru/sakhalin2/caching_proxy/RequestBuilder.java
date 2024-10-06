package ru.sakhalin2.caching_proxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RequestBuilder {

    private String method = CachingProxyHelper.EMPTY_STRING;
    private String host = CachingProxyHelper.EMPTY_STRING;
    private String uri = CachingProxyHelper.EMPTY_STRING;
    private String version = CachingProxyHelper.EMPTY_STRING;
    private final List<Header> headers = new ArrayList<>();
    private byte[] body = new byte[0];

    public Request build() {
        return new Request(method, host, uri, version, headers, body);
    }

    public RequestBuilder addMethod(String method) {
        this.method = method;
        return this;
    }

    public RequestBuilder addUri(String uri) {
        this.uri = uri;
        return this;
    }

    public RequestBuilder addVersion(String version) {
        this.version = version;
        return this;
    }

    public RequestBuilder addHeader(Header header) {
        if (header.name().equals(RequestParser.HOST_HEADER_NAME)) {
            this.host = header.value();
        }
        this.headers.add(header);
        return this;
    }

    public RequestBuilder addBody(byte[] body) {
        this.body = Arrays.copyOf(body, body.length);
        return this;
    }
}
