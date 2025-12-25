package com.flykraft.livemenu.dto.menu;

import com.flykraft.livemenu.model.Category;
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
    private Long kitchenId;
    private Category category;
    private Boolean inStock;
    private Boolean isVeg;
    private BigDecimal price;
    private String imageUrl;
}
