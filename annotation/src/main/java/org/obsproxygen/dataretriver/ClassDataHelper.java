package org.obsproxygen.dataretriver;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.util.ElementKindVisitor6;

import org.obsproxygen.TypeMapper;
import org.obsproxygen.dataretriver.impl.MethodContext;

/**
 * Created by thku on 28.12.16.
 */
public class ClassDataHelper {


    public static String getSimpleClassName(Element clazz) {
        assert clazz.getKind() == ElementKind.CLASS;
        return asTypeElement(clazz).getSimpleName().toString();
    }

    public static boolean hasTypeParameters(Element clazz) {
        assert clazz.getKind() == ElementKind.CLASS;
        List<? extends TypeParameterElement> typeParameters = ((TypeElement) clazz).getTypeParameters();
        return typeParameters != null && !typeParameters.isEmpty();
    }

    public static String getTypeParameters(Element clazz) {
        assert clazz.getKind() == ElementKind.CLASS;
        List<? extends TypeParameterElement> typeParameters = ((TypeElement) clazz).getTypeParameters();
        StringJoiner joiner = new StringJoiner(",");
        for (TypeParameterElement typeParameter : typeParameters) {
            joiner.add(typeParameter.getSimpleName());
        }
        return joiner.toString();
    }

    public static String getFullQualifiedeClassName(Element clazz) {
        assert clazz.getKind() == ElementKind.CLASS;

        return getPackageName(clazz) + "." + getSimpleClassName(clazz);
    }

    public static String getPackageName(Element clazz) {
        assert clazz.getKind() == ElementKind.CLASS;
        return ((PackageElement) asTypeElement(clazz).getEnclosingElement()).getQualifiedName().toString();
    }


    public static boolean hasReturnValue(ExecutableElement method) {
        TypeMirror returnType = method.getReturnType();
        return !returnType.getKind().equals(TypeKind.VOID);
    }

    public static String getMethodSignature(ExecutableElement method, Element currentElement, Element rootElement, TypeMapper typeMapper) {

        String signature = "";
        Set<Modifier> modifiers = method.getModifiers();

        if (modifiers.size() == 1 && modifiers.iterator().next().equals(Modifier.PUBLIC)) {
            signature += "public ";
        }

        signature += determineReturnType(currentElement, method, rootElement, typeMapper);
        signature += " ";
        signature += method.getSimpleName().toString();
        signature += "(";
        String parameterSignature = getParameterSignature(currentElement, rootElement, typeMapper, method);
        signature += parameterSignature;
        signature += ")";
        return signature;
    }

    private static String getParameterSignature(Element currentElement, Element rootElement, TypeMapper typeMapper, ExecutableElement method) {
        List<? extends VariableElement> parameters = method.getParameters();

        StringJoiner stringJoiner = new StringJoiner(",");

        for (VariableElement parameter : parameters) {
            String parameterType = getType(currentElement, rootElement, typeMapper, parameter.asType());
            String name = parameter.getSimpleName().toString();

            stringJoiner.add(parameterType + " " + name);

        }
        return stringJoiner.toString();
    }


    public static String getParameterCall(ExecutableElement method) {
        List<? extends VariableElement> parameters = method.getParameters();

        StringJoiner stringJoiner = new StringJoiner(",");

        for (VariableElement parameter : parameters) {
            String name = parameter.getSimpleName().toString();
            stringJoiner.add(name);

        }
        return stringJoiner.toString();
    }

    private static String determineReturnType(Element currentElement, ExecutableElement method, Element rootElement, TypeMapper typeMapper) {
        final TypeMirror returnTypeOfMethod = method.getReturnType();

        return getType(currentElement, rootElement, typeMapper, returnTypeOfMethod);
    }

    public static String getMethodName(ExecutableElement method) {
        return method.getSimpleName().toString();
    }


    public static String getType(MethodContext context) {
        return getType(
                context.getEnclosingClass(),
                context.getRootElement(),
                context.getTypeMapper(),
                context.getMethod().getReturnType());
    }

    private static String getType(Element currentElement, Element rootElement, TypeMapper typeMapper, TypeMirror returnTypeOfMethod) {
        String propertyType;
        if (returnTypeOfMethod instanceof DeclaredType) {
            propertyType = ((DeclaredType) returnTypeOfMethod).asElement().toString();
        } else if (returnTypeOfMethod instanceof TypeVariable) {
            if (currentElement == rootElement) {
                propertyType = returnTypeOfMethod.toString();
            } else {
                String fallbackType = Optional.of(((TypeVariable) returnTypeOfMethod))
                        .map(TypeVariable::getUpperBound)
                        .map(ClassDataHelper::handleIntersectionType)
                        .map(Object::toString).orElse("java.lang.Object");
                propertyType = Optional
                        .ofNullable(
                                typeMapper.getMapping(
                                        currentElement,
                                        ((TypeVariable) returnTypeOfMethod).asElement().getSimpleName()))
                        .filter(typeMirror -> typeMirror instanceof DeclaredType)
                        .map(tM -> (DeclaredType) tM)
                        .map(DeclaredType::asElement)
                        .map(Object::toString)
                        .orElse(fallbackType);
            }

        } else {
            propertyType = returnTypeOfMethod.toString();
        }

        if (returnTypeOfMethod instanceof PrimitiveType) {
            final PrimitiveType tag = (PrimitiveType) returnTypeOfMethod;
            propertyType = tag.toString();
        }
        return propertyType;
    }


    private static Object handleIntersectionType(TypeMirror tm) {
        if (tm.getKind() == TypeKind.INTERSECTION && tm instanceof IntersectionType) {
            List<? extends TypeMirror> bounds = ((IntersectionType) tm).getBounds();
            if (bounds != null && !bounds.isEmpty()) {
                TypeMirror typeMirror = bounds.get(0);
                if (typeMirror instanceof DeclaredType) {
                    return ((DeclaredType) typeMirror).asElement();
                }
            }
            return "java.lang.Object";
        }
        return tm;
    }

    /**
     * convert to type element
     *
     * @param element the element
     * @return element as type
     */
    private static TypeElement asTypeElement(Element element) {
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


    public static boolean isPropertyMethod(ExecutableElement method) {
        return isPropertyGetter(method) || isPropertySetter(method);
    }

    public static boolean isPropertyGetter(ExecutableElement method) {
        String name = method.getSimpleName().toString();
        return method.getModifiers().contains(Modifier.PUBLIC)
                && ((name.startsWith("get") && name.length() > 4) || (name.startsWith("is") && name.length() > 3))
                && !name.equals("getClass")
                && ((ExecutableElement) method).getParameters().size() == 0
                && !(method.getReturnType().getKind() == TypeKind.VOID);
    }

    public static boolean isIsPropertyGetter(ExecutableElement method) {
        String name = method.getSimpleName().toString();
        return isPropertyGetter(method) && name.startsWith("is");
    }


    public static boolean isPropertySetter(ExecutableElement method) {
        String name = method.getSimpleName().toString();
        return method.getModifiers().contains(Modifier.PUBLIC)
                && ((name.startsWith("set") && name.length() > 4))
                && ((ExecutableElement) method).getParameters().size() == 1
                && (method.getReturnType().getKind() == TypeKind.VOID);
    }


    public static String getPropertyName(ExecutableElement enclosedElement) {
        final String string = enclosedElement.getSimpleName().toString();
        if (isPropertyGetter(enclosedElement)) {

            if (string.startsWith("get")) {
                return string.substring(3, 4).toLowerCase() + string.substring(4);
            } else {
                return string.substring(2, 3).toLowerCase() + string.substring(3);
            }
        }
        if (isPropertySetter(enclosedElement)) {
            return string.substring(3, 4).toLowerCase() + string.substring(4);
        }
        return "";
    }

    public static String getGetterName(String propertyName, boolean booleanGetter) {
        if (!propertyName.isEmpty()) {
            if (booleanGetter) {
                return "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
            }

            return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
        }
        return "";

    }

}
