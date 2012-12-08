package com.charite.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;

public final class FilterStopWhenMatchExecutor <IN, OUT extends MapReduceKey> implements FilterConsumer<IN> {
  private List<Filter<IN, OUT>> filters = null;
  private final List<Future<Boolean>> futures = new ArrayList<>();

  private FilterConsumer<OUT> nextConsumerChain = null;
  
  private FilterMapReduce<OUT> nextMapReduceChain = null;

  @Autowired
  AsyncTaskExecutor executor;

  @Override
  public void consume(final IN in, Method method) {
    if (method == Method.Async) {
      Future<Boolean> future = executor.submit(new Callable<Boolean>() {
        @Override
        public Boolean call() {
          return execute(in);
        }
      });
      futures.add(future);
    }
    else
      execute(in);
  }
  
  public void shutdown() throws InterruptedException, ExecutionException {
    for (Future<Boolean> future : futures)
      future.get();

    if (nextMapReduceChain != null)
      nextMapReduceChain.reduce();

    if (nextConsumerChain != null)
      nextConsumerChain.shutdown();
  }
  
  public void setFilters(List<Filter<IN, OUT>> filters) {
    this.filters = filters;
  }

  public void setNextConsumerChain(FilterConsumer<OUT> nextConsumerChain) {
    this.nextConsumerChain = nextConsumerChain;
  }

  public void setNextMapReduceChain(FilterMapReduce<OUT> nextMapReduceChain) {
    this.nextMapReduceChain = nextMapReduceChain;
  }
  
  private boolean execute(final IN in) {
    OUT out = null;
    for (Filter<IN, OUT> filter : filters) {
      out = filter.processFilter(in);
      if (out != null) {
        if (nextConsumerChain != null)
          nextConsumerChain.consume(out, Method.Sync);

        if (nextMapReduceChain != null)
          nextMapReduceChain.map(out.key(), out);
      
        return true;
      }
    }
    return false;
  }
}