package com.flykraft.livemenu.dto.kitchen;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KitchenResponseDto {
    private Long id;
    private String name;
    private String tagline;
    private String address;
    private String whatsapp;
}
