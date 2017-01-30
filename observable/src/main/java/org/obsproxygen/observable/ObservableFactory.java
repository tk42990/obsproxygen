package org.obsproxygen.observable;

import java.lang.reflect.Constructor;
import java.util.Collection;

import org.obsproxygen.GenerateObservable;

/**
 * Created by thku on 29.12.16.
 */
public class ObservableFactory {

    @SuppressWarnings("unchecked")
    public static <T> T makeObservable(String prefix, ObservableModel<?> observableModel, T object) { //TODO T class
        if (!(object instanceof ObservableBean)) {
            if (object != null) {
                Class<?> aClass = object.getClass();
                GenerateObservable annotation = aClass.getAnnotation(GenerateObservable.class);
                if (annotation != null) {
                    try {
                        Class<?> observableClass = Class
                                .forName(aClass.getPackage().getName() + ".Observable" + aClass.getSimpleName());
                        Constructor<?> constructor = observableClass
                                .getConstructor(String.class, ObservableModel.class, aClass);
                        return (T) constructor.newInstance(prefix, observableModel, object);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (object instanceof Collection) { //TODO check
                    return (T) new ObservableCollection<>(prefix, observableModel, (Collection) object);
                }
            }
            else {
                return null;
            }
        }
        return object;
    }

}
