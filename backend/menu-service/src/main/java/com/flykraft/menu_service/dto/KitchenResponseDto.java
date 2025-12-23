package com.flykraft.menu_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KitchenResponseDto {
    private Long id;
    private String name;
    private String subdomain;
    private String address;
    private String whatsapp;
}
