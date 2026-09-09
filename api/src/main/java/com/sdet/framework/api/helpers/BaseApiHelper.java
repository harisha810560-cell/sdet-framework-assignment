package com.sdet.framework.api.helpers;

import com.sdet.framework.api.model.ApiTestContext;
import com.sdet.framework.commons.base.ServiceHelper;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@SuperBuilder
public abstract class BaseApiHelper implements ServiceHelper {

  /** Replaced with an enriched copy when a stage captures a response a later stage asserts on. */
  protected ApiTestContext apiTestContext;
}
