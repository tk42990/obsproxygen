package org.obsproxygen.bean.beans;

import java.util.Collection;

import org.obsproxygen.GenerateObservable;

/**
 * Created by Thomas.Kummer on 30.01.2017.
 */
@GenerateObservable
public class CollectionContainingModelBean {

    private Collection<SimpleTestModelBean> simpleTestModelBeans;


    public Collection<SimpleTestModelBean> getSimpleTestModelBeans() {
        return simpleTestModelBeans;
    }

    public void setSimpleTestModelBeans(
            Collection<SimpleTestModelBean> simpleTestModelBeans) {
        this.simpleTestModelBeans = simpleTestModelBeans;
    }
}
