package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "post_photos")
public class PostPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "photo_path", nullable = false, length = 255)
    private String photoPath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, foreignKey = @ForeignKey(name = "FK_PHOTO_POST_ID"))
    private Post post;
}