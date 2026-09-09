package com.sdet.framework.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.sdet.framework.ui.constants.UiConstants;

/** Storefront header and explicit waiting, shared by every signed-in page. */
public abstract class BasePage {
  protected final Page page;
  private final Locator cartLink;
  private final Locator cartBadge;

  protected BasePage(Page page) {
    this.page = page;
    cartLink = page.getByTestId(UiConstants.CART_LINK);
    cartBadge = page.getByTestId(UiConstants.CART_BADGE);
  }

  public String currentUrl() {
    return page.url();
  }

  /** The badge is absent rather than zero when the cart is empty. */
  public int cartBadgeCount() {
    return cartBadge.count() == 0 ? 0 : Integer.parseInt(cartBadge.textContent().trim());
  }

  public CartPage openCart() {
    cartLink.click();
    return new CartPage(page).waitUntilLoaded();
  }

  protected Locator waitUntilVisible(Locator locator) {
    locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    return locator;
  }
}
