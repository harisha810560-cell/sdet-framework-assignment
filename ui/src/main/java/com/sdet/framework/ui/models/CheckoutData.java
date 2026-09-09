package com.sdet.framework.ui.models;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CheckoutData {
  String firstName;
  String lastName;
  String postalCode;
}
