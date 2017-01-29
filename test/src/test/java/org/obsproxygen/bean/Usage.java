package org.obsproxygen.bean;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.obsproxygen.observable.ObservableBean;
import org.obsproxygen.observable.ObservableModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.function.Function;

import static org.junit.Assert.*;


public class Usage {

    private RecordingPCL pcl;

    private static class RecordingPCL implements PropertyChangeListener{
        private List<PropertyChangeEvent> events = new ArrayList<>();

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            events.add(evt);
        }

        public List<PropertyChangeEvent> getEvents() {
            return events;
        }
    }

    @Before
    public void setUp(){
        pcl = new RecordingPCL();
    }

    @Test
    public void simpleUsage(){
        ObservableModel<SimpleTestModelBean> model = new ObservableModel<>(new SimpleTestModelBean());
        model.addPropertyChangeListener("simpleProperty", pcl);
        model.getProxy().setSimpleProperty("Hello");
        model.getProxy().setSimpleProperty("Hello1");

        assertThat(pcl.getEvents().size(),CoreMatchers.equalTo(2));
        assertThat(pcl.getEvents().get(0).getNewValue(),CoreMatchers.equalTo("Hello"));
        assertThat(pcl.getEvents().get(1).getNewValue(),CoreMatchers.equalTo("Hello1"));
        assertThat(pcl.getEvents().get(1).getOldValue(),CoreMatchers.equalTo("Hello"));

    }


    @Test
    public void simpleUsageModelBean(){
        ObservableModel<SimpleTestModelBean> model = new ObservableModel<>(new SimpleTestModelBean());
        model.addPropertyChangeListener("property", pcl);
        OtherSimpleTestModelBean property1 = new OtherSimpleTestModelBean();
        model.getProxy().setProperty(property1);
        OtherSimpleTestModelBean property2 = new OtherSimpleTestModelBean();
        model.getProxy().setProperty(property2);

        assertThat(pcl.getEvents().size(),CoreMatchers.equalTo(2));
        assertThat(pcl.getEvents().get(0).getNewValue() instanceof ObservableBean,CoreMatchers.equalTo(true));
        assertThat(((ObservableBean)pcl.getEvents().get(0).getNewValue()).getSource(),CoreMatchers.equalTo(property1));

        assertThat(pcl.getEvents().get(1).getNewValue() instanceof ObservableBean,CoreMatchers.equalTo(true));
        assertThat(((ObservableBean)pcl.getEvents().get(1).getNewValue()).getSource(),CoreMatchers.equalTo(property2));
        assertThat(((ObservableBean)pcl.getEvents().get(1).getOldValue()).getSource(),CoreMatchers.equalTo(property1));
    }


    @Test
    public void testPCLOnSubBean(){
        ObservableModel<SimpleTestModelBean> model = new ObservableModel<>(new SimpleTestModelBean());
        model.addPropertyChangeListener("property.property", pcl);
        OtherSimpleTestModelBean property1 = new OtherSimpleTestModelBean();
        model.getProxy().setProperty(property1);
        model.getProxy().getProperty().setProperty("new value in sub bean");

        assertThat(pcl.getEvents().size(),CoreMatchers.equalTo(1));
        assertThat(pcl.getEvents().get(0).getNewValue(),CoreMatchers.equalTo("new value in sub bean"));
    }

    @Test
    public void testPCLOnSubBean_Stream(){
        ObservableModel<SimpleTestModelBean> model = new ObservableModel<>(new SimpleTestModelBean());
        model.addPropertyChangeListener("property.property", pcl);
        OtherSimpleTestModelBean property1 = new OtherSimpleTestModelBean();
        List<SimpleTestModelBean> simpleTestModelBeans = Collections.singletonList(model.getProxy());

        model.getProxy().setProperty(property1);
        simpleTestModelBeans
                .stream()
                .map(SimpleTestModelBean::getProperty)
                .forEach(s -> s.setProperty("new value in sub bean"));

        assertThat(pcl.getEvents().size(),CoreMatchers.equalTo(1));
        assertThat(pcl.getEvents().get(0).getNewValue(),CoreMatchers.equalTo("new value in sub bean"));
    }

    @Test
    public void testPCLOnSubBean_Exchange(){
        ObservableModel<SimpleTestModelBean> model = new ObservableModel<>(new SimpleTestModelBean());
        model.addPropertyChangeListener("property.property", pcl);
        OtherSimpleTestModelBean property1 = new OtherSimpleTestModelBean();
        model.getProxy().setProperty(property1);
        model.getProxy().getProperty().setProperty("new value in sub bean");

        assertThat(pcl.getEvents().size(),CoreMatchers.equalTo(1));
        assertThat(pcl.getEvents().get(0).getNewValue(),CoreMatchers.equalTo("new value in sub bean"));

        property1 = new OtherSimpleTestModelBean();
        model.getProxy().setProperty(property1);
        model.getProxy().getProperty().setProperty("new value in sub bean");

        assertThat(pcl.getEvents().size(),CoreMatchers.equalTo(2));
        assertThat(pcl.getEvents().get(1).getNewValue(),CoreMatchers.equalTo("new value in sub bean"));
    }

    @Test
    public void testPCLOnSubBeanAndBeanChange(){
        ObservableModel<SimpleTestModelBean> model = new ObservableModel<>(new SimpleTestModelBean());
        model.addPropertyChangeListener("property.property", pcl);
        OtherSimpleTestModelBean property1 = new OtherSimpleTestModelBean();
        model.getProxy().setProperty(property1);
        model.getProxy().getProperty().setProperty("new value in sub bean");

        OtherSimpleTestModelBean property2 = new OtherSimpleTestModelBean();
        model.getProxy().setProperty(property2);
        model.getProxy().getProperty().setProperty("new value in new sub bean");


        assertThat(pcl.getEvents().size(),CoreMatchers.equalTo(2));
        assertThat(pcl.getEvents().get(0).getNewValue(),CoreMatchers.equalTo("new value in sub bean"));

        assertThat(pcl.getEvents().get(1).getNewValue(),CoreMatchers.equalTo("new value in new sub bean"));
        //todo dispose old bean om = null
    }


    private static void test(){
        Function<SimpleTestModelBean, OtherSimpleTestModelBean> getProperty = SimpleTestModelBean::getProperty;

        Function<OtherSimpleTestModelBean, String> getProperty1 = OtherSimpleTestModelBean::getProperty;

        Function<SimpleTestModelBean, String> compose = getProperty1.compose(getProperty);

        SimpleTestModelBean simpleTestModelBean = new SimpleTestModelBean();
        OtherSimpleTestModelBean property = new OtherSimpleTestModelBean();
        simpleTestModelBean.setProperty(property);
        property.setProperty("value");
        System.out.println(compose.apply(simpleTestModelBean));


    }

}
