package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Модель данных для таблицы wheel_sides.
 * Справочник типов расположения руля.
 */
@Data
@Entity
@Table(name = "wheel_sides")
public class WheelSide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;
}