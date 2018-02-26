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

package com.sebastian_daschner.jaxrs_analyzer.backend.swagger;

import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation.ConcreteTypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentationVisitor;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.XMLMetadata;
import com.sebastian_daschner.jaxrs_analyzer.utils.Pair;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.HashMap;
import java.util.Map;

import static com.sebastian_daschner.jaxrs_analyzer.backend.ComparatorUtils.mapKeyComparator;
import static com.sebastian_daschner.jaxrs_analyzer.model.Types.*;

/**
 * Creates Swagger schema type definitions.
 *
 * @author Sebastian Daschner
 */
class SchemaBuilder {

    /**
     * The fully-qualified class name together with the JSON definitions identified by the definition names.
     */
    private final Map<String, Pair<String, JsonObject>> jsonDefinitions = new HashMap<>();

    /**
     * All known representation defined in the REST resources
     */
    private final Map<TypeIdentifier, TypeRepresentation> typeRepresentations;

    SchemaBuilder(final Map<TypeIdentifier, TypeRepresentation> typeRepresentations) {
        this.typeRepresentations = typeRepresentations;
    }

    /**
     * Creates the schema object builder for the identifier.
     * The actual definitions are retrieved via {@link SchemaBuilder#getDefinitions} after all types have been declared.
     *
     * @param identifier The identifier
     * @return The schema JSON object builder with the needed properties
     */
    JsonObjectBuilder build(final TypeIdentifier identifier) {
        final SwaggerType type = toSwaggerType(identifier.getType());
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        switch (type) {
            case BOOLEAN:
            case INTEGER:
            case NUMBER:
            case NULL:
            case STRING:
                addPrimitive(builder, type);
                return builder;
        }

        final TypeRepresentationVisitor visitor = new TypeRepresentationVisitor() {

            private boolean inCollection = false;

            @Override
            public void visit(final TypeRepresentation.ConcreteTypeRepresentation representation) {
                final JsonObjectBuilder nestedBuilder = inCollection ? Json.createObjectBuilder() : builder;
                add(nestedBuilder, representation);

                if (inCollection) {
                    builder.add("items", nestedBuilder.build());
                }
            }

            @Override
            public void visit(final TypeRepresentation.CollectionTypeRepresentation representation) {
                builder.add("type", "array");
                inCollection = true;
            }

            @Override
            public void visit(final TypeRepresentation.EnumTypeRepresentation representation) {
                builder.add("type", "string");
                if (!representation.getEnumValues().isEmpty()) {
                    final JsonArrayBuilder array = representation.getEnumValues().stream().sorted().collect(Json::createArrayBuilder, JsonArrayBuilder::add, JsonArrayBuilder::add);
                    builder.add("enum", array);
                }
            }
        };

        final TypeRepresentation representation = typeRepresentations.get(identifier);
        if (representation == null)
            builder.add("type", "object");
        else
            representation.accept(visitor);
        return builder;
    }

    /**
     * Returns the stored schema definitions. This has to be called after all calls of {@link SchemaBuilder#build(TypeIdentifier)}.
     *
     * @return The schema JSON definitions
     */
    JsonObject getDefinitions() {
        final JsonObjectBuilder builder = Json.createObjectBuilder();
        jsonDefinitions.entrySet().stream().sorted(mapKeyComparator()).forEach(e -> builder.add(e.getKey(), e.getValue().getRight()));
        return builder.build();
    }

    private void add(final JsonObjectBuilder builder, final TypeRepresentation.ConcreteTypeRepresentation representation) {
        final SwaggerType type = toSwaggerType(representation.getIdentifier().getType());
        switch (type) {
            case BOOLEAN:
            case INTEGER:
            case NUMBER:
            case NULL:
            case STRING:
                addPrimitive(builder, type);
                return;
        }

        addObject(builder, representation);
    }

    private void addObject(final JsonObjectBuilder builder, final ConcreteTypeRepresentation typeRepresentation) {
        TypeIdentifier identifier = typeRepresentation.getIdentifier();
        final String definition = buildDefinition(identifier.getName());

        if (jsonDefinitions.containsKey(definition)) {
            builder.add("$ref", "#/definitions/" + definition);
            return;
        }

        // reserve definition
        jsonDefinitions.put(definition, Pair.of(identifier.getName(), Json.createObjectBuilder().build()));

        final JsonObjectBuilder definitionBuilder = Json.createObjectBuilder();
        
        final JsonObjectBuilder nestedBuilder = Json.createObjectBuilder();

        Map<String, TypeIdentifier> properties = typeRepresentation.getProperties();
        final XMLMetadata typeXmlMetadata = typeRepresentation.getTypeXmlMetadata();
        properties.entrySet().stream().forEach(e -> {
            final String propertyName = e.getKey();
            final TypeIdentifier propertyTypeIdentifier = e.getValue();
            final JsonObjectBuilder propertyBuilder = build(propertyTypeIdentifier);
            XMLMetadata propertyXmlMetadata = typeRepresentation.getPropertyXmlMetadata(propertyName);
            renderXmlMetadata(propertyBuilder, typeXmlMetadata, propertyXmlMetadata);
            nestedBuilder.add(propertyName, propertyBuilder);   
        });
        definitionBuilder.add("properties", nestedBuilder.build());
        renderXmlMetadata(definitionBuilder, typeXmlMetadata, null);
        jsonDefinitions.put(definition, Pair.of(identifier.getName(), definitionBuilder.build()));

        builder.add("$ref", "#/definitions/" + definition);
    }

    private void addPrimitive(final JsonObjectBuilder builder, final SwaggerType type) {
        builder.add("type", type.toString());
    }
    
    private void renderXmlMetadata(JsonObjectBuilder builder, XMLMetadata typeXmlMetadata, XMLMetadata propertyXmlMetadata) {
        final JsonObjectBuilder xmlObjectBuilder = Json.createObjectBuilder();
        String namespace = null;
        String prefix = null;
        String name = null;
        Boolean attribute = null;
        
        if (typeXmlMetadata != null) {
            namespace = typeXmlMetadata.getNamespace();
            name = typeXmlMetadata.getName();
            prefix = typeXmlMetadata.getPrefix();
        }
        if (propertyXmlMetadata != null) {
//            if (propertyXmlMetadata.getNamespace() != null)
                namespace = propertyXmlMetadata.getNamespace();
            if (propertyXmlMetadata.getName() != null)
                name = propertyXmlMetadata.getName();
            if (propertyXmlMetadata.isAttribute())
                attribute = true;
//            if (propertyXmlMetadata.getPrefix() != null)
                prefix = propertyXmlMetadata.getPrefix();
        }
        if (namespace != null || prefix != null || name != null || attribute != null) {
            if (namespace != null)
                xmlObjectBuilder.add("namespace", namespace);
            if (name != null)
                xmlObjectBuilder.add("name", name);
            if (prefix != null)
                xmlObjectBuilder.add("prefix", prefix);
            if (attribute != null)
                xmlObjectBuilder.add("attribute", attribute);
            builder.add("xml", xmlObjectBuilder.build());
        }
    }

    private String buildDefinition(final String typeName) {
        final String definition = typeName.startsWith(TypeIdentifier.DYNAMIC_TYPE_PREFIX) ? "JsonObject" :
                typeName.substring(typeName.lastIndexOf('/') + 1, typeName.length() - 1);

        final Pair<String, JsonObject> containedEntry = jsonDefinitions.get(definition);
        if (containedEntry == null || containedEntry.getLeft() != null && containedEntry.getLeft().equals(typeName))
            return definition;

        if (!definition.matches("_\\d+$"))
            return definition + "_2";

        final int separatorIndex = definition.lastIndexOf('_');
        final int index = Integer.parseInt(definition.substring(separatorIndex + 1));
        return definition.substring(0, separatorIndex + 1) + (index + 1);
    }

    /**
     * Converts the given Java type to the Swagger JSON type.
     *
     * @param type The Java type definition
     * @return The Swagger type
     */
    private static SwaggerType toSwaggerType(final String type) {
        if (INTEGER_TYPES.contains(type))
            return SwaggerType.INTEGER;

        if (DOUBLE_TYPES.contains(type))
            return SwaggerType.NUMBER;

        if (BOOLEAN.equals(type) || PRIMITIVE_BOOLEAN.equals(type))
            return SwaggerType.BOOLEAN;

        if (STRING.equals(type))
            return SwaggerType.STRING;

        return SwaggerType.OBJECT;
    }

    /**
     * Represents the different Swagger types.
     *
     * @author Sebastian Daschner
     */
    private enum SwaggerType {

        ARRAY, BOOLEAN, INTEGER, NULL, NUMBER, OBJECT, STRING;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

}
