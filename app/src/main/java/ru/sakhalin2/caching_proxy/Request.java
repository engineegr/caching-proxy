package ru.sakhalin2.caching_proxy;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public record Request(
        String method,
        String host,
        String url,
        String version,
        List<Header> headers,
        byte[] body) {

    private enum BodyPublisherResolver {
        APPLICATION___MULTIPART_FORMDATA(new String[]{"multipart/form-data"}, (req, builder) -> {
            builder.method(req.method, HttpRequest.BodyPublishers.ofByteArray(req.body));
        }),
        APPLICATION___PLAIN__JSON__X_WWW_FORM_URLENCODED(
                new String[]{"plain/text", "application/json", "application/x-www-form-urlencoded"},
                (req, builder) -> {
                    builder.method(req.method, HttpRequest.BodyPublishers.ofString(new String(req.body)));
                }),
        APPLICATION___NO__CONTENT_TYPE(
                null, (req, builder) -> {});

        private final BiConsumer<Request, java.net.http.HttpRequest.Builder> convertAcceptFn;

        private final String[] contentTypeTitles;

        private BodyPublisherResolver(String[] contentTypeTitles, BiConsumer<Request, java.net.http.HttpRequest.Builder> convertAcceptFn) {
            this.contentTypeTitles = contentTypeTitles;
            this.convertAcceptFn = convertAcceptFn;
        }

        public static BodyPublisherResolver resolve(Header contentTypeHeader) throws UnsupportedOperationException {
            for (var it : ru.sakhalin2.caching_proxy.Request.BodyPublisherResolver.values()) {
                for (var ct : it.contentTypeTitles) {
                    if (ct.contains(contentTypeHeader.name())) {
                        return it;
                    }
                }
            }
            throw new UnsupportedOperationException("Unsupported content-type: " + contentTypeHeader.value());
        }

        public void accept(Request request, java.net.http.HttpRequest.Builder requestBuilder) {
            this.convertAcceptFn.accept(request, requestBuilder);
        }
    }

    @Override
    public String toString() {
        var headersToStr = headers.stream().map((h1) -> h1.toString()).collect(Collectors.joining("\n\t"));
        return Request.class.getPackageName()
                .concat(Request.class.getName())
                .concat("\nmethod = [ ").concat(method).concat(" ] ")
                .concat("\nhost = [ ").concat(host).concat(" ] ")
                .concat("\nurl = [ ").concat(url).concat(" ] ")
                .concat("\nversion = [ ").concat(version).concat(" ] ")
                .concat("\nheaders = [ ").concat(headersToStr).concat(" ]\n");
    }

    public boolean isValid() {
        return method.length() != 0 && host.length() != 0 && version.length() != 0 && url.length() != 0 && !headers.isEmpty();
    }

    public HttpRequest toJdkHttpRequest() throws URISyntaxException, UnsupportedOperationException {
        java.net.http.HttpRequest.Builder requestBuilder
                = HttpRequest.newBuilder().uri(this.getUri());

        this.resolveHttpBodyPublisher().accept(this, requestBuilder);
        return requestBuilder.build();
    }

    public URI getUri() throws URISyntaxException, IllegalArgumentException {
        var uri = this.version.split("\\/")[0].toLowerCase().concat("://").concat((this.host).concat(this.url));
        return new URI(uri);
    }

    private BodyPublisherResolver resolveHttpBodyPublisher() {
        Header contentTypeHeader = Header.DUMMY_HEADER;
        for (var h : this.headers) {
            if (h.name().contains("Content-Type")) {
                contentTypeHeader = h;
                break;
            }
        }
        if (contentTypeHeader != Header.DUMMY_HEADER) {
            return Request.BodyPublisherResolver.resolve(contentTypeHeader);
        }

        return Request.BodyPublisherResolver.APPLICATION___NO__CONTENT_TYPE;
    }
}
