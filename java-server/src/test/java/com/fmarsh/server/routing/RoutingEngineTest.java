package com.fmarsh.server.routing;

import com.fmarsh.server.model.HttpMethod;

import com.fmarsh.server.exception.DuplicateRouteDefinitionException;
import com.fmarsh.server.exception.InvalidPathException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RoutingEngineTest {

    private final RouteEngine routeEngine = RouteEngine.getInstance();

    @BeforeEach
    public void setup() {
        routeEngine.flushRoutes();
    }

    // TODO make this legal
    @Test
    void throwsInvalidPathException_when_registeringEmptyPath() {
       Exception exception = assertThrows(InvalidPathException.class, () ->
               routeEngine.addRoute(HttpMethod.GET, "/", new RouteDefinition(null, null))
       );
       assertEquals("Empty path", exception.getMessage());
    }

    @Test
    void canRegisterSimplePath() {
        Node node = routeEngine.addRoute(HttpMethod.GET, "/foo", new RouteDefinition(null, null));
        assertEquals("GET /foo", RoutingHelper.getPathFrom(node));
    }

    @Test
    void canRegisterMoreSimplePath2() {
        Node node = routeEngine.addRoute(HttpMethod.GET, "/foo/bar", new RouteDefinition(null, null));
        assertEquals("GET /foo/bar", RoutingHelper.getPathFrom(node));
    }


    @Test
    void canRegisterSimplePath3() {
        Node node = routeEngine.addRoute(HttpMethod.GET, "/foo/bar/", new RouteDefinition(null, null));
        assertEquals("GET /foo/bar", RoutingHelper.getPathFrom(node));
    }

    @Test
    void canRegisterMultipleSimplePaths() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo/bar1", new RouteDefinition(null, null));
        Node node2 = routeEngine.addRoute(HttpMethod.GET, "/foo/bar2", new RouteDefinition(null, null));
        assertEquals("GET /foo/bar1", RoutingHelper.getPathFrom(node1));
        assertEquals("GET /foo/bar2", RoutingHelper.getPathFrom(node2));
    }

    @Test
    void canRegisterMultipleSimplePaths2() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo", new RouteDefinition(null, null));
        Node node2 = routeEngine.addRoute(HttpMethod.GET, "/foo/bar", new RouteDefinition(null, null));
        assertEquals("GET /foo", RoutingHelper.getPathFrom(node1));
        assertEquals("GET /foo/bar", RoutingHelper.getPathFrom(node2));
    }

    @Test
    void canRegisterMultipleSimplePaths3() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo", new RouteDefinition(null, null));
        Node node2 = routeEngine.addRoute(HttpMethod.GET, "/foo/bar", new RouteDefinition(null, null));
        Node node3 = routeEngine.addRoute(HttpMethod.GET, "/foo/ba", new RouteDefinition(null, null));
        Node node4 = routeEngine.addRoute(HttpMethod.GET, "/foo/bar/soap", new RouteDefinition(null, null));
        Node node5 = routeEngine.addRoute(HttpMethod.GET, "/foo/ba/toast", new RouteDefinition(null, null));
        Node node6 = routeEngine.addRoute(HttpMethod.GET, "/foo/bar/home/top", new RouteDefinition(null, null));

        assertEquals("GET /foo", RoutingHelper.getPathFrom(node1));
        assertEquals("GET /foo/bar", RoutingHelper.getPathFrom(node2));
        assertEquals("GET /foo/ba", RoutingHelper.getPathFrom(node3));
        assertEquals("GET /foo/bar/soap", RoutingHelper.getPathFrom(node4));
        assertEquals("GET /foo/ba/toast", RoutingHelper.getPathFrom(node5));
        assertEquals("GET /foo/bar/home/top", RoutingHelper.getPathFrom(node6));
    }

    @Test
    void throwsInvalidPathException_when_registeringSamePathTwice() {
        routeEngine.addRoute(HttpMethod.GET, "/foo/bar", new RouteDefinition(null, null));
        Exception exception = assertThrows(DuplicateRouteDefinitionException.class, () ->
                routeEngine.addRoute(HttpMethod.GET, "/foo/bar", new RouteDefinition(null, null))
        );
        assertEquals("The route: [GET /foo/bar] is defined multiple times.", exception.getMessage());
    }

    @Test
    void throwsInvalidPathException_when_registeringSamePathTwice2() {
        routeEngine.addRoute(HttpMethod.GET, "/foo/bar", new RouteDefinition(null, null));
        Exception exception = assertThrows(DuplicateRouteDefinitionException.class, () ->
                routeEngine.addRoute(HttpMethod.GET, "/foo/bar/", new RouteDefinition(null, null))
        );
        assertEquals("The route: [GET /foo/bar] is defined multiple times.", exception.getMessage());
    }

    @Test
    void throwsInvalidPathException_when_registeringSamePathTwice3() {
        routeEngine.addRoute(HttpMethod.GET, "/foo/{id}", new RouteDefinition(null, null));
        Exception exception = assertThrows(DuplicateRouteDefinitionException.class, () ->
                routeEngine.addRoute(HttpMethod.GET, "/foo/{id}", new RouteDefinition(null, null))
        );
        assertEquals("The route: [GET /foo/{id}] is defined multiple times.", exception.getMessage());
    }

    @Test
    void throwsInvalidPathException_when_registeringTwoWildcardPathsOnSameNode() {
        routeEngine.addRoute(HttpMethod.GET, "/foo/{id1}", new RouteDefinition(null, null));
        Exception exception = assertThrows(DuplicateRouteDefinitionException.class, () ->
                routeEngine.addRoute(HttpMethod.GET, "/foo/{id2}", new RouteDefinition(null, null))
        );
        assertEquals("Path collision detected: [GET /foo/*here*].", exception.getMessage());
    }

    @Test
    void throwsInvalidPathException_when_registeringOneWildcardOneSimplePathOnSameNode() {
        routeEngine.addRoute(HttpMethod.GET, "/foo/{id1}", new RouteDefinition(null, null));
        Exception exception = assertThrows(DuplicateRouteDefinitionException.class, () ->
                routeEngine.addRoute(HttpMethod.GET, "/foo/test", new RouteDefinition(null, null))
        );
        assertEquals("Path collision detected: [GET /foo/*here*].", exception.getMessage());
    }

    @Test
    void throwsInvalidPathException_when_registeringOneWildcardOneSimplePathOnSameNode2() {
        routeEngine.addRoute(HttpMethod.GET, "/foo/{id1}/2", new RouteDefinition(null, null));
        Exception exception = assertThrows(DuplicateRouteDefinitionException.class, () ->
                routeEngine.addRoute(HttpMethod.GET, "/foo/test/2", new RouteDefinition(null, null))
        );
        assertEquals("Path collision detected: [GET /foo/*here*].", exception.getMessage());
    }


    @Test
    void canRegisterMultipleComplexPaths() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo/{id}", new RouteDefinition(null, null));
        Node node2 = routeEngine.addRoute(HttpMethod.GET, "/foo/{id}/test1", new RouteDefinition(null, null));
        Node node3 = routeEngine.addRoute(HttpMethod.GET, "/foo/{id}/test2", new RouteDefinition(null, null));
        assertEquals("GET /foo/{id}", RoutingHelper.getPathFrom(node1));
        assertEquals("GET /foo/{id}/test1", RoutingHelper.getPathFrom(node2));
        assertEquals("GET /foo/{id}/test2", RoutingHelper.getPathFrom(node3));
    }


    @Test
    void canFindRouteDefinitionForSimplePath() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo", new RouteDefinition("Some random object", null));
        Optional<RouteDefinition> result = routeEngine.findRouteDefinition(HttpMethod.GET, "/foo");
        assertTrue(result.isPresent());
        assertEquals(new RouteDefinition("Some random object", null), result.get());
    }

    @Test
    void canFindRouteDefinitionForSimplePath2() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/bar", new RouteDefinition("Some random object", null));
        Optional<RouteDefinition> result = routeEngine.findRouteDefinition(HttpMethod.GET, "/foo");
        assertFalse(result.isPresent());
    }

    @Test
    void canFindWildcardMatch() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo/{id}", new RouteDefinition("Some random object", null));
        Optional<RouteDefinition> result = routeEngine.findRouteDefinition(HttpMethod.GET, "/foo");
        assertFalse(result.isPresent());
    }

    @Test
    void canFindWildcardMatch1() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo/{id}", new RouteDefinition("Some random object", null));
        Optional<RouteDefinition> result = routeEngine.findRouteDefinition(HttpMethod.GET, "/foo/this-is-wild");
        assertTrue(result.isPresent());
        assertEquals(new RouteDefinition("Some random object", null), result.get());
    }

    @Test
    void canFindWildcardMatch2() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo/{id}", new RouteDefinition("Some random object", null));
        Optional<RouteDefinition> result = routeEngine.findRouteDefinition(HttpMethod.GET, "/foo/this-is-wild/test");
        assertFalse(result.isPresent());
    }

    @Test
    void canFindWildcardMatch3() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo/{id1}/{id2}", new RouteDefinition("Some random object", null));
        Optional<RouteDefinition> result = routeEngine.findRouteDefinition(HttpMethod.GET, "/foo/this-is-wild/test");
        assertTrue(result.isPresent());
        assertEquals(new RouteDefinition("Some random object", null), result.get());
    }

    @Test
    void canFindWildcardMatch4() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo/{id1}/test/{id2}", new RouteDefinition("Some random object", null));
        Optional<RouteDefinition> result = routeEngine.findRouteDefinition(HttpMethod.GET, "/foo/this-is-wild/nope/something");
        assertFalse(result.isPresent());
    }

    @Test
    void canFindWildcardMatch5() {
        Node node1 = routeEngine.addRoute(HttpMethod.GET, "/foo/{id1}/test/{id2}", new RouteDefinition("Some random object", null));
        Optional<RouteDefinition> result = routeEngine.findRouteDefinition(HttpMethod.GET, "/foo/this-is-wild/test/something");
        assertTrue(result.isPresent());
        assertEquals(new RouteDefinition("Some random object", null), result.get());
    }

    @Test
    void findWildCardMatch() {
        routeEngine.addRoute(HttpMethod.GET, "/foo/{id}", new RouteDefinition("Some random object", null));
        Optional<String> result = routeEngine.findWildcardMatch(HttpMethod.GET, "/foo/test-value", "id");
        assertTrue(result.isPresent());
        assertEquals("test-value", result.get());
    }

    @Test
    void findWildCardMatch2() {
        routeEngine.addRoute(HttpMethod.GET, "/foo/{id}/something-more", new RouteDefinition("Some random object", null));
        Optional<String> result = routeEngine.findWildcardMatch(HttpMethod.GET, "/foo/test-value/something-more", "id");
        assertTrue(result.isPresent());
        assertEquals("test-value", result.get());
    }

    @Test
    void findWildCardMatch3() {
        routeEngine.addRoute(HttpMethod.GET, "/foo/{id1}/{id2}", new RouteDefinition("Some random object", null));
        Optional<String> result = routeEngine.findWildcardMatch(HttpMethod.GET, "/foo/test-value/something-more", "id2");
        assertTrue(result.isPresent());
        assertEquals("something-more", result.get());
    }

    @Test
    void findWildCardMatchFails() {
        routeEngine.addRoute(HttpMethod.GET, "/foo/{id1}/{id2}", new RouteDefinition("Some random object", null));
        Optional<String> result = routeEngine.findWildcardMatch(HttpMethod.GET, "/foo/test-value/something-more", "id");
        assertFalse(result.isPresent());
    }
}
