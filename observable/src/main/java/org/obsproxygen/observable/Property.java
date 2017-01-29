package org.obsproxygen.observable;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by thku on 08.01.17.
 */
public class Property <T,TT>{

    private final Function<T,TT> getter;
    private final BiConsumer<T,TT> setter; // dummy
    private final String name;

    //TODO check null values


    public Property(String name,Function<T, TT> getter, BiConsumer<T, TT> setter) {
        this.getter = getter;
        this.setter = setter;
        this.name = name;
    }

    public Property(String name, Function<T, TT> getter) {
        this(name, getter, (t, tt) -> {});
    }


    public Function<T, TT> getGetter() {
        return getter;
    }

    public BiConsumer<T, TT> getSetter() {
        return setter;
    }

    public String getName() {
        return name;
    }
}
