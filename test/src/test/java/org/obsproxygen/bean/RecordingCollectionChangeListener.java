package org.obsproxygen.bean;

import java.util.ArrayList;
import java.util.List;

import org.obsproxygen.observable.CollectionChangeListener;
import org.obsproxygen.observable.CollectionChangedEvent;

class RecordingCollectionChangeListener implements CollectionChangeListener {
    private List<CollectionChangedEvent> events = new ArrayList<>();

    @Override
    public void collectionChanged(String property, CollectionChangedEvent evt) {
        events.add(evt);
    }

    public List<CollectionChangedEvent> getEvents() {
        return events;
    }
}
