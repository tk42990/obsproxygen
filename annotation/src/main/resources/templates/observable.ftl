package ${packagename};
import ${classname_full};
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import javax.annotation.Generated;
import java.beans.PropertyChangeEvent;
import org.obsproxygen.observable.ObservableBean;
import java.beans.PropertyChangeListener;
import org.obsproxygen.observable.ObservableFactory;
import org.obsproxygen.observable.ObservableModel;

//
// THIS IS A GENERATED FILE. DO NOT EDIT.
//
//
/**
* Property Class for Bean: {@link ${classname_full}}
* See {@link GenerateObservable} for more information.
*/
@Generated(
    value = "${property_generator}",
    date = "${date}"
)
public final class Observable${classname} <#if type_parameter?has_content><${type_parameter}></#if> extends ${classname}<#if type_parameter?has_content><${type_parameter}></#if> implements ObservableBean<${classname}>{

    private String prefix = "";
    private ObservableModel<?> observableModel;
    private ${classname}<#if type_parameter?has_content><${type_parameter}></#if> source;

<#list properties as property>
        <#if property.is_property_getter>
    private ${property.return_type} ${property.property_name};
        </#if>
</#list>

    public Observable${classname}(String prefix, ObservableModel<?> observableModel,  ${classname}<#if type_parameter?has_content><${type_parameter}></#if> source){
        this.source = source;
        this.prefix = prefix;
        this.observableModel = observableModel;
    }
<#list properties as property>

    @Override
    ${property.method_signature}{
<#if property.is_property_getter>
        Object ${property.property_name} = source.${property.method_name}(${property.parameter_call});
        if(this.${property.property_name} == ${property.property_name}){
            return this.${property.property_name};
        }
        this.${property.property_name} = ObservableFactory.makeObservable(prefix + "${property.property_name}", observableModel, source.getProperty());
        return this.property;
</#if>
<#if property.return_value>
        return source.${property.method_name}(${property.parameter_call});
<#else>
    <#if property.is_property_setter>
        Object oldValue = <#if property.has_getter> ${property.getter_name}() <#else>null</#if>;
        ${property.parameter_call} = ObservableFactory.makeObservable(prefix+ "${property.property_name}", observableModel,${property.parameter_call});
        // set proxy value
    </#if>
        source.${property.method_name}(${property.parameter_call});
    <#if property.is_property_setter>
        observableModel.firePropertyChangeListener(prefix.isEmpty() ? "${property.property_name}" : prefix + "." + "${property.property_name}",  oldValue, ${property.parameter_call});
    </#if>

</#if>
    }
</#list>


    @Override
    public ${classname} getSource(){
        return source;
    }


    @Override
    public boolean equals(Object obj) {
        return source.equals(obj);
    }

    @Override
    public int hashCode() {
        return source.hashCode();
    }

/*
@Override
public org.obsproxygen.bean.beans.OtherSimpleTestModelBean getProperty(){
Object property = source.getProperty();
if(this.property == property){
return this.property;
}
this.property = ObservableFactory.makeObservable(prefix + "property", observableModel, source.getProperty());
return this.property;
}

@Override
public void setProperty(org.obsproxygen.bean.beans.OtherSimpleTestModelBean property){


Object oldValue =  getProperty() ;
property = ObservableFactory.makeObservable(prefix+ "property", observableModel,property);

// set proxy value
this.property = property;
source.setProperty(property);
observableModel.firePropertyChangeListener(prefix.isEmpty() ? "property" : prefix + "." + "property",  oldValue, property);

}
*/
}
