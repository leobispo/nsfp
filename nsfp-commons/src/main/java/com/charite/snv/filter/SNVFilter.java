package com.charite.snv.filter;

// TODO: Maybe this should be an abstract class, so I can set all other filters!!
public interface SNVFilter {
  public String filterName();
  public String filterDescription();
  public boolean hasArgs();
}
