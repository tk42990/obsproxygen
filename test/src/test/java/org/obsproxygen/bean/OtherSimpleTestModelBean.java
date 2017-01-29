package org.obsproxygen.bean;

import org.obsproxygen.GenerateObservable;

/**
 * Created by thku on 07.01.17.
 */
@GenerateObservable
public class OtherSimpleTestModelBean {

    private String property;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
