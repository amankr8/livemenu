package com.flykraft.livemenu.entity;

import com.flykraft.livemenu.dto.customer.UserResDto;
import com.flykraft.livemenu.model.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "users")
public class User extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "au_id", nullable = false, unique = true)
    private AuthUser authUser;

    @Column(name = "c_name")
    private String name;

    @Column(name = "c_phone, unique = true, nullable = false")
    private String phone;

    @Column(name = "c_address")
    private String address;

    public UserResDto toResponseDto() {
        return UserResDto.builder()
                .id(this.id)
                .name(this.name)
                .phone(this.phone)
                .address(this.address)
                .build();
    }
}
