package cn.evolvefield.mods.morechickens.common.util.math;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CachedMap<K, V>{
    private final Map<K, CachedMap<K, V>.ValueWrapper> cache;
    private final long lifespan;
    private long lastCheck;

    protected CachedMap(Map<K, CachedMap<K, V>.ValueWrapper> map, long lifespan) {
        this.lifespan = lifespan;
        this.cache = map;
    }

    public CachedMap(long lifespan, Comparator<K> comparator) {
        this(new TreeMap(comparator), lifespan);
    }

    public CachedMap(long lifespan) {
        this(new HashMap(), lifespan);
    }

    public CachedMap() {
        this(-1L);
    }

    public V get(K key, Supplier<V> valueSupplier) {
        Object value;
        if (this.cache.containsKey(key)) {
            value = ((CachedMap.ValueWrapper)this.cache.get(key)).getValue();
        } else {
            value = valueSupplier.get();
            this.cache.put(key, new CachedMap.ValueWrapper(value));
        }

        this.cleanup();
        return (V)value;
    }

    private void cleanup() {
        if (this.lifespan >= 0L) {
            long time = System.currentTimeMillis();
            if (time - this.lastCheck > this.lifespan) {
                Collection<K> collect = (Collection)this.cache.entrySet().stream().filter((kValueWrapperEntry) -> {
                    return ((CachedMap.ValueWrapper)kValueWrapperEntry.getValue()).checkInvalid(time);
                }).map(Map.Entry::getKey).collect(Collectors.toSet());
                this.cache.keySet().removeAll(collect);
                this.lastCheck = time;
            }

        }
    }

    public boolean has(K key) {
        return this.cache.containsKey(key);
    }

    public void remove(K key) {
        this.cache.remove(key);
    }

    public void clear() {
        this.cache.clear();
    }

    private class ValueWrapper {
        private V value;
        private long accessTimestamp;

        public ValueWrapper(V value) {
            this.value = value;
            this.accessTimestamp = System.currentTimeMillis();
        }

        public boolean checkInvalid(long currentTime) {
            if (CachedMap.this.lifespan >= 0L) {
                return currentTime - this.accessTimestamp > CachedMap.this.lifespan;
            } else {
                return false;
            }
        }

        public V getValue() {
            this.accessTimestamp = System.currentTimeMillis();
            return this.value;
        }
    }
}
