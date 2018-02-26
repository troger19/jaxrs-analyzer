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

package com.sebastian_daschner.jaxrs_analyzer.analysis.results.testclasses.typeanalyzer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.sebastian_daschner.jaxrs_analyzer.analysis.results.TypeUtils;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypedMetadata;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeIdentifier;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.TypeRepresentation;
import com.sebastian_daschner.jaxrs_analyzer.model.rest.XMLMetadata;

/**
 * Test for capture of basic metadata for XML representation. 
 */
@XmlType(namespace = "http://foo.bar/ns-1", name = "test-class-31")
@XmlAccessorType(XmlAccessType.FIELD)
public class TestClass31 {
    @XmlElement(name="property-1", namespace = "http://foo.bar/ns-2")
    private String property1;
    
    @XmlAttribute(name="property-2", namespace = "http://foo.bar/ns-1")
    private String property2;
    
    @XmlAttribute
    private String property3;
    
    public static Set<TypeRepresentation> expectedTypeRepresentations() {
        final Map<String, TypedMetadata> properties = new HashMap<>();

        XMLMetadata property1Metadata = new XMLMetadata("http://foo.bar/ns-2", "property-1", null, null);
        properties.put("property1", new TypedMetadata(TypeUtils.STRING_IDENTIFIER, property1Metadata));
        
        XMLMetadata property2Metadata = new XMLMetadata("http://foo.bar/ns-1", "property-2", true);
        properties.put("property2", new TypedMetadata(TypeUtils.STRING_IDENTIFIER, property2Metadata));

        XMLMetadata property3Metadata = new XMLMetadata(null, null, true);
        properties.put("property3", new TypedMetadata(TypeUtils.STRING_IDENTIFIER, property3Metadata));

        XMLMetadata typeXmlMetadata = new XMLMetadata("http://foo.bar/ns-1", "test-class-31");
        return Collections.singleton(TypeRepresentation.ofConcrete(expectedIdentifier(), typeXmlMetadata , properties));
    }

    public static TypeIdentifier expectedIdentifier() {
        return TypeIdentifier.ofType("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/results/testclasses/typeanalyzer/TestClass31;");
    }

}
