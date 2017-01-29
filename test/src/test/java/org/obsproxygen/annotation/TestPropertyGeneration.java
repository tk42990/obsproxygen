package org.obsproxygen.annotation;

import java.util.Arrays;

import com.google.common.truth.FailureStrategy;
import com.google.testing.compile.JavaFileObjects;
import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.obsproxygen.ModelBeanPropertyGenerator;

/**
 * Test Property generation at compile time.
 */
public class TestPropertyGeneration {

    @org.junit.Test
    public void testPropertyGen(){

        JavaSourcesSubjectFactory.javaSources().getSubject(new FailureStrategy() {
        }, Arrays.asList(
                JavaFileObjects.forResource("test/OtherAnnotatedModelBean.java"),
                JavaFileObjects.forResource("test/TestModelBean.java")
        )).processedWith(new ModelBeanPropertyGenerator())
                .compilesWithoutError();

    }
/*
    @org.junit.Test
    public void testPropertyGenComplex() {

        JavaSourcesSubjectFactory.javaSources().getSubject(new FailureStrategy() {
        }, Arrays.asList(
                JavaFileObjects.forResource("test/complex/AbstractTableCriteriaSearchModelBean.java"),
                JavaFileObjects.forResource("test/complex/InMemoryPagingTableModelBean.java")

        )).processedWith(new ModelBeanPropertyGenerator())
                .compilesWithoutError();

    }

    @org.junit.Test
    public void testPropertyGenWithRawSuper() {
        JavaSourceSubjectFactory.javaSource().getSubject(new FailureStrategy() {},
                JavaFileObjects.forResource("test/TestModelBeanWithRawSuperClass.java"))
                .processedWith(new ModelBeanPropertyGenerator())
                .compilesWithoutError();
    }

    @org.junit.Test
    public void testPropertyGenComplex2() {

        JavaSourcesSubjectFactory.javaSources().getSubject(new FailureStrategy() {
        }, Arrays.asList(
                JavaFileObjects.forResource("test/complex/InMemoryPagingTableModelBean.java"),
                JavaFileObjects.forResource("test/complex2/DefaultSearchCriteriaModelBean.java"),
                JavaFileObjects.forResource("test/complex2/DefaultSearchModelBean.java"),
                JavaFileObjects.forResource("test/complex2/DefaultSearchResultRowModelBean.java"),
                JavaFileObjects.forResource("test/complex2/Usage.java")
        )).processedWith(new ModelBeanPropertyGenerator())
                .compilesWithoutError();

    }

    @org.junit.Test
    public void testPropertyGenInherit() {

        JavaSourcesSubjectFactory.javaSources().getSubject(new FailureStrategy() {
        }, Arrays.asList(
                JavaFileObjects.forResource("test/inherit/SubSub.java"),
                JavaFileObjects.forResource("test/inherit/Usage.java")
        )).processedWith(new ModelBeanPropertyGenerator())
                .compilesWithoutError();

    }
*/
}
