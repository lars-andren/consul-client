package transport;

import lombok.extern.java.Log;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Log
public abstract class AbstractHttp {

    static final int DEFAULT_MAX_CONNECTIONS = 1000;
    static final int DEFAULT_MAX_PER_ROUTE_CONNECTIONS = 500;
    static final int DEFAULT_CONNECTION_TIMEOUT = 10 * 1000;

    // För att det finns blocking queries; https://www.consul.io/api/index.html#blocking-queries
    static final int DEFAULT_READ_TIMEOUT = 1000 * 60 * 10;

    public HttpResponse makeGetRequest(HttpRequest request) throws IOException {
        HttpGet httpGet = new HttpGet(request.getUrl());
        addHeadersToRequest(httpGet, request.getHeaders());

        return executeRequest(httpGet);
    }

    public HttpResponse makePutRequest(HttpRequest request) throws IOException {
        HttpPut httpPut = new HttpPut(request.getUrl());
        addHeadersToRequest(httpPut, request.getHeaders());
        if (request.getContent() != null) {
            httpPut.setEntity(new StringEntity(request.getContent(), StandardCharsets.UTF_8));
        } else {
            httpPut.setEntity(new ByteArrayEntity(request.getBinaryContent()));
        }

        return executeRequest(httpPut);
    }

    public HttpResponse makeDeleteRequest(HttpRequest request) throws IOException {
        HttpDelete httpDelete = new HttpDelete(request.getUrl());
        addHeadersToRequest(httpDelete, request.getHeaders());
        return executeRequest(httpDelete);
    }

    protected abstract HttpClient getHttpClient();

    private HttpResponse executeRequest(HttpUriRequest httpRequest) throws IOException {
        logRequest(httpRequest);

        try {
            return getHttpClient().execute(httpRequest, response -> {
                int statusCode = response.getStatusLine().getStatusCode();
                String statusMessage = response.getStatusLine().getReasonPhrase();

                String content = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);

                Long consulIndex = parseUnsignedLong(response.getFirstHeader("X-Consul-Index"));
                Boolean consulKnownLeader = parseBoolean(response.getFirstHeader("X-Consul-Knownleader"));
                Long consulLastContact = parseUnsignedLong(response.getFirstHeader("X-Consul-Lastcontact"));

                return new HttpResponse(statusCode, statusMessage, content, consulIndex, consulKnownLeader, consulLastContact);
            });
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private Long parseUnsignedLong(Header header) {
        if (header == null) {
            return null;
        }

        String value = header.getValue();
        if (value == null) {
            return null;
        }

        try {
            return Long.parseUnsignedLong(value);
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean parseBoolean(Header header) {
        if (header == null) {
            return null;
        }

        if ("true".equals(header.getValue())) {
            return true;
        }

        if ("false".equals(header.getValue())) {
            return false;
        }

        return null;
    }

    private void addHeadersToRequest(HttpRequestBase request, Map<String, String> headers) {
        if (headers == null) {
            return;
        }

        for (Map.Entry<String, String> headerValue : headers.entrySet()) {
            String name = headerValue.getKey();
            String value = headerValue.getValue();

            request.addHeader(name, value);
        }
    }

    private void logRequest(HttpUriRequest httpRequest) {
        StringBuilder sb = new StringBuilder();

        sb.append(httpRequest.getMethod());
        sb.append(" ");
        sb.append(httpRequest.getURI());
        sb.append(" ");

        HeaderIterator iterator = httpRequest.headerIterator();
        if (iterator.hasNext()) {
            sb.append("Headers:[");

            Header header = iterator.nextHeader();
            sb.append(header.getName()).append("=").append(header.getValue());

            while (iterator.hasNext()) {
                header = iterator.nextHeader();
                sb.append(header.getName()).append("=").append(header.getValue());
                sb.append(";");
            }

            sb.append("] ");
        }

        log.finest(sb.toString());
    }

}
