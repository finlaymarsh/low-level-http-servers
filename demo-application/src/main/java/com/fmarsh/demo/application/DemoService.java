package com.fmarsh.demo.application;

import com.fmarsh.server.model.HttpResponse;

import java.util.List;

public class DemoService {
    final ResponseBuilder responseBuilder;

    public DemoService(ResponseBuilder responseBuilder) {
        this.responseBuilder = responseBuilder;
    }

    public String obiWan() {
        return "Hello There!\n";
    }

    public String anakin() {
        return "Hello There!\n";
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

    public HttpResponse headerExtraction(Object testHeaderValue) {
        // Todo need much better type conversion for headers

        List<?> value;
        String firstValue;

        if (testHeaderValue instanceof  List<?> list) {
            value = list;
        } else {
            return responseBuilder.build400Response("Mandatory request header {\"test\"} not present\n");
        }

        if (value.size() == 0) {
            return responseBuilder.build400Response("Mandatory request header {\"test\"} cannot be empty\n");
        }

        if (value.get(0) instanceof String stringValue) {
            firstValue = stringValue;
        } else {
            return responseBuilder.build400Response("Some serialization issue\n");

        }
        return responseBuilder.build200Response(String.format("Request sent with the header \"test\":\"%s\"\n", firstValue));
    }
}
