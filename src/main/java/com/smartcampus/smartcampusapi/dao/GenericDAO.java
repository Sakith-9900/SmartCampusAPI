package com.smartcampus.smartcampusapi.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GenericDAO<T> {

    // ConcurrentHashMap ensures thread safety
    protected final Map<String, T> store = new ConcurrentHashMap<>();

    // Save or update an entity by its ID
    public void save(String id, T entity) {
        store.put(id, entity);
    }

    // Find a single entity by its ID
    public T findById(String id) {
        return store.get(id);
    }

    // Return all stored entities
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }
    //  Delete an entity by ID
    public boolean deleteById(String id) {
        return store.remove(id) != null;
    }
    //  Check if an entity exists by ID
    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}