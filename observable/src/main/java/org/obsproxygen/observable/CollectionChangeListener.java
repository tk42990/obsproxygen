package org.obsproxygen.observable;

/**
 * Created by Thomas.Kummer on 13.02.2017.
 */
public interface CollectionChangeListener {

    void collectionChanged(String property, CollectionChangedEvent evt);

}
