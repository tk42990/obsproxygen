package org.obsproxygen.dataretriver.impl;

import javax.lang.model.element.Element;

/**
 * Created by Thomas.Kummer on 07.02.2017.
 */
public class ClassContext {
    private Element element;
    private GlobalContext globalContext;

    public ClassContext(Element element, GlobalContext globalContext) {
        this.element = element;
        this.globalContext = globalContext;
    }

    public Element getElement() {
        return element;
    }

    public GlobalContext getGlobalContext() {
        return globalContext;
    }
}
