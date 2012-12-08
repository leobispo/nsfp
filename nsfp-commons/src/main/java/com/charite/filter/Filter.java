package com.charite.filter;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Filter<IN, OUT> {
  @SuppressWarnings("unused")
  private final String filterName            = getFilterName();
  @SuppressWarnings("unused")
  private final String description           = getDescription();
  private final AtomicLong elementsProcessed = new AtomicLong();
  private final AtomicLong  elementsFiltered = new AtomicLong();
  private int position                       = 0;
  
  public abstract String getFilterName();
  
  public abstract String getDescription();

  public final long getElementsProcessed() {
    return elementsProcessed.get();
  }

  public final long getElementsFiltered() {
    return elementsFiltered.get();
  }
  
  public final void setPosition(int position) {
    this.position = position;
  }
  
  public final int getPosition() {
    return position;
  }
  final OUT processFilter(final IN in, Object... arguments) {
    elementsProcessed.incrementAndGet();
    OUT out = filter(in, arguments);
    
    if (out == null)
      elementsFiltered.incrementAndGet();
    
    return out;
  }
  
  public abstract OUT filter(final IN in, Object... arguments);
  public abstract boolean setParameter(final String parameter);
  
  //TODO: MUST IMPLEMENT THE EQUALS!!!
}
