package org.obsproxygen.bean;

import static org.obsproxygen.observable.CollectionChangedEvent.ChangeType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.obsproxygen.bean.beans.CollectionContainingModelBean;
import org.obsproxygen.bean.beans.SimpleTestModelBean;
import org.obsproxygen.observable.CollectionChangedEvent;
import org.obsproxygen.observable.ObservableModel;

public class CollectionChangeListenerTest {

    private RecordingCollectionChangeListener ccl;

    @Before
    public void setUp() {
        ccl = new RecordingCollectionChangeListener();
    }

    @Test
    public void testAdd() {
        ObservableModel<CollectionContainingModelBean> model = new ObservableModel<>(new CollectionContainingModelBean());
        model.addCollectionChangeListener("simpleTestModelBeans",ccl);

        CollectionContainingModelBean proxy = model.getProxy();
        proxy.setSimpleTestModelBeans( new ArrayList<>());

        SimpleTestModelBean e = new SimpleTestModelBean();
        proxy.getSimpleTestModelBeans().add(e);

        Assert.assertThat(ccl.getEvents().size(), CoreMatchers.equalTo(1));
        CollectionChangedEvent evt = ccl.getEvents().iterator().next();
        Assert.assertThat(evt.getChangeType(), CoreMatchers.equalTo( ChangeType.added));
        Assert.assertThat(evt.getChangedObject(), CoreMatchers.equalTo( e));
        Assert.assertThat(evt.getAfterChange().size(), CoreMatchers.equalTo(1));
        Assert.assertTrue(evt.getAfterChange().iterator().next().equals(e));
    }

    @Test
    public void testRemove() {
        ObservableModel<CollectionContainingModelBean> model = new ObservableModel<>(
                new CollectionContainingModelBean());


        CollectionContainingModelBean proxy = model.getProxy();
        proxy.setSimpleTestModelBeans(new ArrayList<>());

        SimpleTestModelBean e = new SimpleTestModelBean();
        proxy.getSimpleTestModelBeans().add(e);

        model.addCollectionChangeListener("simpleTestModelBeans", ccl);

        proxy.getSimpleTestModelBeans().remove(e);

        Assert.assertThat(ccl.getEvents().size(), CoreMatchers.equalTo(1));
        CollectionChangedEvent evt = ccl.getEvents().iterator().next();
        Assert.assertThat(evt.getChangeType(), CoreMatchers.equalTo(ChangeType.removed));
        Assert.assertThat(evt.getChangedObject(), CoreMatchers.equalTo(e));
        Assert.assertThat(evt.getAfterChange().size(), CoreMatchers.equalTo(0));
    }

    @Test
    public void testRemoveSteam() {
        ObservableModel<CollectionContainingModelBean> model = new ObservableModel<>(
                new CollectionContainingModelBean());

        CollectionContainingModelBean proxy = model.getProxy();
        proxy.setSimpleTestModelBeans(new ArrayList<>());

        SimpleTestModelBean simpleTestModelBean = new SimpleTestModelBean();
        simpleTestModelBean.setSimpleProperty("hello");
        proxy.getSimpleTestModelBeans().add(simpleTestModelBean);

        SimpleTestModelBean simpleTestModelBean1 = new SimpleTestModelBean();
        simpleTestModelBean1.setSimpleProperty("hello1");
        proxy.getSimpleTestModelBeans().add(simpleTestModelBean1);

        model.addCollectionChangeListener("simpleTestModelBeans", ccl);

        List<SimpleTestModelBean> toRemove = proxy.getSimpleTestModelBeans().stream()
                .filter(sb -> sb.getSimpleProperty().equals("hello")).collect(
                        Collectors.toList());
        proxy.getSimpleTestModelBeans().removeAll(toRemove);

        Assert.assertThat(ccl.getEvents().size(), CoreMatchers.equalTo(1));
        CollectionChangedEvent evt = ccl.getEvents().iterator().next();
        Assert.assertThat(evt.getChangeType(), CoreMatchers.equalTo(ChangeType.multipeRemove));
        Assert.assertTrue(evt.getChangedObject() instanceof Collection);
        Assert.assertThat(((Collection) evt.getChangedObject()).iterator().next(),
                CoreMatchers.equalTo(simpleTestModelBean));
        Assert.assertThat(evt.getAfterChange().size(), CoreMatchers.equalTo(1));
    }



}
