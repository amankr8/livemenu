package com.flykraft.menu_service.model;

import com.flykraft.menu_service.dto.KitchenResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "kitchens")
public class Kitchen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kitchen_id")
    private Long id;

    @Column(name = "kitchen_name")
    private String name;

    @Column(name = "kitchen_address")
    private String address;

    @Column(name = "kitchen_subdomain")
    private String subdomain;

    @Column(name = "kitchen_whatsapp")
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
