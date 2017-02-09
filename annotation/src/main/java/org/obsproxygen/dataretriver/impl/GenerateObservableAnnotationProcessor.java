package org.obsproxygen.dataretriver.impl;

import static java.util.Arrays.asList;
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
import java.util.Date;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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


    private Filer filer;
    private Messager messager;
    private GlobalContext globalContext;
    private ClassContext classContext;

    @SuppressWarnings("unused")
    private enum TemplatePlaceholderInit implements Consumer<GlobalContext>{
        property_generator(globalContext1 -> GenerateObservableAnnotationProcessor.class.toString()), // FQN of property generator
        date(globalContext1 -> new Date().toString()); // current date

        private final Function<GlobalContext, Object> function;

        TemplatePlaceholderInit(Function<GlobalContext,Object> function) {
            this.function = function;
        }

        @Override
        public void accept(GlobalContext globalContext) {
            globalContext.getTemplateData().put(name(),function.apply(globalContext));
        }
    }

    @SuppressWarnings("unused")
    private enum TemplatePlaceholderFinish implements Consumer<GlobalContext> {
        getter_name((property, gc) -> {
            String propertyName = (String) property.get(TemplatePlaceholderMethod.property_name.name());
            boolean hasGetter = gc.getHasGetter().containsKey(propertyName);
            if (hasGetter) {
                boolean booleanGetter = gc.getHasGetter().get(propertyName) == GetterType.is;
                return getGetterName((String) property.get(TemplatePlaceholderMethod.property_name.name()),
                        booleanGetter);
            }
            return "";
        }),

        has_getter((property, gc) -> {
            String propertyName = (String) property.get(TemplatePlaceholderMethod.property_name.name());
            return gc.getHasGetter().containsKey(propertyName);
        }),

        has_is_getter((property, gc) -> {
            String propertyName = (String) property.get(TemplatePlaceholderMethod.property_name.name());
            boolean hasGetter = gc.getHasGetter().containsKey(propertyName);
            return hasGetter && gc.getHasGetter().get(propertyName) == GetterType.is;
        });

        private BiFunction<Map<String, Object>, GlobalContext, Object> function;

        TemplatePlaceholderFinish(BiFunction<Map<String, Object>, GlobalContext, Object> function) {
            this.function = function;
        }

        @Override
        public void accept(GlobalContext globalContext) {
            globalContext.getProperties()
                    .forEach(property -> property.put(name(), function.apply(property, globalContext)));
        }

    }


    @SuppressWarnings("unused")
    enum TemplatePlaceholderMethod implements Consumer<MethodContext> {
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
        public void accept(MethodContext methodContext) {
            methodContext.getMethodData().put(name(), function.apply(methodContext));
        }
    }

    @SuppressWarnings("unused")
    enum TemplatePlaceholderClass implements Consumer<ClassContext> {
        packagename(ClassDataHelper::getPackageName), // package name of target class
        classname(ClassDataHelper::getSimpleClassName),  // class name of source class simple name
        classname_full(ClassDataHelper::getFullQualifiedeClassName), // full name of source class (FQN)
        has_type_parameter(ClassDataHelper::hasTypeParameters),
        type_parameter(ClassDataHelper::getTypeParameters);
       // properties(element -> new ArrayList<>()); //TODO useless

        private final Function<Element, Object> function;

        TemplatePlaceholderClass(Function<Element, Object> function){
            this.function = function;
        }

        @Override
        public void accept(ClassContext classContext) {
            classContext.getGlobalContext()
                    .getTemplateData().put(name(), function.apply(classContext.getElement()));
        }
    }

    @Override
    public void init(Filer filer, Messager messager) {
        this.filer = filer;
        this.messager = messager;
        this.globalContext = new GlobalContext();
        asList(TemplatePlaceholderInit.values())
                .forEach(templatePlaceholderInit -> templatePlaceholderInit.accept(globalContext));
    }

    @Override
    public void onClass(Element rootElement) {
        classContext = new ClassContext(rootElement,globalContext);
        asList(TemplatePlaceholderClass.values())
                .forEach(templatePlaceholder -> templatePlaceholder.accept(classContext));

    }

    @Override
    public void onMethod(Element enclosingClass, ExecutableElement method, TypeMapper typeMapper) {
        MethodContext methodContext = new MethodContext(
                enclosingClass,
                classContext.getElement(),
                method,
                typeMapper,
                globalContext);

        asList(TemplatePlaceholderMethod.values())
                .forEach(templatePlaceholder -> templatePlaceholder.accept(methodContext));
        String propertyName = getPropertyName(method);
        if (!propertyName.isEmpty()&& isPropertyGetter(method)) {
            globalContext.getHasGetter().put(propertyName, isIsPropertyGetter(method) ? GetterType.is : GetterType.get);
        }
    }

    @Override
    public void finished() throws Exception {
        asList(TemplatePlaceholderFinish.values())
                .forEach(templatePlaceholder -> templatePlaceholder.accept(globalContext));
        new CodeGenerator(filer, messager)
                .generateCode(
                        TemplateFactory.getTemplate(),
                        getNameForObservableClass(),
                        globalContext.getTemplateData());
    }


    private String getNameForObservableClass() {
        return globalContext.getTemplateData().get(TemplatePlaceholderClass.packagename.name())
                + ".Observable" + globalContext.getTemplateData().get(TemplatePlaceholderClass.classname.name());
    }
}
