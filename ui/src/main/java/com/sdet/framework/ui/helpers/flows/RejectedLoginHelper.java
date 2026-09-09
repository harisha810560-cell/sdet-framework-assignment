package com.sdet.framework.ui.helpers.flows;

import com.sdet.framework.commons.base.ServiceHelper;
import com.sdet.framework.ui.helpers.BaseUiHelper;
import com.sdet.framework.ui.pages.LoginPage;
import com.sdet.framework.ui.validators.SauceDemoValidator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/** Logins that must fail. The caller supplies the expected message. */
@Getter
@Slf4j
@SuperBuilder
public class RejectedLoginHelper extends BaseUiHelper {
  private final String expectedError;
  private LoginPage loginPage;
  private String actualError;

  @Override
  public ServiceHelper init() {
    loginPage = new LoginPage(page).open(uiTestContext.getBaseUrl());
    return this;
  }

  @Override
  public ServiceHelper process() {
    loginPage.submitCredentials(uiTestContext.getUsername(), uiTestContext.getPassword());
    actualError = loginPage.errorMessage();
    log.info("Login rejected for {} with '{}'", uiTestContext.getUsername(), actualError);
    return this;
  }

  @Override
  public ServiceHelper validate() {
    SauceDemoValidator.loginWasRejected(actualError, expectedError);
    return this;
  }
}
