package org.obsproxygen.observable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.PreferenceChangeListener;

/**
 * Created by thku on 09.01.17.
 */
public class ObservableModel<T> {
    private T source;
    private T proxy;
    private boolean active = true;

    private Map<String, List<PropertyChangeListener>> listeners = new HashMap<>();


    public ObservableModel(T source) {
        this.source = source;
        this.proxy = ObservableFactory.makeObservable("",this,source);
    }

    public T getSource() {
        return source;
    }

    public T getProxy() {
        return proxy;
    }

    public void addPropertyChangeListener(String path, PropertyChangeListener propertyChangeListener) {
        List<PropertyChangeListener> listenerList = listeners.computeIfAbsent(path, k -> new ArrayList<>());
        listenerList.add(propertyChangeListener);
    }

    public void removePropertyChangeListener(String path, PropertyChangeListener propertyChangeListener) {
        List<PropertyChangeListener> listenerList = listeners.get(path);
        if (listenerList != null) {
            listenerList.remove(propertyChangeListener);
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
}
