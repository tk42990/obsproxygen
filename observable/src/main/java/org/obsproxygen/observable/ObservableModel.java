package org.obsproxygen.observable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by thku on 09.01.17.
 */
//TODO sub model
public class ObservableModel<T> {
    private T source;
    private T proxy;
    private boolean active = true;

    private Map<String, List<PropertyChangeListener>> listeners = new HashMap<>();

    private Map<String, List<CollectionChangeListener>> collectionListeners = new HashMap<>();

    /**
     * Constructor
     * @param source the source object
     */
    public ObservableModel(T source) {
        this.source = source;
        this.proxy = ObservableFactory.makeObservable("", this, source);
    }

    /**
     * Return source with out proxy
     * @return source
     */
    public T getSource() {
        return source;
    }

    /**
     * Return proxy
     * @return the proxy
     */
    public T getProxy() {
        return proxy;
    }

    public void addPropertyChangeListener(String path, PropertyChangeListener propertyChangeListener) {
        List<PropertyChangeListener> listenerList = listeners.computeIfAbsent(path, k -> new ArrayList<>());
        listenerList.add(propertyChangeListener);
    }

    public void addCollectionChangeListener(String path, CollectionChangeListener collectionChangeListener) {
        List<CollectionChangeListener> listenerList = collectionListeners.computeIfAbsent(path, k -> new ArrayList<>());
        listenerList.add(collectionChangeListener);
    }

    public void removePropertyChangeListener(String path, PropertyChangeListener propertyChangeListener) {
        List<PropertyChangeListener> listenerList = listeners.get(path);
        if (listenerList != null) {
            listenerList.remove(propertyChangeListener);
        }
    }

    public void removeCollectionChangeListener(String path, CollectionChangeListener collectionChangeListener) {
        List<CollectionChangeListener> listenerList = collectionListeners.get(path);
        if (listenerList != null) {
            listenerList.remove(collectionChangeListener);
        }
    }

    public void activateListeners(boolean active) {
        this.active = active;
    }

    public void firePropertyChangeListener(String property, Object oldValue, Object newValue) {
        if (active) {
            List<PropertyChangeListener> listenerList = listeners.get(property);
            if (listenerList != null) {
                PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);
                for (PropertyChangeListener pcl : listenerList) {
                    pcl.propertyChange(event);
                }
            }
        }
    }

    public void fireCollectionChangeListener(String property, CollectionChangedEvent evt) {
        if (active) {
            List<CollectionChangeListener> listenerList = collectionListeners.get(property);
            if (listenerList != null) {

                for (CollectionChangeListener ccl : listenerList) {
                   ccl.collectionChanged(property,evt);
                }
            }
        }
    }
}
