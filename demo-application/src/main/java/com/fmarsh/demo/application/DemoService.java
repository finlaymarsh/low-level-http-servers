package com.fmarsh.demo.application;

import com.fmarsh.server.model.HttpRequest;

public class DemoService {
    final ResponseBuilder responseBuilder;

    public DemoService(ResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    public String everythingTest(String testHeader, boolean showResultHeader, short id, int queryParam1, long queryParam2, HttpRequest request) {
        String headerResponse = headerExtraction(testHeader);
        String queryResponse = queryExtraction(queryParam1, (int) queryParam2);
        if (!showResultHeader) {
            return "Result hidden\n";
        }
        return String.format("{\n\t\"id\": %d\n\t\"path\": %s\n\t\"headerResponse\": %s\t\"queryResponse\": %s}\n",
                                                        id, request.getUri().getPath(), headerResponse, queryResponse);

    }

    public String obiWan() {
        return "Hello There!\n";
    }

    public String anakin() {
        return "You underestimate my power!\n";
    }

    public String dynamicPath1(String dynamicPath) {
        return String.format("Dynamic path with id1 -> %s\n", dynamicPath);
    }

    public String dynamicPath2(String dynamicPath1, String dynamicPath2) {
        return String.format("Dynamic path with id1 -> %s & id2 -> %s\n", dynamicPath1, dynamicPath2);
    }

    public String headerExtraction(String value) {
        return String.format("Request sent with the header \"test\":\"%s\"\n", value);
    }

    public String queryExtraction(String queryValue) {
        return String.format("Request sent with the query parameter \"test\":\"%s\"\n", queryValue);
    }

    public String queryExtraction(int queryValue1, int queryValue2) {
        return String.format("%d + %d = %d\n", queryValue1, queryValue2, queryValue1 + queryValue2);
    }
}
