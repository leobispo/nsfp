package com.charite.filter;

import java.util.concurrent.ExecutionException;

public interface FilterConsumer<IN> {
  public enum Method { Sync, Async };

  public void consume(final IN in, Method method);
  public void shutdown() throws InterruptedException, ExecutionException;
}