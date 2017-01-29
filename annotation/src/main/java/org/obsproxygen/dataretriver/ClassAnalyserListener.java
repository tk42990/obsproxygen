package org.obsproxygen.dataretriver;

import org.obsproxygen.TypeMapper;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * Created by thku on 25.12.16.
 */
public interface ClassAnalyserListener {

    default void init(Filer filer, Messager messager) {
    }

    default void onClass(Element rootElement) {
    }

    default void onMethod(Element enclosingClass, ExecutableElement method, TypeMapper typeMapper) {
    }

    default void finished()  throws  Exception{
    }
}
