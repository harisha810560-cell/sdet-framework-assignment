package com.sdet.framework.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.sdet.framework.ui.constants.UiConstants;
import com.sdet.framework.ui.models.Product;
import java.util.List;
import java.util.stream.IntStream;

public class InventoryPage extends BasePage {
  private final Locator container;
  private final Locator items;

  public InventoryPage(Page page) {
    super(page);
    container = page.getByTestId(UiConstants.INVENTORY_CONTAINER);
    items = page.getByTestId(UiConstants.INVENTORY_ITEM);
  }

  public InventoryPage waitUntilLoaded() {
    waitUntilVisible(container);
    return this;
  }

  public boolean isLoaded() {
    return container.isVisible();
  }

  public int productCount() {
    return items.count();
  }

  /** Selects a named product, so the test asserts against a product it actually chose. */
  public Product addProductToCart(String productName) {
    Locator matches = items.filter(new Locator.FilterOptions().setHasText(productName));
    int found = matches.count();
    if (found != 1) {
      throw new IllegalStateException(
          "Expected exactly one product matching '" + productName + "' but found " + found);
    }
    return addToCart(matches.first());
  }

  /** Positional selection, used by the multi-item case where the names do not matter. */
  public Product addProductToCart(int index) {
    return addToCart(items.nth(index));
  }

  public List<Product> addProductsToCart(int count) {
    return IntStream.range(0, count).mapToObj(this::addProductToCart).toList();
  }

  private Product addToCart(Locator item) {
    Product product =
        Product.builder()
            .name(item.getByTestId(UiConstants.INVENTORY_ITEM_NAME).textContent().trim())
            .price(item.getByTestId(UiConstants.INVENTORY_ITEM_PRICE).textContent().trim())
            .build();
    item.locator(UiConstants.ADD_TO_CART_CSS).click();
    return product;
  }
}
