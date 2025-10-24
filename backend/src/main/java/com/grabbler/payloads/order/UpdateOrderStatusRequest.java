package com.grabbler.payloads.order;

import com.grabbler.enums.OrderStatus;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOrderStatusRequest {
    @Valid
    @NotNull(message = "Payment details are required")
    private OrderStatus status;

}
