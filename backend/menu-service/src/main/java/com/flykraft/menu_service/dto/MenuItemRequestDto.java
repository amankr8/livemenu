package com.flykraft.menu_service.dto;

import com.flykraft.menu_service.model.Category;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MenuItemRequestDto {
    private String name;
    private String desc;
    private Category category;
    private BigDecimal price;
}
