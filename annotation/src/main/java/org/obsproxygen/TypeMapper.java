package org.obsproxygen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;



public final class TypeMapper {

    private final List<TypeMapping> mappings = new ArrayList<>();


    void register(Element element, Map<Name, TypeMirror> typeMapping){
       mappings.add(new TypeMapping(element,typeMapping));
    }




    public TypeMirror getMapping(Element element, Name name) {
        int idx = 0;
        for (int i = 0; i < mappings.size(); i++) {
            if(mappings.get(i).element == element){
                idx = i;
                break;
            }
        }
        for (int i = idx; i >=0 ; i--) {
            TypeMirror typeMirror = mappings.get(i).typeMapping.get(name);
            if(typeMirror != null && typeMirror.getKind() == TypeKind.DECLARED){
                return typeMirror;
            }
        }
        return null;
    }


    private static class TypeMapping {

        private final Element element;
        private final Map<Name, TypeMirror> typeMapping;


        TypeMapping(Element element, Map<Name, TypeMirror> typeMapping) {
            this.typeMapping = typeMapping;
            this.element = element;
        }
    }
}
