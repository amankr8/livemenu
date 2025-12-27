package com.flykraft.livemenu.dto.order;

import com.flykraft.livemenu.dto.customer.UserResDto;
import com.flykraft.livemenu.model.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderResponseDto {
    private Long id;
    private Long kitchenId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private UserResDto customerDetails;
    private List<OrderItemResponseDto> orderItems;
}
