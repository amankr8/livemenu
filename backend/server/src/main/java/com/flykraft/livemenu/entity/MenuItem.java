package com.flykraft.livemenu.entity;

import com.flykraft.livemenu.dto.menu.MenuItemResponseDto;
import com.flykraft.livemenu.model.Auditable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Filter(name = "kitchenFilter", condition = "k_id = :kitchenId")
@Entity
@Table(name = "menu_items")
public class MenuItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mi_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "k_id", nullable = false)
    private Kitchen kitchen;

    @Column(name = "mi_name", nullable = false)
    private String name;

    @Column(name = "mi_desc")
    private String desc;

    @Column(name = "mi_category")
    private String category;

    @Column(name = "mi_in_stock", nullable = false)
    private Boolean inStock;

    @Column(name = "mi_is_veg", nullable = false)
    private Boolean isVeg;

    @Column(name = "mi_price", nullable = false)
    private BigDecimal price;

    @OneToOne(mappedBy = "menuItem", cascade = CascadeType.ALL)
    private DishImage dishImage;

    public MenuItemResponseDto toResponseDto(){
        return MenuItemResponseDto.builder()
                .id(this.id)
                .name(this.name)
                .desc(this.desc)
                .category(this.category)
                .inStock(this.inStock)
                .isVeg(this.isVeg)
                .price(this.price)
                .imageUrl(this.dishImage == null ? null : this.dishImage.getSecureUrl())
                .build();
    }
}
