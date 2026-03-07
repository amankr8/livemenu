package com.flykraft.livemenu.dto.menu;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class MenuItemRequestDto {
    private String name;
    private String desc;
    private String category;
    private Boolean isVeg;
    private BigDecimal price;
    private MultipartFile image;
}
