package org.obsproxygen.bean.beans;

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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || ! (o instanceof SimpleTestModelBean))
            return false;

        SimpleTestModelBean that = (SimpleTestModelBean) o;

        return !(simpleProperty != null ? !simpleProperty.equals(that.getSimpleProperty()) : that.getSimpleProperty() != null);

    }

    @Override
    public int hashCode() {
        return simpleProperty != null ? simpleProperty.hashCode() : 0;
    }
}
