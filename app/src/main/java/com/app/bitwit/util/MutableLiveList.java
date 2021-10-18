package com.app.bitwit.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.util.*;

public class MutableLiveList<T> extends MutableLiveData<List<T>> implements List<T> {
    
    public MutableLiveList( ) {
        super(new ArrayList<>( ));
    }
    
    public MutableLiveList(List<T> value) {
        super(value);
    }
    
    @Override
    public int size( ) {
        return getValue( ).size( );
    }
    
    @Override
    public boolean isEmpty( ) {
        return getValue( ).isEmpty( );
    }
    
    @Override
    public boolean contains(@Nullable Object o) {
        return getValue( ).contains(o);
    }
    
    @NonNull
    @Override
    public Iterator<T> iterator( ) {
        return getValue( ).iterator( );
    }
    
    @NonNull
    @Override
    public Object[] toArray( ) {
        return getValue( ).toArray( );
    }
    
    @NonNull
    @Override
    public <S> S[] toArray(@NonNull S[] a) {
        return (S[])getValue( ).toArray( );
    }
    
    @Override
    public boolean add(T t) {
        List<T> list   = getValue( );
        boolean result = list.add(t);
        postValue(list);
        return result;
    }
    
    @Override
    public boolean remove(@Nullable Object o) {
        List<T> list   = getValue( );
        boolean result = list.remove(o);
        postValue(list);
        return result;
    }
    
    @Override
    public boolean containsAll(@NonNull Collection<?> c) {
        return getValue( ).containsAll(c);
    }
    
    @Override
    public boolean addAll(@NonNull Collection<? extends T> c) {
        List<T> list   = getValue( );
        boolean result = list.addAll(c);
        postValue(list);
        return result;
    }
    
    @Override
    public boolean addAll(int index, @NonNull Collection<? extends T> c) {
        List<T> list   = getValue( );
        boolean result = list.addAll(index, c);
        postValue(list);
        return result;
    }
    
    @Override
    public boolean removeAll(@NonNull Collection<?> c) {
        List<T> list   = getValue( );
        boolean result = list.removeAll(c);
        postValue(list);
        return result;
    }
    
    @Override
    public boolean retainAll(@NonNull Collection<?> c) {
        List<T> list   = getValue( );
        boolean result = list.retainAll(c);
        postValue(list);
        return result;
    }
    
    @Override
    public void clear( ) {
        List<T> list = getValue( );
        list.clear( );
        postValue(list);
    }
    
    @Override
    public T get(int index) {
        return getValue( ).get(index);
    }
    
    @Override
    public T set(int index, T element) {
        List<T> list   = getValue( );
        T       result = list.set(index, element);
        postValue(list);
        return result;
    }
    
    @Override
    public void add(int index, T element) {
        List<T> list = getValue( );
        list.add(index, element);
        postValue(list);
    }
    
    @Override
    public T remove(int index) {
        List<T> list   = getValue( );
        T       result = list.remove(index);
        postValue(list);
        return result;
    }
    
    @Override
    public int indexOf(@Nullable Object o) {
        return getValue( ).indexOf(o);
    }
    
    @Override
    public int lastIndexOf(@Nullable Object o) {
        return getValue( ).lastIndexOf(o);
    }
    
    @NonNull
    @Override
    public ListIterator<T> listIterator( ) {
        return getValue( ).listIterator( );
    }
    
    @NonNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return getValue( ).listIterator(index);
    }
    
    @NonNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return getValue( ).subList(fromIndex, toIndex);
    }
}
