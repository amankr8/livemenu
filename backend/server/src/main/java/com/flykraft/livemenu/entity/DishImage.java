package com.flykraft.livemenu.entity;

import com.flykraft.livemenu.model.CloudinaryFile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "dish_image")
public class DishImage extends CloudinaryFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "di_id")
    private Long id;
}
