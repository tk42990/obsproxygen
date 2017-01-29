package org.obsproxygen.dataretriver.impl;

import org.obsproxygen.TypeMapper;
import org.obsproxygen.codegenerator.CodeGenerator;
import org.obsproxygen.codegenerator.TemplateFactory;
import org.obsproxygen.dataretriver.ClassAnalyserListener;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import java.util.*;

import static org.obsproxygen.dataretriver.ClassDataHelper.*;
import static org.obsproxygen.dataretriver.ClassDataHelper.getFullQualifiedeClassName;
import static org.obsproxygen.dataretriver.ClassDataHelper.getPackageName;
import static org.obsproxygen.dataretriver.ClassDataHelper.hasTypeParameters;

/**
 * Created by thku on 28.12.16.
 */
public class GenerateObservableAnnotationProcessor implements ClassAnalyserListener {

    private Element rootElement;

    private enum GetterType{is,get}
    private final Map<String, Object> templateData = new HashMap<>();
    private Filer filer;
    private Messager messager;
    private final List<Map<String, Object>> properties;
    private final Map<String,GetterType> hasGetter = new HashMap<>();

    private enum TemplatePlaceholder {
        packagename, // package name of target class
        classname,  // class name of source class simple name
        classname_full, // full name of source class (FQN)
        properties, // list of properties in the source class
        type, // type of a property
        property_generator, // FQN of property generator
        date, // current date
        method_signature,
        parameter_call,
        method_name,
        return_value,
        has_type_parameter,
        type_parameter,
        is_property_setter,
        getter_name,
        has_getter,
        has_is_getter,
        property_name
    }


    public GenerateObservableAnnotationProcessor() {
        properties = new ArrayList<>();
        templateData.put(TemplatePlaceholder.properties.name(), properties);
    }

    @Override
    public void init(Filer filer, Messager messager) {
        this.filer = filer;
        this.messager = messager;
        templateData.put(TemplatePlaceholder.property_generator.name(), getClass().toString());
        templateData.put(TemplatePlaceholder.date.name(), new Date().toString());
    }

    @Override
    public void onClass(Element rootElement) {
        this.rootElement = rootElement;
        templateData.put(TemplatePlaceholder.classname.name(), getSimpleClassName(rootElement));
        templateData.put(TemplatePlaceholder.packagename.name(), getPackageName(rootElement));
        templateData.put(TemplatePlaceholder.classname_full.name(), getFullQualifiedeClassName(rootElement));
        templateData.put(TemplatePlaceholder.has_type_parameter.name(), hasTypeParameters(rootElement));
        templateData.put(TemplatePlaceholder.type_parameter.name(), getTypeParameters(rootElement));
    }

    @Override
    public void onMethod(Element enclosingClass, ExecutableElement method, TypeMapper typeMapper) {
        final Map<String, Object> methodData = new HashMap<>();
        methodData.put(TemplatePlaceholder.method_signature.name(),
                getMethodSignature(method, enclosingClass, rootElement, typeMapper));
        methodData.put(TemplatePlaceholder.method_name.name(),
                getMethodName(method));
        methodData.put(TemplatePlaceholder.parameter_call.name(),
                getParameterCall(method));
        methodData.put(TemplatePlaceholder.return_value.name(),
                hasReturnValue(method));
        methodData.put(TemplatePlaceholder.is_property_setter.name(),
                isPropertySetter(method));
        String propertyName = getPropertyName(method);
        if (!propertyName.isEmpty()&& isPropertyGetter(method)) {
            hasGetter.put(propertyName, isIsPropertyGetter(method) ? GetterType.is :GetterType.get);
        }
        methodData.put(TemplatePlaceholder.property_name.name(),
                propertyName);



        properties.add(methodData);
    }

    @Override
    public void finished() throws Exception {

        for (Map<String, Object> property : properties) {
            String propertyName = (String) property.get(TemplatePlaceholder.property_name.name());
            boolean hasGetter = this.hasGetter.containsKey(propertyName);
            property.put(TemplatePlaceholder.has_getter.name(), hasGetter);
            if(hasGetter){
                property.get(TemplatePlaceholder.property_name.name());
                boolean booleanGetter = this.hasGetter.get(propertyName) == GetterType.is;
                property.put(TemplatePlaceholder.has_is_getter.name(), booleanGetter);
                property.put(TemplatePlaceholder.getter_name.name(),
                        getGetterName(
                                (String) property.get(TemplatePlaceholder.property_name.name()),
                                booleanGetter));
            }

        }
        new CodeGenerator(filer, messager)
                .generateCode(
                        TemplateFactory.getTemplate(),
                        getNameForObservableClass(),
                        templateData);
    }


    private String getNameForObservableClass() {
        return templateData.get(TemplatePlaceholder.packagename.name()) + ".Observable" + templateData.get(TemplatePlaceholder.classname.name());
    }
}
