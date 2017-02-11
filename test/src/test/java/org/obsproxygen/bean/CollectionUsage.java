package org.obsproxygen.bean;

import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.obsproxygen.bean.beans.CollectionContainingModelBean;
import org.obsproxygen.bean.beans.SimpleTestModelBean;
import org.obsproxygen.observable.ObservableBean;
import org.obsproxygen.observable.ObservableModel;


public class CollectionUsage {

    private RecordingPropertyChangeListener pcl;

    @Before
    public void setUp() {
        pcl = new RecordingPropertyChangeListener();
    }

    @Test
    public void testProxy() {
        ObservableModel<CollectionContainingModelBean> model = new ObservableModel<>(new CollectionContainingModelBean());
        CollectionContainingModelBean proxy = model.getProxy();
        proxy.setSimpleTestModelBeans( new ArrayList<>());
        Assert.assertTrue(proxy.getSimpleTestModelBeans() instanceof ObservableBean);
    }

    @Test
    public void testAdd() {
        ObservableModel<CollectionContainingModelBean> model = new ObservableModel<>(
                new CollectionContainingModelBean());
        model.addPropertyChangeListener("simpleTestModelBeans[*].simpleProperty", pcl);
        CollectionContainingModelBean proxy = model.getProxy();
        proxy.setSimpleTestModelBeans(new ArrayList<>());
        //TODO proxy getter
        proxy.getSimpleTestModelBeans().add(new SimpleTestModelBean());
        proxy.getSimpleTestModelBeans().iterator().next().setSimpleProperty("hello");
        Assert.assertThat(pcl.getEvents().size(), CoreMatchers.equalTo(1));
        Assert.assertThat(pcl.getEvents().iterator().next().getOldValue(), CoreMatchers.equalTo(null));
        Assert.assertThat(pcl.getEvents().iterator().next().getNewValue(), CoreMatchers.equalTo("hello"));

    }

    @Test
    public void testStream() {
        ObservableModel<CollectionContainingModelBean> model = new ObservableModel<>(
                new CollectionContainingModelBean());
        model.addPropertyChangeListener("simpleTestModelBeans[*].simpleProperty", pcl);
        CollectionContainingModelBean proxy = model.getProxy();
        proxy.setSimpleTestModelBeans(new ArrayList<>());
        //TODO proxy getter
        proxy.getSimpleTestModelBeans().add(new SimpleTestModelBean());
        proxy.getSimpleTestModelBeans().add(new SimpleTestModelBean());
        proxy.getSimpleTestModelBeans().add(new SimpleTestModelBean());

        proxy.getSimpleTestModelBeans().forEach(simpleTestModelBean -> simpleTestModelBean.setSimpleProperty("Hello Stream"));


        Assert.assertThat(pcl.getEvents().size(), CoreMatchers.equalTo(3));
        pcl.getEvents().forEach(propertyChangeEvent -> {
            Assert.assertThat(propertyChangeEvent.getOldValue(), CoreMatchers.equalTo(null));
            Assert.assertThat(propertyChangeEvent.getNewValue(), CoreMatchers.equalTo("Hello Stream"));
        });

    }

    @Test
    public void testForEach() {
        ObservableModel<CollectionContainingModelBean> model = new ObservableModel<>(
                new CollectionContainingModelBean());
        model.addPropertyChangeListener("simpleTestModelBeans[*].simpleProperty", pcl);
        CollectionContainingModelBean proxy = model.getProxy();
        proxy.setSimpleTestModelBeans(new ArrayList<>());
        proxy.getSimpleTestModelBeans().add(new SimpleTestModelBean());
        proxy.getSimpleTestModelBeans().add(new SimpleTestModelBean());
        proxy.getSimpleTestModelBeans().add(new SimpleTestModelBean());

        Collection<SimpleTestModelBean> simpleTestModelBeans = proxy.getSimpleTestModelBeans();

        for (SimpleTestModelBean simpleTestModelBean : simpleTestModelBeans) {
            simpleTestModelBean.setSimpleProperty("Hello For Each");
        }

        Assert.assertThat(pcl.getEvents().size(), CoreMatchers.equalTo(3));
        pcl.getEvents().forEach(propertyChangeEvent -> {
            Assert.assertThat(propertyChangeEvent.getOldValue(), CoreMatchers.equalTo(null));
            Assert.assertThat(propertyChangeEvent.getNewValue(), CoreMatchers.equalTo("Hello For Each"));
        });

    }


}
