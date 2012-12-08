package com.charite.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;

public class FilterMapReduce<IN> {
  private final ConcurrentHashMap<String, ConcurrentLinkedQueue<IN>> map  = new ConcurrentHashMap<>();
  
  private FilterReducer<IN> reducer;
  private final List<Future<Boolean>> futures = new ArrayList<>();
  
  @Autowired
  AsyncTaskExecutor executor;
  
  public void map(String key, IN in) {
    ConcurrentLinkedQueue<IN> queue = map.putIfAbsent(key, new ConcurrentLinkedQueue<IN>());
    if (queue == null)
      queue = map.get(key);
      
    queue.add(in);
  }
  
  public void reduce() throws InterruptedException, ExecutionException {
    
    if (reducer != null) {
      for (final Entry<String, ConcurrentLinkedQueue<IN>> entry : map.entrySet()) {
        Future<Boolean> future = executor.submit(new Callable<Boolean>() {
          @Override
          public Boolean call() {
            reducer.reduce(entry.getKey(), entry.getValue());
            return true;
          }
        });
        futures.add(future);
      }
    }
    
    for (Future<Boolean> future : futures)
      future.get();
  }

  public void setReducer(FilterReducer<IN> reducer) {
    this.reducer = reducer;
  }
}
