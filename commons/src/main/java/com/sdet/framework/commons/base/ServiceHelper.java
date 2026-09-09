package com.sdet.framework.commons.base;

/** Lifecycle used by every UI and API flow helper. */
public interface ServiceHelper {
  ServiceHelper init();

  ServiceHelper process();

  ServiceHelper validate();

  default ServiceHelper test() {
    init();
    process();
    validate();
    return this;
  }
}
