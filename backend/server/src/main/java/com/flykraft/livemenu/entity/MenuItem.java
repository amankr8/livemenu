package com.flykraft.livemenu.entity;

import com.flykraft.livemenu.dto.menu.MenuItemResponseDto;
import com.flykraft.livemenu.model.Auditable;
import com.flykraft.livemenu.model.Category;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FilterDef(name = "kitchenFilter", parameters = @ParamDef(name = "kitchenId", type = Long.class))
@Filter(name = "kitchenFilter", condition = "k_id = :kitchenId")
@Entity
@Table(name = "menu_items")
public class MenuItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mi_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "k_id")
    private Kitchen kitchen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "di_id")
    private DishImage dishImage;

    @Column(name = "mi_name")
    private String name;

    @Column(name = "mi_desc")
    private String desc;

    @Enumerated(EnumType.STRING)
    @Column(name = "mi_category")
    private Category category;

    @Column(name = "mi_in_stock")
    private Boolean inStock;

    @Column(name = "mi_is_veg")
    private Boolean isVeg;

    @Column(name = "mi_price")
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
                .imageUrl(this.dishImage == null ? null : this.dishImage.getUrl())
                .build();
    }
}
