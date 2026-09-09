package com.sdet.framework.ui.data;

import com.sdet.framework.ui.models.CheckoutData;

/**
 * Test data for the SauceDemo suites. Valid credentials come from configuration; only the
 * deliberately wrong or incomplete values are fixed here.
 */
public final class SauceDemoTestData {

  public static final String UNREGISTERED_USERNAME = "not_a_registered_user";

  public static final String UNREGISTERED_PASSWORD = "not_a_valid_password";

  public static final int MULTI_ITEM_PRODUCT_COUNT = 3;

  /** Product the single-item journey selects by name. */
  public static final String TARGET_PRODUCT = "Sauce Labs Backpack";

  private SauceDemoTestData() {}

  public static CheckoutData validShippingDetails() {
    return CheckoutData.builder()
        .firstName("SDET")
        .lastName("Candidate")
        .postalCode("560001")
        .build();
  }

  /** First name left null, so the page object skips it and the form reports it missing. */
  public static CheckoutData shippingDetailsMissingFirstName() {
    return CheckoutData.builder().lastName("Candidate").postalCode("560001").build();
  }
}
