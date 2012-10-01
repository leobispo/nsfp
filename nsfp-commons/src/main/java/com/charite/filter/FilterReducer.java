package com.charite.filter;

import java.util.concurrent.ConcurrentLinkedQueue;

public interface FilterReducer<IN> {
  public abstract void reduce(final String key, final ConcurrentLinkedQueue<IN> in);
}
