package com.fmarsh.server.decoding;

import com.fmarsh.server.model.HttpHeader;
import com.fmarsh.server.model.HttpMethod;
import com.fmarsh.server.model.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * HttpDecoder:
 *          InputStreamReader -> bytes to characters ( decoded with certain Charset ( ascii ) )
 *          BufferedReader    -> character stream to text
 */
public class HttpDecoder {

    public static Optional<HttpRequest> decode(final InputStream inputStream) {
        return readMessage(inputStream).flatMap(HttpDecoder::buildRequest);
    }

    private static Optional<List<String>> readMessage(final InputStream inputStream) {
        try {
            if (!(inputStream.available() > 0)) {
                return Optional.empty();
            }

            final char[] inBuffer = new char[inputStream.available()];
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final int read = inputStreamReader.read(inBuffer);

            List<String> message = new ArrayList<>();
            try (Scanner sc = new Scanner(new String(inBuffer))) {
                while (sc.hasNextLine()) {
                    message.add(sc.nextLine());
                }
            }
            return Optional.of(message);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static Optional<HttpRequest> buildRequest(final List<String> messages) {
        if (messages.isEmpty()) {
            return Optional.empty();
        }

        String firstLine = messages.get(0);
        String[] httpInfo = firstLine.split(" ");
        if (httpInfo.length != 3) {
            return Optional.empty();
        }

        String protocolVersion = httpInfo[2];
        if (!protocolVersion.equals("HTTP/1.1")) {
            return Optional.empty();
        }

        try {
            URI uri = new URI(httpInfo[1]);
            Map<String, List<String>> requestHeaders = decodeRequestHeaders(messages);
            String body = decodeRequestBody(messages, requestHeaders);
            Map<String, List<String>> queryParameters = decodeRequestQueryParameters(uri);
            return Optional.of(new HttpRequest.Builder()
                    .withHttpMethod(HttpMethod.valueOf(httpInfo[0]))
                    .withUri(uri)
                    .withHttpRequestHeaders(requestHeaders)
                    .withQueryParameters(queryParameters)
                    .withBody(body)
                    .build());
        } catch (URISyntaxException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private static Map<String, List<String>> decodeRequestHeaders(final List<String> messages) {
        final Map<String, List<String>> requestHeaders = new HashMap<>();
        if (messages.size() > 1) {
            for (int i = 1; i < messages.size(); i++) {
                String header = messages.get(i);

                if (header.isBlank()) {
                    break;
                }

                int colonIndex = header.indexOf(":");

                if (!(colonIndex > 0 && header.length() > colonIndex + 1)) {
                    break;
                }

                String headerName = URLDecoder.decode(header.substring(0, colonIndex), StandardCharsets.UTF_8);
                String headerValue = URLDecoder.decode(header.substring(colonIndex + 1), StandardCharsets.UTF_8);

                if (requestHeaders.containsKey(headerName)) {
                    requestHeaders.get(headerName).add(headerValue);
                } else {
                    List<String> values = new ArrayList<>();
                    values.add(headerValue);
                    requestHeaders.put(headerName, values);
                }
            }
        }
        return requestHeaders;
    }

    private static String decodeRequestBody(final List<String> messages, Map<String, List<String>> requestHeaders) {
        int index = 0;
        while (index < messages.size() && !messages.get(index).isBlank()) {
            index++;
        }

        if (index >= messages.size() - 1) {
            return "";
        }

        String rawBody = messages.get(index + 1);

        long contentLength = Long.parseLong(requestHeaders.get(HttpHeader.CONTENT_LENGTH.getValue()).get(0).trim()); // Todo Add better error handling when parsing int
        if (contentLength != rawBody.length()) {
            throw new RuntimeException("The length of the body does not match the size specified in Content-Length header"); // Todo better exception
        }

        return URLDecoder.decode(rawBody, StandardCharsets.UTF_8);
    }

    private static Map<String, List<String>> decodeRequestQueryParameters(final URI uri) {
        if (uri == null || uri.getQuery() == null || uri.getQuery().isBlank()) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> queryParametersMap = new HashMap<>();
        String queryPathString = uri.getQuery();
        String[] queryParameters = queryPathString.split("&");

        for (String queryParameter : queryParameters) {
            int index = queryParameter.indexOf("=");
            String key = index > 0 ? URLDecoder.decode(queryParameter.substring(0, index), StandardCharsets.UTF_8) : queryParameter;
            String value = index > 0 && queryParameter.length() > index + 1 ? URLDecoder.decode(queryParameter.substring(index + 1), StandardCharsets.UTF_8) : null;
            if (queryParametersMap.containsKey(key)) {
                queryParametersMap.get(key).add(value);
            } else {
                List<String> values = new ArrayList<>();
                values.add(value);
                queryParametersMap.put(key, values);
            }
        }

        return queryParametersMap;
    }
}
