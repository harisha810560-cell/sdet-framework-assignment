package com.sdet.framework.ui.helpers;

import com.microsoft.playwright.Page;
import com.sdet.framework.commons.base.ServiceHelper;
import com.sdet.framework.ui.models.UiTestContext;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@SuperBuilder
public abstract class BaseUiHelper implements ServiceHelper {
  protected final Page page;

  /** Replaced with an enriched copy when a stage discovers state a later stage asserts on. */
  protected UiTestContext uiTestContext;
}
