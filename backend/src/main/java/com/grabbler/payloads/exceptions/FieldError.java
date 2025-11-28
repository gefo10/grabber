package com.grabbler.payloads.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldError {
  private String field;
  private String message;
  private Object rejectedValue;

  public FieldError(String field, String message) {
    this.field = field;
    this.message = message;
  }
}
