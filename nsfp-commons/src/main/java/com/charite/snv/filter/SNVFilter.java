package com.charite.snv.filter;

public interface SNVFilter { // TODO: Maybe this should be an abstract class, so I can set all other filters!!
  public String filterName();
  public String filterDescription();
  public boolean hasArgs();
  
}
