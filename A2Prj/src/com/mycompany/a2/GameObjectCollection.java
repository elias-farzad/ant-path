package com.mycompany.a2;

import java.util.Vector;

/**
 * Collection of all game objects with a custom index-based iterator.
 */
public class GameObjectCollection implements ICollection {
    private final Vector<GameObject> store = new Vector<>();
    
    // remove all game objects
    public void clear() {
        store.removeAllElements();
    }

    // implements ICollection
    public void add(Object newObject) {
        if (newObject instanceof GameObject) {
            store.add((GameObject)newObject);
        } else {
            throw new IllegalArgumentException("Only GameObject allowed");
        }
    }

    // implements ICollection
    public IIterator getIterator() {
        return new GOCIterator();
    }

    // implements ICollection
    public int size() { return store.size(); }

    /**
     * Custom iterator that traverses by index. No built-in Java Iterator used.
     */
    private class GOCIterator implements IIterator {
        private int index = 0;

        // implements IIterator
        public boolean hasNext() {
            return index < store.size();
        }

        // implements IIterator
        public Object getNext() {
            if (!hasNext()) return null;
            return store.get(index++);
        }

        // implements IIterator
        public void reset() { index = 0; }
    }
}
