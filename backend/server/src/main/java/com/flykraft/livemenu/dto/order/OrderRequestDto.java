package com.flykraft.livemenu.dto.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {
    private String guestName;
    private String guestPhone;
    private String guestAddress;
    private List<OrderItemRequestDto> orderItems;
}
