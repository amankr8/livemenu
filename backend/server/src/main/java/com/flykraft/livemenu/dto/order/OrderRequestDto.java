package com.flykraft.livemenu.dto.order;

import com.flykraft.livemenu.dto.customer.CustomerReqDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequestDto {
    private CustomerReqDto customerDetails;
    private List<OrderItemRequestDto> orderItems;
}
