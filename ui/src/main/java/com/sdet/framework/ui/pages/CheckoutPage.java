package com.sdet.framework.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.sdet.framework.ui.constants.UiConstants;
import com.sdet.framework.ui.models.CheckoutData;

/** Shipping details, order overview and confirmation. */
public class CheckoutPage extends BasePage {
  private final Locator firstName;
  private final Locator lastName;
  private final Locator postalCode;
  private final Locator continueButton;
  private final Locator finishButton;
  private final Locator confirmationHeader;
  private final Locator confirmationText;
  private final Locator error;

  public CheckoutPage(Page page) {
    super(page);
    firstName = page.getByTestId(UiConstants.FIRST_NAME);
    lastName = page.getByTestId(UiConstants.LAST_NAME);
    postalCode = page.getByTestId(UiConstants.POSTAL_CODE);
    continueButton = page.getByTestId(UiConstants.CONTINUE_BUTTON);
    finishButton = page.getByTestId(UiConstants.FINISH_BUTTON);
    confirmationHeader = page.getByTestId(UiConstants.CONFIRMATION_HEADER);
    confirmationText = page.getByTestId(UiConstants.CONFIRMATION_TEXT);
    error = page.getByTestId(UiConstants.ERROR_MESSAGE);
  }

  public CheckoutPage waitUntilDetailsLoaded() {
    waitUntilVisible(continueButton);
    return this;
  }

  /** Null fields are skipped, so a test can omit a mandatory value. */
  public CheckoutPage fillShippingDetails(CheckoutData data) {
    if (data.getFirstName() != null) firstName.fill(data.getFirstName());
    if (data.getLastName() != null) lastName.fill(data.getLastName());
    if (data.getPostalCode() != null) postalCode.fill(data.getPostalCode());
    return this;
  }

  public CheckoutPage continueToOverview() {
    continueButton.click();
    waitUntilVisible(finishButton);
    return this;
  }

  /** Submits without waiting for the overview, for flows expected to be rejected. */
  public CheckoutPage submitShippingDetails() {
    continueButton.click();
    return this;
  }

  public CheckoutPage finishOrder() {
    finishButton.click();
    waitUntilVisible(confirmationHeader);
    return this;
  }

  public String confirmationHeader() {
    return waitUntilVisible(confirmationHeader).textContent().trim();
  }

  public String confirmationText() {
    return waitUntilVisible(confirmationText).textContent().trim();
  }

  public String errorMessage() {
    return waitUntilVisible(error).textContent().trim();
  }
}
