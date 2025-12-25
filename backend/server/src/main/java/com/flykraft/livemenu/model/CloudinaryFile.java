package com.flykraft.livemenu.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@MappedSuperclass
public class CloudinaryFile {

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "url")
    private String url;
}
