package com.charite.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;

public abstract class FilterMapReduce<IN> {
  private final ConcurrentHashMap<String, ConcurrentLinkedQueue<IN>> map  = new ConcurrentHashMap<>();
  private final AtomicReference<ConcurrentLinkedQueue<IN>> cachedQueue    = new AtomicReference<>();
  
  private FilterReducer<IN> reducer;
  private final List<Future<Boolean>> futures = new ArrayList<>();
  
  @Autowired
  AsyncTaskExecutor executor;
  
  public void map(String key, IN in) {
    ConcurrentLinkedQueue<IN> queue;
    cachedQueue.compareAndSet(null, new ConcurrentLinkedQueue<IN>());
    cachedQueue.compareAndSet(queue = map.putIfAbsent(key, cachedQueue.get()), null);
    
    queue.add(in);
  }
  
  public void reduce() throws InterruptedException, ExecutionException {
    if (reducer != null) {
      for (final Entry<String, ConcurrentLinkedQueue<IN>> entry : map.entrySet()) {
        reducer.reduce(entry.getKey(), entry.getValue());
      }
    }
    for (Future<Boolean> future : futures)
      future.get();
  }
}
