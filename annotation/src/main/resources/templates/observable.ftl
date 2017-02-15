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
        this.${property.property_name} = ObservableFactory.makeObservable(prefix + "${property.property_name}", observableModel, source.${property.method_name}(${property.parameter_call}));
        return this.${property.property_name};
</#if>
<#if property.is_property_setter>
        Object oldValue = <#if property.has_getter> ${property.getter_name}() <#else>null</#if>;
        this.${property.parameter_call} = ObservableFactory.makeObservable(prefix+ "${property.property_name}", observableModel,${property.parameter_call});
        observableModel.firePropertyChangeListener(prefix.isEmpty() ? "${property.property_name}" : prefix + "." + "${property.property_name}",  oldValue, this.${property.parameter_call});
</#if>
<#if property.return_value && !property.is_property_getter>
        return source.${property.method_name}(${property.parameter_call});
</#if>
<#if !property.return_value>
        source.${property.method_name}(${property.parameter_call});
</#if>
    }
</#list>


    @Override
    public ${classname} getSource(){
        return source;
    }

/*
    @Override
    public boolean equals(Object obj) {

        if(obj instanceof ObservableBean){
            return source.equals(((ObservableBean)obj).getSource());
        }
        return source.equals(obj);
    }

    @Override
    public int hashCode() {
        return source.hashCode();
    }
*/
}
