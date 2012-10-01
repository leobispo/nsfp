package com.charite.filter;

import java.util.concurrent.ExecutionException;

public interface FilterConsumer<IN> {
  public void consume(final IN in);
  public void shutdown() throws InterruptedException, ExecutionException;
}