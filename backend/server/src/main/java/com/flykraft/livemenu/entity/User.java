package com.flykraft.livemenu.entity;

import com.flykraft.livemenu.dto.customer.UserResDto;
import com.flykraft.livemenu.model.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
    @Column(name = "u_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "au_id", nullable = false, unique = true)
    private AuthUser authUser;

    @Column(name = "u_name")
    private String name;

    @Column(name = "u_phone")
    private String phone;

    @Column(name = "u_default_profile_id")
    private Long defaultProfileId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CustomerProfile> customerProfiles;

    public UserResDto toResponseDto() {
        return UserResDto.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }
}
