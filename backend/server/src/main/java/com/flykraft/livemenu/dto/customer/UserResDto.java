package com.flykraft.livemenu.dto.customer;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResDto {
    private Long id;
    private String name;
    private String phone;
    private String address;
}
