package com.sdet.framework.ui.helpers.flows;

import com.sdet.framework.commons.base.ServiceHelper;
import com.sdet.framework.ui.helpers.BaseUiHelper;
import com.sdet.framework.ui.pages.CartPage;
import com.sdet.framework.ui.pages.CheckoutPage;
import com.sdet.framework.ui.validators.SauceDemoValidator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/** Submits the shipping form with a mandatory field omitted. */
@Getter
@Slf4j
@SuperBuilder
public class RejectedCheckoutHelper extends BaseUiHelper {
  private final String expectedError;
  private CheckoutPage checkoutPage;
  private String actualError;

  @Override
  public ServiceHelper init() {
    checkoutPage = new CartPage(page).waitUntilLoaded().checkout();
    return this;
  }

  @Override
  public ServiceHelper process() {
    actualError =
        checkoutPage
            .fillShippingDetails(uiTestContext.getCheckoutData())
            .submitShippingDetails()
            .errorMessage();
    log.info("Checkout rejected with '{}'", actualError);
    return this;
  }

  @Override
  public ServiceHelper validate() {
    SauceDemoValidator.checkoutWasRejected(actualError, expectedError);
    return this;
  }
}
