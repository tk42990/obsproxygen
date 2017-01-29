package org.obsproxygen.observable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by thku on 29.12.16.
 */
public class ObservableCollection<E> implements Collection<E> {

    private Collection<E> source;
    // todo path to sub model bean

    //TODO copy to other path !!!!!!!!!!!!!

    public ObservableCollection(Collection<E> source) {
        this.source = source;
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
        return source.contains(o); //TODO
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
        return source.add(e); //TODO
    }

    @Override
    public boolean remove(Object o) {
        return source.remove(o); //TODO
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return source.containsAll(c); //TODO
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return source.addAll(c); //TODO
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return source.removeAll(c); //TODO
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return source.retainAll(c); //TODO
    }

    @Override
    public void clear() {
        source.clear();
    }

    @Override
    public boolean removeIf(Predicate<? super E> filter) {
        return source.removeIf(filter); //TODO
    }

    @Override
    public Spliterator<E> spliterator() {
        return source.spliterator();
    }

    @Override
    public Stream<E> stream() {
        return source.stream(); //TODO
    }

    @Override
    public Stream<E> parallelStream() {
        return source.parallelStream(); // todo
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        source.forEach(action);
    }
}
