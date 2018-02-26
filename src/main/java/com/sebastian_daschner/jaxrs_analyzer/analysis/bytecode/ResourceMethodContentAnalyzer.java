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

package com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode;

import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodPool;
import com.sebastian_daschner.jaxrs_analyzer.analysis.bytecode.simulation.MethodSimulator;
import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.Element;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.JsonValue;
import com.sebastian_daschner.jaxrs_analyzer.model.instructions.Instruction;
import com.sebastian_daschner.jaxrs_analyzer.model.methods.ProjectMethod;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Analyzes JAX-RS resource methods. This class is thread-safe.
 *
 * @author Sebastian Daschner
 */
class ResourceMethodContentAnalyzer extends MethodContentAnalyzer {

    private final Lock lock = new ReentrantLock();

    /**
     * Analyzes the method (including own project methods).
     *
     * @param methodResult The method result
     */
    void analyze(final MethodResult methodResult) {
        lock.lock();
        try {
            buildPackagePrefix(methodResult.getParentResource().getOriginalClass());

            final List<Instruction> visitedInstructions = interpretRelevantInstructions(methodResult.getInstructions());

            // find project defined methods in invoke occurrences
            final Set<ProjectMethod> projectMethods = findProjectMethods(visitedInstructions);

            // add project methods to global method pool
            projectMethods.forEach(MethodPool.getInstance()::addProjectMethod);

            Element returnedElement = new MethodSimulator().simulate(visitedInstructions);
            final String returnType = methodResult.getOriginalMethodSignature().getReturnType();

            // void resource methods are interpreted later; stop analyzing on error
            if (Types.PRIMITIVE_VOID.equals(returnType)) {
                return;
            }
            
            // happens for abstract methods or if there is no return
            boolean hasReturnElement = returnedElement != null;

            final Set<Object> possibleObjects = hasReturnElement ? returnedElement.getPossibleValues().stream().filter(o -> !(o instanceof HttpResponse))
                    .collect(Collectors.toSet()) : Collections.emptySet();

            // for non-Response methods add a default if there are non-Response objects or none objects at all
            if (!Types.RESPONSE.equals(returnType)) {
                final HttpResponse defaultResponse = new HttpResponse();

                if (Types.OBJECT.equals(returnType))
                    defaultResponse.getEntityTypes().addAll(returnedElement.getTypes());
                else
                    defaultResponse.getEntityTypes().add(returnType);

                possibleObjects.stream().filter(o -> o instanceof JsonValue).map(o -> (JsonValue) o).forEach(defaultResponse.getInlineEntities()::add);

                methodResult.getResponses().add(defaultResponse);
            }

            // add Response results as well
            if (hasReturnElement)
                returnedElement.getPossibleValues().stream().filter(o -> o instanceof HttpResponse).map(o -> (HttpResponse) o).forEach(methodResult.getResponses()::add);
        } finally {
            lock.unlock();
        }
    }

}
