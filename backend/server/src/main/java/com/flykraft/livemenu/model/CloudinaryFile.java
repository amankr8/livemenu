package com.flykraft.livemenu.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@MappedSuperclass
public class CloudinaryFile {

    @Column(name = "cn_public_id")
    private String publicId;

    @Column(name = "cn_secure_url")
    private String secureUrl;
}
