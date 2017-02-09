package org.obsproxygen;

import com.google.common.truth.FailureStrategy;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.Ignore;

import java.util.Arrays;

/**
 * Created by thku on 09.02.17.
 */
@Ignore
public class AnnotationProcessorTest {

    @org.junit.Test
    public void testPropertyGen(){

        JavaSourcesSubjectFactory.javaSources().getSubject(new FailureStrategy() {
        }, Arrays.asList(
                JavaFileObjects.forResource("org/obsproxygen/bean/beans/SimpleTestModelBean.java"),
                JavaFileObjects.forResource("org/obsproxygen/bean/beans/OtherSimpleTestModelBean.java")
        )).processedWith(new AnnotationProcessor())
                .compilesWithoutError();

    }

}
