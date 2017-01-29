package org.obsproxygen.dataretriver;

import org.obsproxygen.GenerateObservable;
import org.obsproxygen.dataretriver.impl.GenerateObservableAnnotationProcessor;

import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thku on 26.12.16.
 */
public class ClassAnalyserFactory {

    public  static List<ClassAnalyserListener> createListener(TypeElement type){
        if(hasAnnotation(type,GenerateObservable.class)){
            List<ClassAnalyserListener> returnValue = new ArrayList<>();
            returnValue.add(new GenerateObservableAnnotationProcessor());
            return returnValue;
        }
        return null;
    }

    private static boolean hasAnnotation(TypeElement type, Class<? extends  Annotation> clazz) {
        return type != null && type.getAnnotation(clazz) != null;
    }
}
