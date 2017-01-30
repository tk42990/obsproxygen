package org.obsproxygen.bean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas.Kummer on 30.01.2017.
 */
class RecordingPropertyChangeListener implements PropertyChangeListener {
    private List<PropertyChangeEvent> events = new ArrayList<>();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        events.add(evt);
    }

    public List<PropertyChangeEvent> getEvents() {
        return events;
    }
}
