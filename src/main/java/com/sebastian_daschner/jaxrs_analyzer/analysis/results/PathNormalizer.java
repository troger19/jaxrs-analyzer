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

package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import com.sebastian_daschner.jaxrs_analyzer.model.results.ClassResult;
import com.sebastian_daschner.jaxrs_analyzer.model.results.MethodResult;
import com.sebastian_daschner.jaxrs_analyzer.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Normalizes the JAX-RS paths.
 *
 * @author Sebastian Daschner
 */
final class PathNormalizer {

    private PathNormalizer() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the normalized application path found in any of the given class results.
     *
     * @return The base URI of the application
     */
    static String getApplicationPath(final Set<ClassResult> classResults) {
        return classResults.stream().map(ClassResult::getApplicationPath).filter(Objects::nonNull)
                .map(PathNormalizer::normalize).findAny().orElse("");
    }

    /**
     * Returns the normalized path (without forward-slashes at the beginning or the end) of the given method result
     * including all parent class resources.
     *
     * @param methodResult The method result
     * @return The normalized full path of the method
     */
    static String getPath(final MethodResult methodResult) {
        final List<String> paths = determinePaths(methodResult);

        return paths.stream().map(PathNormalizer::normalize).collect(Collectors.joining("/"));
    }

    /**
     * Determines all single paths of the method result recursively. All parent class and method results are analyzed as well.
     *
     * @param methodResult The method result
     * @return All single path pieces
     */
    private static List<String> determinePaths(final MethodResult methodResult) {
        final List<String> paths = new LinkedList<>();
        MethodResult currentMethod = methodResult;

        while (true) {
            addNonBlank(currentMethod.getPath(), paths);
            final ClassResult parentClass = currentMethod.getParentResource();

            if (parentClass == null)
                break;

            currentMethod = parentClass.getParentSubResourceLocator();
            if (currentMethod == null) {
                addNonBlank(parentClass.getResourcePath(), paths);
                break;
            }
        }

        Collections.reverse(paths);
        return paths;
    }

    /**
     * Adds the string to the list if it is not blank.
     *
     * @param string  The string to add
     * @param strings The list
     */
    private static void addNonBlank(final String string, final List<String> strings) {
        if (!StringUtils.isBlank(string) && !"/".equals(string))
            strings.add(string);
    }

    /**
     * Normalizes the given path, i.e. trims leading or trailing forward-slashes and removes path parameter matchers.
     *
     * @param path The path to normalize
     * @return The normalized path
     */
    private static String normalize(final String path) {
        final StringBuilder builder = new StringBuilder(path);

        int index = 0;
        int colonIndex = -1;
        char current = 0;
        char last;

        while ((index > -1) && (index < builder.length())) {
            last = current;
            current = builder.charAt(index);

            switch (current) {
                case '}':
                    if (last == '\\')
                        break;
                    if (colonIndex != -1) {
                        builder.delete(colonIndex, index);
                        index = colonIndex;
                        colonIndex = -1;
                    }
                    break;
                case ':':
                    if (colonIndex == -1) {
                        colonIndex = index;
                    }
                    break;
            }

            if ((index == 0 || index == builder.length() - 1) && current == '/') {
                builder.deleteCharAt(index);
                if (index == builder.length()) {
                    // check again for path end
                    index--;
                }
            } else {
                index++;
            }
        }

        return builder.toString();
    }

}
