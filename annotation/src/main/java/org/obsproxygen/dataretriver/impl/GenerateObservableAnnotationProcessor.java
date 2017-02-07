package org.obsproxygen.dataretriver.impl;

import static org.obsproxygen.dataretriver.ClassDataHelper.getGetterName;
import static org.obsproxygen.dataretriver.ClassDataHelper.getMethodName;
import static org.obsproxygen.dataretriver.ClassDataHelper.getMethodSignature;
import static org.obsproxygen.dataretriver.ClassDataHelper.getParameterCall;
import static org.obsproxygen.dataretriver.ClassDataHelper.getPropertyName;
import static org.obsproxygen.dataretriver.ClassDataHelper.hasReturnValue;
import static org.obsproxygen.dataretriver.ClassDataHelper.isIsPropertyGetter;
import static org.obsproxygen.dataretriver.ClassDataHelper.isPropertyGetter;
import static org.obsproxygen.dataretriver.ClassDataHelper.isPropertySetter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

import org.obsproxygen.TypeMapper;
import org.obsproxygen.codegenerator.CodeGenerator;
import org.obsproxygen.codegenerator.TemplateFactory;
import org.obsproxygen.dataretriver.ClassAnalyserListener;
import org.obsproxygen.dataretriver.ClassDataHelper;

/**
 * Created by thku on 28.12.16.
 */
public class GenerateObservableAnnotationProcessor implements ClassAnalyserListener {

    private Element rootElement;

    private enum GetterType{is,get}
    private final Map<String, Object> templateData = new HashMap<>();
    private Filer filer;
    private Messager messager;
    private List<Map<String, Object>> properties;
    private final Map<String,GetterType> hasGetter = new HashMap<>();

    private enum TemplatePlaceholder {
        property_generator, // FQN of property generator
        date, // current date
        getter_name,
        has_getter,
        has_is_getter,
        property_name
    }


    @SuppressWarnings("unused")
    private enum TemplatePlaceholderMethod implements BiConsumer<Map<String, Object>, MethodContext> {
        method_signature (m -> getMethodSignature(m.getMethod(), m.getEnclosingClass(),m.getRootElement(), m.getTypeMapper())),
        parameter_call(methodContext -> getParameterCall(methodContext.getMethod())),
        method_name(methodContext -> getMethodName(methodContext.getMethod())),
        return_value(methodContext -> hasReturnValue(methodContext.getMethod())),
        is_property_setter(methodContext -> isPropertySetter(methodContext.getMethod())),
        property_name(methodContext -> getPropertyName(methodContext.getMethod()));

        private final Function<MethodContext, Object> function;

        TemplatePlaceholderMethod(Function<MethodContext, Object> function) {
            this.function = function;
        }

        @Override
        public void accept(Map<String, Object> stringObjectMap, MethodContext methodContext) {
            stringObjectMap.put(name(), function.apply(methodContext));
        }
    }

    @SuppressWarnings("unused")
    private enum TemplatePlaceholderClass implements BiConsumer<Map<String, Object>,Element> {
        packagename(ClassDataHelper::getPackageName), // package name of target class
        classname(ClassDataHelper::getSimpleClassName),  // class name of source class simple name
        classname_full(ClassDataHelper::getFullQualifiedeClassName), // full name of source class (FQN)
        has_type_parameter(ClassDataHelper::hasTypeParameters),
        type_parameter(ClassDataHelper::getTypeParameters),
        properties(element -> new ArrayList<>());

        private final Function<Element, Object> function;

        TemplatePlaceholderClass(Function<Element, Object> function){
            this.function = function;
        }

        @Override
        public void accept(Map<String, Object> stringObjectMap, Element element) {
            stringObjectMap.put(name(), function.apply(element));
        }
    }

    public GenerateObservableAnnotationProcessor() {
        properties = new ArrayList<>();
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
        Arrays.asList(TemplatePlaceholderClass.values())
                .forEach(templatePlaceholder -> templatePlaceholder.accept(templateData, rootElement));
        properties = (List<Map<String, Object>>) templateData.get(TemplatePlaceholderClass.properties.name());
    }

    @Override
    public void onMethod(Element enclosingClass, ExecutableElement method, TypeMapper typeMapper) {
        MethodContext methodContext = new MethodContext(enclosingClass, rootElement, method, typeMapper);

        final Map<String, Object> methodData = new HashMap<>();
        Arrays.asList(TemplatePlaceholderMethod.values())
                .forEach(templatePlaceholder -> templatePlaceholder.accept(methodData, methodContext));
        properties.add(methodData);
        String propertyName = getPropertyName(method);
        if (!propertyName.isEmpty()&& isPropertyGetter(method)) {
            hasGetter.put(propertyName, isIsPropertyGetter(method) ? GetterType.is :GetterType.get);
        }
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
        return templateData.get(TemplatePlaceholderClass.packagename.name()) + ".Observable" + templateData.get(TemplatePlaceholderClass.classname.name());
    }
}
