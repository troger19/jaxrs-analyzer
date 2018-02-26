/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.builder;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.Response;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;

import java.util.stream.Stream;

/**
 * @author Sebastian Daschner
 */
public class ResponseBuilder {

    private final Response response;

    private ResponseBuilder(final Response response) {
        this.response = response;
    }

    public static ResponseBuilder newBuilder() {
        return new ResponseBuilder(new Response());
    }

    public static ResponseBuilder withResponseBody(final TypeIdentifier responseBody) {
        return new ResponseBuilder(new Response(responseBody));
    }

    public ResponseBuilder andHeaders(final String... headers) {
        Stream.of(headers).forEach(response.getHeaders()::add);
        return this;
    }

    public Response build() {
        return response;
    }

}
