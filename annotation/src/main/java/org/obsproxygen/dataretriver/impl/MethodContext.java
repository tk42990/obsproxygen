package org.obsproxygen.dataretriver.impl;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.obsproxygen.TypeMapper;

/**
 * Created by Thomas.Kummer on 07.02.2017.
 */
public class MethodContext {

    private final Element enclosingClass;
    private final Element rootElement;
    private final ExecutableElement method;
    private final TypeMapper typeMapper;

    public MethodContext(Element enclosingClass, Element rootElement, ExecutableElement method,
            TypeMapper typeMapper) {
        this.enclosingClass = enclosingClass;
        this.rootElement = rootElement;
        this.method = method;
        this.typeMapper = typeMapper;
    }

    public Element getEnclosingClass() {
        return enclosingClass;
    }

    public Element getRootElement() {
        return rootElement;
    }

    public ExecutableElement getMethod() {
        return method;
    }

    public TypeMapper getTypeMapper() {
        return typeMapper;
    }
}
