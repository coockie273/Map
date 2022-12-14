package lesson6.homework.src.HashMap;

import lesson6.homework.src.Helpers.*;

import java.util.Optional;

import lesson6.homework.src.Search.*;


//__________________________  HashMap with linear collision resolution method  _________________//

public class HashMap {
    private HashMapElement[] table;
    private double loadFactor;
    private int capacity;
    private int size;
    private AbstractSearch searchStrategy;      //strategy pattern

    private HashMap() {
    }

    public int getSize() {
        return this.size;
    }

    public int getCapacity() {
        return this.capacity;
    }

    //Builder to create HashMap with linear search strategy

    public static LinearBuilder linearBuilder() {
        return new HashMap().new LinearBuilder();
    }

    //Builder to create HashMap with consistent search strategy

    public static ConsistentBuilder consistentBuilder() {
        return new HashMap().new ConsistentBuilder();
    }

    //Builder to create HashMap with quadratic search strategy

    public static QuadraticBuilder quadraticBuilder() {
        return new HashMap().new QuadraticBuilder();
    }

    abstract public class Builder {

        //default value for each Hash-map

        private Builder() {
            HashMap.this.loadFactor = 0.75;
            HashMap.this.capacity = 10;
        }


        public HashMap build() {
            HashMap.this.size = 0;
            HashMap.this.table = new HashMapElement[HashMap.this.capacity];
            return HashMap.this;
        }

        abstract public Builder setCapacity(int capacity);

        abstract public Builder setLoadFactor(int loadFactor);

    }

    public class LinearBuilder extends Builder {
        private LinearBuilder() {
            super();
            HashMap.this.searchStrategy = new LinearSearch();
        }

        public LinearBuilder setCapacity(int capacity) {
            HashMap.this.capacity = capacity;
            return this;
        }

        public LinearBuilder setLoadFactor(int loadFactor) {
            HashMap.this.loadFactor = loadFactor;
            return this;
        }

        //the property that only the linear strategy has

        public LinearBuilder setStep(int step) {
            HashMap.this.searchStrategy.setStep(step);
            return this;
        }
    }

    public class ConsistentBuilder extends Builder {
        private ConsistentBuilder() {
            super();
            HashMap.this.searchStrategy = new ConsistentSearch();
        }

        public ConsistentBuilder setCapacity(int capacity) {
            HashMap.this.capacity = capacity;
            return this;
        }

        public ConsistentBuilder setLoadFactor(int loadFactor) {
            HashMap.this.loadFactor = loadFactor;
            return this;
        }
    }

    public class QuadraticBuilder extends Builder {
        private QuadraticBuilder() {
            super();
            HashMap.this.searchStrategy = new QuadraticSearch();
        }

        public QuadraticBuilder setCapacity(int capacity) {
            HashMap.this.capacity = capacity;
            return this;
        }

        public QuadraticBuilder setLoadFactor(int loadFactor) {
            HashMap.this.loadFactor = loadFactor;
            return this;
        }
    }


    private void resizeIfTableIsFull(int newCapacity) {
        if ((double) this.size / this.capacity > this.loadFactor) {
            HashMapElement[] copyTable = new HashMapElement[newCapacity];
            HashMapElement[] oldTable = this.table;
            int oldCapacity = this.capacity;
            this.capacity = newCapacity;
            this.table = copyTable;
            for (int i = 0; i < oldCapacity; i++) {
                if (oldTable[i] != null) {
                    this.put(oldTable[i].getKey(), oldTable[i].getValue());
                    this.size--;
                }
            }
        }
    }

    public void put(String s, Point p) {
        if (this.contains(s)) {
            return;
        }
        HashMapElement el = new HashMapElement(s, p);
        int hash;
        hash = searchStrategy.indexForPutting(el.getHashCode(), this.table, this.capacity);
        this.table[hash % this.capacity] = el;
        this.size++;
        this.resizeIfTableIsFull(2 * this.capacity);
    }

    public Point get(String key) {
        int index = this.searchStrategy.search(key, this.table, this.capacity);
        if (index == -1) {
            return null;
        }
        return this.table[index].getValue();
    }

    public Optional<Point> getSafe(String key) {
        return Optional.ofNullable(this.get(key));
    }

    public Point getOrElse(String key, Point def) {
        int index = this.searchStrategy.search(key, this.table, this.capacity);
        if (index == -1) {
            return def;
        }
        return this.table[index].getValue();
    }

    public Optional<Point> remove(String key) {
        int index = this.searchStrategy.search(key, this.table, this.capacity);
        if (index == -1) {
            return Optional.empty();
        }
        Point removed = this.table[index].getValue();

        //Tombstone - link to special HashMapElement, it helps to realise correct removing elements with the same hash
        this.table[index] = HashMapElement.TOMBSTONE;
        this.size--;
        return Optional.ofNullable(removed);
    }

    public boolean contains(String key) {
        return searchStrategy.search(key, table, capacity) != -1;
    }

}


