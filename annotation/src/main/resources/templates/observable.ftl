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

    public Observable${classname}(String prefix, ObservableModel<?> observableModel,  ${classname}<#if type_parameter?has_content><${type_parameter}></#if> source){
        this.source = source;
        this.prefix = prefix;
        this.observableModel = observableModel;
    }
<#list properties as property>

    @Override
    ${property.method_signature}{
<#if property.return_value>
        return source.${property.method_name}(${property.parameter_call});
<#else>
<#if property.is_property_setter>
        Object oldValue = <#if property.has_getter> ${property.getter_name}() <#else>null</#if>;
        ${property.parameter_call} = ObservableFactory.makeObservable(prefix+ "${property.property_name}", observableModel,${property.parameter_call});
</#if>
        source.${property.method_name}(${property.parameter_call});
<#if property.is_property_setter>
        observableModel.firePropertyChangeListener(prefix.isEmpty() ? "${property.property_name}" : prefix + "." + "${property.property_name}",  oldValue, ${property.parameter_call});
</#if>

</#if>
    }
</#list>


    public ${classname} getSource(){
        return source;
    }

}
