package com.sdet.framework.ui.models;

import java.util.List;
import lombok.Builder;
import lombok.Value;

/** Immutable contract passed between the stages of a UI flow. */
@Value
@Builder(toBuilder = true)
public class UiTestContext {
  String baseUrl;
  String username;
  String password;

  /** Product the shopper intends to buy, selected by name on the inventory page. */
  String productName;

  CheckoutData checkoutData;
  Product selectedProduct;
  List<Product> selectedProducts;
}
