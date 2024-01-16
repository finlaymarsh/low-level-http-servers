package com.fmarsh.demo.application;

import com.fmarsh.server.annotation.clazz.RestController;
import com.fmarsh.server.annotation.mapping.*;
import com.fmarsh.server.annotation.parameter.*;
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

    @PostMapping(path="/everything/{id}")
    public HttpResponse bringingItAllTogether(
            @Header("test") String testHeader,
            @Header("show-result") boolean showResultHeader,
            @PathVariable("id") short id,
            @QueryParam("num1") int queryParam1,
            @QueryParam("num2") long queryParam2,
            @RequestBody String body,
            @Request HttpRequest request
    ){
        String responseBody = demoService.everythingTest(testHeader, showResultHeader, id, queryParam1, queryParam2, body, request);
        return responseBuilder.build200Response(responseBody);
    }

    @GetMapping(path="/obi-wan")
    public HttpResponse helloThere() {
        return responseBuilder.build200Response(demoService.obiWan());
    }

    @PatchMapping(path="/obi-wan")
    public HttpResponse helloTherePatch() {
        return responseBuilder.build200Response(demoService.obiWan());
    }

    @PutMapping(path="/obi-wan")
    public HttpResponse helloTherePut() {
        return responseBuilder.build200Response(demoService.obiWan());
    }

    @DeleteMapping(path="/obi-wan")
    public HttpResponse deleteTest() {
        return responseBuilder.build200Response(demoService.deleteEndpoint());
    }

    @PostMapping(path="/obi-wan")
    public HttpResponse postTest(@RequestBody String requestBody) {
        return responseBuilder.build200Response(demoService.postEndpoint(requestBody));
    }

    @GetMapping(path="/anakin")
    public HttpResponse highGround() {
        return responseBuilder.build200Response(demoService.anakin());
    }

    @GetMapping(path="/test/{id1}")
    public HttpResponse dynamicPathVariables(@PathVariable("id1") String id) {
        return responseBuilder.build200Response(demoService.dynamicPath1(id));
    }

    @GetMapping(path="/test/{id1}/{id2}")
    public HttpResponse dynamicPathVariables2(@PathVariable("id1") String id1, @PathVariable("id2") String id2) {
        return responseBuilder.build200Response(demoService.dynamicPath2(id1, id2));
    }

    @GetMapping(path="/header-test")
    public HttpResponse invokeMethodWithRequest(@Request HttpRequest request) {
        String headerValue = request.getRequestHeaders().get("test").get(0);
        String responseBody = demoService.headerExtraction(headerValue);
        return responseBuilder.build200Response(responseBody);
    }

    @GetMapping(path="/header-test-2")
    public HttpResponse invokeMethodWithHeader(@Header("test") String testHeaderValue) {
        return responseBuilder.build200Response(demoService.headerExtraction(testHeaderValue));
    }

    @GetMapping(path="/query-param-test")
    public HttpResponse invokeMethodWithQueryParameter(@QueryParam("test") String queryParam) {
        return responseBuilder.build200Response(demoService.queryExtraction(queryParam));
    }

    @GetMapping(path="/add")
    public HttpResponse invokeMethodWithQueryParameter(@QueryParam("test1") int queryParam1, @QueryParam("test2") int queryParam2) {
        return responseBuilder.build200Response(demoService.queryExtraction(queryParam1, queryParam2));
    }
}

