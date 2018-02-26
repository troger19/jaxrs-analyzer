package com.sebastian_daschner.jaxrs_analyzer.model.rest;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;

import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.getAnnotation;
import static com.sebastian_daschner.jaxrs_analyzer.model.JavaUtils.isAnnotationPresent;

public class XMLMetadata {
    private String namespace;
    private String prefix;
    private String name;
    private Boolean attribute;

    public XMLMetadata(String namespace, String name) {
        this(namespace, name, null, null);
    }

    public XMLMetadata(String namespace, String name, boolean attribute) {
        this(namespace, name, attribute, null);
    }
    
    public XMLMetadata(String namespace, String name, Boolean attribute, String prefix) {
        this.namespace = namespace;
        this.name = name;
        this.attribute = attribute;
        this.prefix = prefix;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isAttribute() {
        return attribute != null && Boolean.TRUE.equals(attribute);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        XMLMetadata other = (XMLMetadata) obj;
        if (attribute == null) {
            if (other.attribute != null)
                return false;
        } else if (!attribute.equals(other.attribute))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        if (prefix == null) {
            if (other.prefix != null)
                return false;
        } else if (!prefix.equals(other.prefix))
            return false;
        return true;
    }

    public static XMLMetadata extract(final AnnotatedElement annotatedElement) {
        XMLMetadata xmlMetadata = null;
        Boolean attribute = null;
        String namespace = null;
        String name = null;
        String prefix = null;
        if (isAnnotationPresent(annotatedElement, XmlElement.class)) {
            XmlElement asXmlElement = getAnnotation(annotatedElement, XmlElement.class);
//            namespace = "##default".equals(asXmlElement.namespace()) ? null : asXmlElement.namespace();
            name = "##default".equals(asXmlElement.name()) ? null : asXmlElement.name();
        } else if (isAnnotationPresent(annotatedElement, XmlAttribute.class)) {
            XmlAttribute asXmlAttribute = getAnnotation(annotatedElement, XmlAttribute.class);
//            namespace = "##default".equals(asXmlAttribute.namespace()) ? null : asXmlAttribute.namespace();
            name = "##default".equals(asXmlAttribute.name()) ? null : asXmlAttribute.name();
            attribute = true;
        } else if (isAnnotationPresent(annotatedElement, XmlRootElement.class)) {
            XmlRootElement asXmlRootElement = getAnnotation(annotatedElement, XmlRootElement.class);
            namespace = "##default".equals(asXmlRootElement.namespace()) ? null : asXmlRootElement.namespace();
            name = "##default".equals(asXmlRootElement.name()) ? null : asXmlRootElement.name();
            prefix = "ns";
        } else if (isAnnotationPresent(annotatedElement, XmlType.class)) {
            XmlType asXmlType = getAnnotation(annotatedElement, XmlType.class);
//            namespace = "##default".equals(asXmlType.namespace()) ? null : asXmlType.namespace();
            name = "##default".equals(asXmlType.name()) ? null : decapitalize(asXmlType.name());
        }
        if (name == null) {
            if (annotatedElement instanceof Class) {
                name = ((Class) annotatedElement).getName();
            } else if (annotatedElement instanceof Field) {
                name = ((Field) annotatedElement).getName();
            } else {
                name = null;
            }
        }
        if (namespace != null || name != null || attribute != null)
            xmlMetadata = new XMLMetadata(namespace, name, attribute, prefix);
        return xmlMetadata;
    }
    
    @Override
    public String toString() {
        return this.namespace + "|" + this.name + "|" + this.attribute + "|" + this.prefix;
    }

    private static String decapitalize(String name) {
        if (name != null && name.length() != 0) {
            if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) && Character.isUpperCase(name.charAt(0))) {
                return name;
            } else {
                char[] chars = name.toCharArray();
                chars[0] = Character.toLowerCase(chars[0]);
                return new String(chars);
            }
        } else {
            return name;
        }
    }

}
