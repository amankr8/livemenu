package com.flykraft.livemenu.dto.menu;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class MenuItemResponseDto {
    private Long id;
    private String name;
    private String desc;
    private Long categoryId;
    private Boolean inStock;
    private Boolean isVeg;
    private BigDecimal price;
    private String imageUrl;
}
