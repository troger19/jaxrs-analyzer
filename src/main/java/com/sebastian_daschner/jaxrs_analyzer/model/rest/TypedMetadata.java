package com.sebastian_daschner.jaxrs_analyzer.model.rest;

public class TypedMetadata {
    private TypeIdentifier typeIdentifier;
    private XMLMetadata xmlMetadata;
    public TypedMetadata(TypeIdentifier typeIdentifier, XMLMetadata xmlMetadata) {
        this.typeIdentifier = typeIdentifier;
        this.xmlMetadata = xmlMetadata;
    }
    public TypeIdentifier getTypeIdentifier() {
        return typeIdentifier;
    }
    public XMLMetadata getXmlMetadata() {
        return xmlMetadata;
    }
}
