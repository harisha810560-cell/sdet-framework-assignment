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
  CheckoutData checkoutData;
  Product selectedProduct;
  List<Product> selectedProducts;
}
