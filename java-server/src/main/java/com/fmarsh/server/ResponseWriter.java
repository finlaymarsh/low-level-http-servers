package com.fmarsh.server;

import com.fmarsh.server.model.HttpHeader;
import com.fmarsh.server.model.HttpResponse;
import com.fmarsh.server.model.HttpStatusCode;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ResponseWriter {

    /**
     * Write a HTTPResponse to an outputstream
     * @param outputStream - the outputstream
     * @param response - the HTTPResponse
     */
    public static void writeResponse(final BufferedWriter outputStream, final HttpResponse response) throws IOException {
        try {
            final int statusCode = response.getStatusCode();
            final String statusCodeMeaning = HttpStatusCode.convertToMessage(statusCode);
            final List<String> responseHeaders = buildHeaderStrings(response.getResponseHeaders());

            outputStream.write("HTTP/1.1 " + statusCode + " " + statusCodeMeaning + "\n");

            for (String header : responseHeaders) {
                outputStream.write(header);
            }

            final Optional<String> entityString = response.getEntity().flatMap(ResponseWriter::getResponseString);
            if (entityString.isPresent()) {
                final String encodedString = new String(entityString.get().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                outputStream.write(HttpHeader.CONTENT_TYPE.getValue() + ": " + encodedString.getBytes().length + "\n");
                outputStream.write("\n");
                outputStream.write(encodedString);
            } else {
                outputStream.write("\n");
            }
        } catch (Exception ignored){}
    }

    private static List<String> buildHeaderStrings(final Map<String, List<String>> responseHeaders) {
        final List<String> responseHeaderList = new ArrayList<>();
        responseHeaders.forEach((name, values) -> {
            final StringBuilder valuesCombined = new StringBuilder();
            values.forEach(valuesCombined::append);
            responseHeaderList.add(name + ": " + valuesCombined + "\n");
        });
        return responseHeaderList;
    }

    private static Optional<String> getResponseString(Object entity) {
        if (entity instanceof String stringEntity) {
            return Optional.of(stringEntity);
        }
        return Optional.empty();
    }
}
