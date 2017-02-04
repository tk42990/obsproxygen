package org.obsproxygen;

import static java.util.Optional.of;

import java.util.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementKindVisitor6;
import javax.tools.Diagnostic;


import org.obsproxygen.dataretriver.ClassAnalyserFactory;
import org.obsproxygen.dataretriver.ClassAnalyserListener;

@SupportedAnnotationTypes({
        "org.obsproxygen.GenerateObservable"
})
public class ModelBeanPropertyGenerator extends AbstractProcessor {

//TODO collection

    private Filer filer;
    private Messager messager;

    private final Set<Object> created = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Set<? extends Element> rootElements = roundEnv.getRootElements();
        for (Element rootElement : rootElements) {
            TypeElement type = asTypeElement(rootElement);
            if (isResponsible(type)) {
                List<ClassAnalyserListener> listener = ClassAnalyserFactory.createListener(type);
                analyseClass(listener, rootElement, type);
            }
        }
        return true;
    }

    private void analyseClass(List<ClassAnalyserListener> listener, Element rootElement, TypeElement type) {
        listener.forEach(l -> l.init(filer,messager));
        Element currentElement = rootElement;
        TypeMirror currentType = rootElement.asType();
        Set<String> handledProperties = new HashSet<>();
        listener.forEach(classAnalyserListener -> classAnalyserListener.onClass(rootElement));
        TypeMapper typeMapper = new TypeMapper();// holder fo type variable name mappings
        do {
            updateTypeVariableTypeMapping(currentElement, currentType, typeMapper);
            final List<? extends Element> enclosedElements = currentElement.getEnclosedElements();
            for (Element enclosedElement : enclosedElements) {
                final ElementKind kind = enclosedElement.getKind();


                if (!handledProperties.contains(enclosedElement.getSimpleName().toString())) {
                    if (isValidMethod(enclosedElement, kind)) {
                        handledProperties.add(enclosedElement.getSimpleName().toString());
                        final Element currentClassElement = currentElement;
                        listener.forEach(
                                l -> l.onMethod(
                                        currentClassElement,
                                        (ExecutableElement) enclosedElement,
                                        typeMapper));
                    }
                }
            }
            Optional<DeclaredType> declaredType = of((TypeElement) currentElement)
                    .map(TypeElement::getSuperclass)
                    .filter(tm -> tm instanceof DeclaredType)
                    .map(tm -> (DeclaredType) tm);
            currentType = declaredType.orElse(null);
            currentElement = declaredType
                    .map(DeclaredType::asElement).orElse(null);

        } while (currentElement != null && !Object.class.getSimpleName().equals(
                currentElement.getSimpleName().toString()));
        listener.forEach(classAnalyserListener -> {

            try {
                classAnalyserListener.finished();
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.ERROR,e.getLocalizedMessage());
            }
        });
    }

    /**
     * convert to type element
     *
     * @param element the element
     * @return element as type
     */
    private TypeElement asTypeElement(Element element) {
        return element.accept(
                new ElementKindVisitor6<TypeElement, Void>() {
                    @Override
                    public TypeElement visitTypeAsInterface(TypeElement e, Void p) {
                        return e;
                    }

                    @Override
                    public TypeElement visitTypeAsClass(TypeElement e, Void p) {
                        return e;
                    }

                }, null
        );
    }








    /**
     * create mapping between type variables and bound types
     * Example(see tests):
     * The class :          public class TestModelBean<MB> extends AbstractTestModelBean<TestEnum>
     * The super class:     public class AbstractTestModelBean<VS extends Enum>
     * Mapping:             VS --> TestEnum
     *
     * @param element     the element
     * @param type        the type of element
     * @param typeMapper mapping holder for class hierarchy
     */
    private void updateTypeVariableTypeMapping(Element element, TypeMirror type, TypeMapper typeMapper) {
        Map<Name, TypeMirror> typeMap = new HashMap<>();
        if (element instanceof TypeElement && type instanceof DeclaredType) {
            TypeElement currentElement = (TypeElement) element;
            DeclaredType currentType = (DeclaredType) type;
            List<? extends TypeParameterElement> typeParameters = currentElement.getTypeParameters();
            List<? extends TypeMirror> typeArguments = currentType.getTypeArguments();
            for (int i = 0; i < typeParameters.size(); i++) {
                if (typeArguments.size() > i) { // it is possible that no argument is maintained (raw super type)
                    typeMap.put(typeParameters.get(i).getSimpleName(), typeArguments.get(i));
                }
            }
        }
        typeMapper.register(element, typeMap);
    }


    /**
     * returns true if method is getter for bean property
     *
     * @param enclosedElement java element e.g. method
     * @param kind            type of enclosedElement
     * @return true if element is property getter
     */
    private boolean isValidMethod(Element enclosedElement, ElementKind kind) {
        String name = enclosedElement.getSimpleName().toString();
        return kind == ElementKind.METHOD
                && enclosedElement.getModifiers().contains(Modifier.PUBLIC)
                && !name.equals("getClass")
                && enclosedElement instanceof ExecutableElement;
    }

    /**
     * returns true if the type has the annotation GenerateProperties
     *
     * @param type the type
     * @return true if annotated with @GenerateProperties
     */
    private boolean isResponsible(TypeElement type) {
        return type != null && type.getAnnotation(GenerateObservable.class) != null;
    }


    @SuppressWarnings("unchecked")
    public static <T> Class<T> castClass(Class<?> aClass) {
        return (Class<T>) aClass;
    }

}
