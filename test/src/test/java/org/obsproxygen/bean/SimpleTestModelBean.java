package org.obsproxygen.bean;

import org.obsproxygen.GenerateObservable;

/**
 * Created by thku on 03.01.17.
 */
@GenerateObservable
public class SimpleTestModelBean {
    private OtherSimpleTestModelBean property;

    private String simpleProperty;

    public String getSimpleProperty() {
        return simpleProperty;
    }

    public void setSimpleProperty(String simpleProperty) {
        this.simpleProperty = simpleProperty;
    }

    public OtherSimpleTestModelBean getProperty() {
        return property;
    }

    public void setProperty(OtherSimpleTestModelBean property) {
        this.property = property;
    }
}
