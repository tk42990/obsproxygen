package org.obsproxygen.observable;

import org.obsproxygen.GenerateObservable;


import java.lang.reflect.Constructor;


/**
 * Created by thku on 29.12.16.
 */
public class ObservableFactory {

    @SuppressWarnings("unchecked")
    public static <T> T wrap(T object) {
        if (!(object instanceof ObservableBean)) {
            if (object != null) {
                Class<?> aClass = object.getClass();
                GenerateObservable annotation = aClass.getAnnotation(GenerateObservable.class);
                if (annotation != null) {
                    try {
                        Class<?> observableClass = Class.forName(aClass.getPackage().getName() + ".Observable" + aClass.getSimpleName());
                        Constructor<?> constructor = observableClass.getConstructor(aClass);
                        return (T) constructor.newInstance(object);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return object;
    }




    @SuppressWarnings("unchecked")
    public static <T> T makeObservable(String prefix, ObservableModel<?> observableModel, T object) {
        if (!(object instanceof ObservableBean)) {
            if (object != null) {
                Class<?> aClass = object.getClass();
                GenerateObservable annotation = aClass.getAnnotation(GenerateObservable.class);
                if (annotation != null) {
                    try {
                        Class<?> observableClass = Class.forName(aClass.getPackage().getName() + ".Observable" + aClass.getSimpleName());
                        Constructor<?> constructor = observableClass.getConstructor(String.class, ObservableModel.class, aClass);
                        return (T) constructor.newInstance(prefix, observableModel, object);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                return null;
            }

        }
        return object;
    }


    @SuppressWarnings("unchecked")
    public static <T> ObservableBean<T> makeObservable(T object) {
        if (!(object instanceof ObservableBean)) {
            if (object != null) {
                Class<?> aClass = object.getClass();
                GenerateObservable annotation = aClass.getAnnotation(GenerateObservable.class);
                if (annotation != null) {
                    try {
                        Class<?> observableClass = Class.forName(aClass.getPackage().getName() + ".Observable" + aClass.getSimpleName());
                        Constructor<?> constructor = observableClass.getConstructor(aClass);
                        return (ObservableBean<T>) constructor.newInstance(object);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                return null;
            }

        }
        return (ObservableBean<T>) object;
    }
}
