package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.testclasses.resource.object;

import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.*;

public class TestClass8 {

    @javax.ws.rs.GET public List<Model> method() {
        if ("".equals(""))
            return handleResponse();
        return new LinkedList<>();
    }

    private List<Model> handleResponse() {
        throw new WebApplicationException(Response.Status.NOT_FOUND);
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse httpResponse = new HttpResponse();
        httpResponse.getEntityTypes().add("Ljava/util/List<Lcom/sebastian_daschner/jaxrs_analyzer/analysis/classes/testclasses/resource/object/TestClass8$Model;>;");

        final HttpResponse notFoundResponse = new HttpResponse();
        notFoundResponse.getStatuses().add(404);

        return new HashSet<>(Arrays.asList(httpResponse, notFoundResponse));
    }

    private class Model {
        public Model(final String string) {
        }
    }

}
