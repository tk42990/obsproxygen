package org.obsproxygen.dataretriver.impl;

import java.util.HashMap;
import java.util.Map;

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
    private final GlobalContext globalContext;
    final Map<String, Object> methodData = new HashMap<>();

    public MethodContext(Element enclosingClass, Element rootElement, ExecutableElement method,
            TypeMapper typeMapper, GlobalContext globalContext) {
        this.enclosingClass = enclosingClass;
        this.rootElement = rootElement;
        this.method = method;
        this.typeMapper = typeMapper;
        this.globalContext = globalContext;
        globalContext.getProperties().add(methodData);
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

    public GlobalContext getGlobalContext() {
        return globalContext;
    }

    public Map<String, Object> getMethodData() {
        return methodData;
    }
}
