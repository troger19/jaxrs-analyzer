package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.testclasses.resource.response;

import java.util.Collections;
import java.util.Set;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

public abstract class TestClass63 {
    @javax.ws.rs.GET
    public abstract User method();
    
    public static Set<HttpResponse> getResult() {
        final HttpResponse result = new HttpResponse();
        result.getEntityTypes().add("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/classes/testclasses/resource/response/TestClass63$User;");
        return Collections.singleton(result);
    }
    
    private static class User {
        private String name;
    }
}
