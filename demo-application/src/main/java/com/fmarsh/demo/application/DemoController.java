package com.fmarsh.demo.application;

import com.fmarsh.server.annotation.*;
import com.fmarsh.server.model.HttpRequest;
import com.fmarsh.server.model.HttpResponse;


@RestController
public class DemoController {

    private final DemoService demoService;
    private final ResponseBuilder responseBuilder;

    // Todo add support for injection by interface

    public DemoController(DemoService demoService, ResponseBuilder responseBuilder) {
        this.demoService = demoService;
        this.responseBuilder = responseBuilder;
    }

    @GetMapping(path="/everything/{id}")
    public HttpResponse bringingItAllTogether(
            @Header("test") String testHeader,
            @Header("show-result") boolean showResultHeader,
            @PathVariable("id") short id,
            @QueryParam("num1") int queryParam1,
            @QueryParam("num2") long queryParam2,
            @Request HttpRequest request
    ){
        String responseBody = demoService.everythingTest(testHeader, showResultHeader, id, queryParam1, queryParam2, request);
        return responseBuilder.build200Response(responseBody);
    }

    @GetMapping(path="/obi-wan")
    public HttpResponse helloThere() {
        String responseBody = demoService.obiWan();
        return responseBuilder.build200Response(responseBody);
    }

    @DeleteMapping(path="/obi-wan")
    public HttpResponse deleteTest() {
        String responseBody = "Delete endpoint hit!\n";
        return responseBuilder.build200Response(responseBody);
    }

    @GetMapping(path="/anakin")
    public HttpResponse highGround() {
        String responseBody = demoService.anakin();
        return responseBuilder.build200Response(responseBody);
    }

    @GetMapping(path="/test/{id1}")
    public HttpResponse dynamicPathVariables(@PathVariable("id1") String id) {
        String responseBody = demoService.dynamicPath1(id);
        return responseBuilder.build200Response(responseBody);
    }

    @GetMapping(path="/test/{id1}/{id2}")
    public HttpResponse dynamicPathVariables2(@PathVariable("id1") String id1, @PathVariable("id2") String id2) {
        String responseBody = demoService.dynamicPath2(id1, id2);
        return responseBuilder.build200Response(responseBody);
    }

    @GetMapping(path="/header-test")
    public HttpResponse invokeMethodWithRequest(@Request HttpRequest request) {
        String headerValue = request.getRequestHeaders().get("test").get(0);
        String responseBody = demoService.headerExtraction(headerValue);
        return responseBuilder.build200Response(responseBody);
    }

    @GetMapping(path="/header-test-2")
    public HttpResponse invokeMethodWithHeader(@Header("test") String testHeaderValue) {
        String responseBody = demoService.headerExtraction(testHeaderValue);
        return responseBuilder.build200Response(responseBody);
    }

    @GetMapping(path="/query-param-test")
    public HttpResponse invokeMethodWithQueryParameter(@QueryParam("test") String queryParam) {
        String responseBody = demoService.queryExtraction(queryParam);
        return responseBuilder.build200Response(responseBody);
    }

    @GetMapping(path="/add")
    public HttpResponse invokeMethodWithQueryParameter(@QueryParam("test1") int queryParam1, @QueryParam("test2") int queryParam2) {
        String responseBody = demoService.queryExtraction(queryParam1, queryParam2);
        return responseBuilder.build200Response(responseBody);
    }
}

