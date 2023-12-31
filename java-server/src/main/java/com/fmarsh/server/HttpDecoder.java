package com.fmarsh.server;

import com.fmarsh.server.model.HttpMethod;
import com.fmarsh.server.model.HttpRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
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
            return Optional.of(new HttpRequest.Builder()
                    .withHttpMethod(HttpMethod.valueOf(httpInfo[0]))
                    .withUri(new URI(httpInfo[1]))
                    .withHttpRequestHeaders(decodeRequestHeaders(messages))
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
                int colonIndex = header.indexOf(":");

                if (!(colonIndex > 0 && header.length() > colonIndex + 1)) {
                    break;
                }

                String headerName = header.substring(0, colonIndex);
                String headerValue = header.substring(colonIndex + 1);

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
}
