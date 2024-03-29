package com.safjnest.Utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;



public class CacheMap<K, V> extends HashMap<K, V>{
    /**
     * the default time in milliseconds after which an entry should be removed
     */
    private final long DEFAULT_EXPIRATION_MILLISECOND;
    /**
     * the maximum increment of time in milliseconds after which an entry should be removed
     */
    private final long MAX_EXPIRATION_MILLISECOND;
    
    /**
     * the scheduler used to schedule the removal of entries
     */
    private ScheduledExecutorService scheduler;
    /**
     * the scheduled tasks for the removal of entries
     */
    private Map<K, ScheduledFuture<?>> scheduledTasks;

    public CacheMap(long DEFAULT_EXPIRATION_MILLISECOND, long MAX_EXPIRATION_MILLISECOND) {
        super();
        
        this.DEFAULT_EXPIRATION_MILLISECOND = DEFAULT_EXPIRATION_MILLISECOND;
        this.MAX_EXPIRATION_MILLISECOND = MAX_EXPIRATION_MILLISECOND;
        
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduledTasks = new HashMap<>();
    }

    public CacheMap() {
        super();
        
        this.DEFAULT_EXPIRATION_MILLISECOND = 12L * 60 * 60 * 1000;
        this.MAX_EXPIRATION_MILLISECOND = 24L * 60 * 60 * 1000;

        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduledTasks = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        V value = super.get(key);
        if (value != null) {
            updateTime((K) key);
        }
        return value;
    }

    @Override
    public V put(K key, V value) {
        updateTime(key);
        return super.put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object key) {
        cancelScheduledTask((K)key);
        return super.remove(key);
    }

    private void updateTime(K key) {
        long def_time = DEFAULT_EXPIRATION_MILLISECOND;
        ScheduledFuture<?> currentTask = scheduledTasks.get(key);
        if (currentTask != null) {
            def_time = currentTask.getDelay(TimeUnit.MILLISECONDS);
            cancelScheduledTask(key);
        }
        ScheduledFuture<?> task = scheduler.schedule(() -> remove(key), calculateExpirationTime(def_time), TimeUnit.MILLISECONDS);
        scheduledTasks.put(key, task);
    }

    private void cancelScheduledTask(K key) {
        ScheduledFuture<?> task = scheduledTasks.get(key);
        if (task != null) {
            task.cancel(false);
            scheduledTasks.remove(key);
        }
    }

    private long calculateExpirationTime(long elapsedTime) {
        return Math.min(MAX_EXPIRATION_MILLISECOND, (long) (1000 * Math.log(elapsedTime + 1)));
    }

}
