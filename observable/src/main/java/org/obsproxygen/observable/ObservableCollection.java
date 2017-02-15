package org.obsproxygen.observable;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by thku on 29.12.16.
 */
public class ObservableCollection<E> implements Collection<E>, ObservableBean<Collection<E>> {

    private String prefix = "";
    private ObservableModel<?> observableModel;
    private Collection<E> source;
    private String COLLECTION_PREFIX = "[*]";
    // todo path to sub model bean

    //todo collection change listener

    public ObservableCollection(String prefix, ObservableModel<?> observableModel, Collection<E> source) {
        this.source = source;
        this.prefix = prefix;
        this.observableModel = observableModel;
    }

    @Override
    public int size() {
        return source.size();
    }

    @Override
    public boolean isEmpty() {
        return source.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return source.contains(o); //TODO check
    }

    @Override
    public Iterator<E> iterator() {
        return source.iterator();
    }

    @Override
    public Object[] toArray() {
        return source.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return source.toArray(a);
    }

    @Override
    public boolean add(E e) {
        E proxy = ObservableFactory.makeObservable(prefix + COLLECTION_PREFIX, observableModel, e);
        boolean add = source.add(proxy);
        if(add){
            observableModel.fireCollectionChangeListener(prefix,
                    new CollectionChangedEvent(
                            e,
                            CollectionChangedEvent.ChangeType.added,
                            Collections.unmodifiableCollection(source)));
        }
        return add;
    }

    @Override
    public boolean remove(Object o) {
        boolean remove =source.remove(ObservableFactory.makeObservable(prefix, observableModel, o));
        if (remove) {
            observableModel.fireCollectionChangeListener(prefix,
                    new CollectionChangedEvent(
                            o,
                            CollectionChangedEvent.ChangeType.removed,
                            Collections.unmodifiableCollection(source)));
        }
        return remove; //TODO check
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return source.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        List<E> newCollection = c
                .stream()
                .map(e -> ObservableFactory.makeObservable(prefix + COLLECTION_PREFIX, observableModel, e))
                .collect(Collectors.toList());
        boolean addAll = source.addAll(newCollection);
        if (addAll) {
            observableModel.fireCollectionChangeListener(prefix,
                    new CollectionChangedEvent(
                            c,
                            CollectionChangedEvent.ChangeType.multipleAdd,
                            Collections.unmodifiableCollection(source)));
        }
        return addAll;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removeAll = source.removeAll(c);
        if (removeAll) {
            observableModel.fireCollectionChangeListener(prefix,
                    new CollectionChangedEvent(
                            c,
                            CollectionChangedEvent.ChangeType.multipeRemove,
                            Collections.unmodifiableCollection(source)));
        }
        return removeAll;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean retainAll = source.retainAll(c);
        if (retainAll) {
            observableModel.fireCollectionChangeListener(prefix ,
                    new CollectionChangedEvent(
                            null,
                            CollectionChangedEvent.ChangeType.multipeRemove,
                            Collections.unmodifiableCollection(source)));
        }
        return retainAll;
    }

    @Override
    public void clear() {
        source.clear();
        observableModel.fireCollectionChangeListener(prefix,
                new CollectionChangedEvent(
                        null,
                        CollectionChangedEvent.ChangeType.multipeRemove,
                        Collections.unmodifiableCollection(source)));

    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        boolean removeIf = source.removeIf(filter);
        if (removeIf) {
            observableModel.fireCollectionChangeListener(prefix,
                    new CollectionChangedEvent(
                            null,
                            CollectionChangedEvent.ChangeType.multipeRemove,
                            Collections.unmodifiableCollection(source)));
        }
        return removeIf;
    }

    @Override
    public Spliterator<E> spliterator() {
        return source.spliterator();
    }

    @Override
    public Stream<E> stream() {
        return source.stream();
    }

    @Override
    public Stream<E> parallelStream() {
        return source.parallelStream();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        source.forEach(action);
    }

    @Override
    public Collection<E> getSource() {
        return source;
    }


    @Override
    public int hashCode() {
        return source.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        return source.equals(obj);
    }
}

