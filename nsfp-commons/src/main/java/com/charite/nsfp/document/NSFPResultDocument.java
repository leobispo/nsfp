package com.charite.nsfp.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.charite.filter.Filter;
import com.charite.nsfp.model.NSFP;
import com.charite.snv.model.SNV;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("NSFPResult")
public final class NSFPResultDocument {
  @XStreamOmitField
  private final Lock lock = new ReentrantLock();

  private List<String> samples                        = null;
  private List<Filter<SNV, SNV>> snvFilters           = null;
  private List<Filter<SNV, NSFP>> nsfpFilters         = null;
  private List<Filter<NSFP, NSFP>> inheritanceFilters = null;
  private final List<NSFP> NSFPs                      = new ArrayList<NSFP>();

  public void addNSFPS(final Collection<NSFP> nsfps) {
    lock.lock();
    try {
      NSFPs.addAll(nsfps);
    }
    finally {
      lock.unlock();
    }
  }

  public List<NSFP> getNSFPs() {
    lock.lock();
    try {
      return NSFPs;
    }
    finally {
      lock.unlock();
    }
  }

  public List<String> getSamples() {
    lock.lock();
    try {
      return samples;
    }
    finally {
      lock.unlock();
    }
  }

  public void setSamples(final List<String> samples) {
    lock.lock();
    try {
      this.samples = samples;
    }
    finally {
      lock.unlock();
    }
  }

  public List<Filter<SNV, SNV>> getSnvFilters() {
    lock.lock();
    try {
      return snvFilters;
    }
    finally {
      lock.unlock();
    }
  }

  public void setSnvFilters(final List<Filter<SNV, SNV>> snvFilters) {
    lock.lock();
    try {
      this.snvFilters = snvFilters;
    }
    finally {
      lock.unlock();
    }
  }
  
  public List<Filter<SNV, NSFP>> getNsfpFilters() {
    lock.lock();
    try {
      return nsfpFilters;
    }
    finally {
      lock.unlock();
    }
  }

  public void setNsfpFilters(List<Filter<SNV, NSFP>> nsfpFilters) {
    lock.lock();
    try {
      this.nsfpFilters = nsfpFilters;
    }
    finally {
      lock.unlock();
    }
  }

  public List<Filter<NSFP, NSFP>> getInheritanceFilters() {
    lock.lock();
    try {
      return inheritanceFilters;
    }
    finally {
      lock.unlock();
    }
  }

  public void setInheritanceFilters(List<Filter<NSFP, NSFP>> inheritanceFilters) {
    lock.lock();
    try {
      this.inheritanceFilters = inheritanceFilters;
    }
    finally {
      lock.unlock();
    }
  }
}