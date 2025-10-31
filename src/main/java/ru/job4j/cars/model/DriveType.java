package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "drive_types")
public class DriveType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;
}