package com.sdet.framework.ui.helpers.saucedemo;

import com.sdet.framework.commons.base.ServiceHelper;
import com.sdet.framework.ui.helpers.BaseUiHelper;
import com.sdet.framework.ui.pages.InventoryPage;
import com.sdet.framework.ui.pages.LoginPage;
import com.sdet.framework.ui.validators.SauceDemoValidator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@SuperBuilder
public class LoginHelper extends BaseUiHelper {
  private LoginPage loginPage;
  private InventoryPage inventoryPage;

  @Override
  public ServiceHelper init() {
    loginPage = new LoginPage(page).open(uiTestContext.getBaseUrl());
    log.info("Login page opened at {}", uiTestContext.getBaseUrl());
    return this;
  }

  @Override
  public ServiceHelper process() {
    inventoryPage = loginPage.signIn(uiTestContext.getUsername(), uiTestContext.getPassword());
    log.info("Signed in as {}", uiTestContext.getUsername());
    return this;
  }

  @Override
  public ServiceHelper validate() {
    SauceDemoValidator.loginWasSuccessful(inventoryPage.isLoaded(), inventoryPage.currentUrl());
    return this;
  }
}
