package org.obsproxygen.observable;

import java.util.Collection;

/**
 * Created by Thomas.Kummer on 13.02.2017.
 */
public class CollectionChangedEvent {

    public enum ChangeType{added, multipleAdd,removed, multipeRemove}

    private final Object changedObject;
    private final ChangeType changeType;
    private final Collection<?> afterChange;

    public CollectionChangedEvent(Object changedObject,
            ChangeType changeType,  Collection<?> afterChange) {
        this.changedObject = changedObject;
        this.changeType = changeType;
        this.afterChange = afterChange;
    }

    public Object getChangedObject() {
        return changedObject;
    }

    public ChangeType getChangeType() {
        return changeType;
    }


    public Collection<?> getAfterChange() {
        return afterChange;
    }


}
