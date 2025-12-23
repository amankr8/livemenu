package com.flykraft.menu_service.model;

import com.flykraft.menu_service.dto.MenuItemResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kitchen_id")
    private Kitchen kitchen;

    @Column(name = "item_name")
    private String name;

    @Column(name = "item_desc")
    private String desc;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_category")
    private Category category;

    @Column(name = "item_in_stock")
    private Boolean inStock;

    @Column(name = "item_is_veg")
    private Boolean isVeg;

    @Column(name = "item_price")
    private BigDecimal price;

    public MenuItemResponseDto toResponseDto(){
        return MenuItemResponseDto.builder()
                .id(this.id)
                .name(this.name)
                .desc(this.desc)
                .kitchenId(this.kitchen.getId())
                .category(this.category)
                .inStock(this.inStock)
                .isVeg(this.isVeg)
                .price(this.price)
                .build();
    }
}
