package com.flykraft.livemenu.entity;

import com.flykraft.livemenu.dto.kitchen.KitchenResDto;
import com.flykraft.livemenu.model.Auditable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@FilterDef(name = "kitchenFilter", parameters = @ParamDef(name = "kitchenId", type = Long.class))
@Filter(name = "kitchenFilter", condition = "k_id = :kitchenId")
@Entity
@Table(name = "kitchens")
public class Kitchen extends Auditable implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "k_id")
    private Long id;

    @Column(name = "k_name", nullable = false)
    private String name;

    @Column(name = "k_tagline")
    private String tagline;

    @Column(name = "k_address")
    private String address;

    @Column(name = "k_subdomain", unique = true, nullable = false)
    private String subdomain;

    @Column(name = "k_whatsapp", nullable = false)
    private String whatsapp;

    @Column(name = "k_location")
    private String location;

    @Column(name = "k_delivery_radius")
    private Integer deliveryRadius;

    @OneToMany(mappedBy = "kitchen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MenuItem> menuItems;

    @OneToMany(mappedBy = "kitchen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<KitchenOwner> kitchenOwners;

    @OneToMany(mappedBy = "kitchen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    public KitchenResDto toResponseDto() {
        return KitchenResDto.builder()
                .id(this.id)
                .name(this.name)
                .tagline(this.tagline)
                .address(this.address)
                .whatsapp(this.whatsapp)
                .location(this.location)
                .deliveryRadius(this.deliveryRadius)
                .build();
    }
}
