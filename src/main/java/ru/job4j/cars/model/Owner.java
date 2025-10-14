package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String name;


}