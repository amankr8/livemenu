package com.flykraft.livemenu.entity;

import com.flykraft.livemenu.dto.kitchen.KitchenResponseDto;
import com.flykraft.livemenu.model.Auditable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "kitchen")
public class Kitchen extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "k_id")
    private Long id;

    @Column(name = "k_name")
    private String name;

    @Column(name = "k_tagline")
    private String tagline;

    @Column(name = "k_address")
    private String address;

    @Column(name = "k_subdomain")
    private String subdomain;

    @Column(name = "k_whatsapp")
    private String whatsapp;

    public KitchenResponseDto toResponseDto() {
        return KitchenResponseDto.builder()
                .id(this.id)
                .name(this.name)
                .address(this.address)
                .subdomain(this.subdomain)
                .whatsapp(this.whatsapp)
                .build();
    }
}
