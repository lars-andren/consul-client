package se.sparklemuffin.consul.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

public class Util {

    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("So strange - every JVM has to support UTF-8 encoding.");
        }
    }

    public static String encodeUrl(String str) {
        try {
            URL url = new URL(str);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toASCIIString();
        } catch (Exception e) {
            throw new RuntimeException("Can't encode url", e);
        }
    }

    public static String generateUrl(String baseUrl, String... params) {
        return generateUrl(baseUrl, Arrays.asList(params));
    }

    public static String generateUrl(String baseUrl, List<String> params) {
        if (params == null) {
            return baseUrl;
        }

        List<String> allParams = new ArrayList<>();
        for (String item : params) {
            if (item != null) {
                allParams.add(item);
            }
        }

        StringBuilder result = new StringBuilder(baseUrl);

        Iterator<String> paramsIterator = allParams.iterator();
        if (paramsIterator.hasNext()) {
            result.append("?").append(paramsIterator.next());
            while (paramsIterator.hasNext()) {
                result.append("&").append(paramsIterator.next());
            }
        }
        return result.toString();
    }

    public static Map<String, String> createTokenMap(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Consul-Token", token);
        return headers;
    }

    public static String toSecondsString(long waitTime) {
        return String.valueOf(waitTime) + "s";
    }

    public static String assembleAgentAddress(String host, int port, String path) {
        String agentPath = "";
        if (path != null && !path.trim().isEmpty()) {
            agentPath = "/" + path;
        }

        return String.format("%s:%d%s", host, port, agentPath);
    }

    public static String trimLeadingSlash(String value) {
        return value.replaceAll("^/+", "");
    }

}
