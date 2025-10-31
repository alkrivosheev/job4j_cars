package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 17)
    private String vin;

    @Column(nullable = false)
    private Long mileage;

    @Column(name = "year_of_manufacture", nullable = false)
    private Long yearOfManufacture;

    @Column(name = "count_owners", nullable = false)
    private Long countOwners = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CAR_MODEL_ID"))
    private CarModel model;

    /**
     * Связь с брендом автомобиля.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CAR_BRAND_ID"))
    private Brand brand;

    /**
     * Связь с категорией автомобиля (например, седан, хэтчбек).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CAR_CATEGORY_ID"))
    private Category category;

    /**
     * Связь с типом кузова автомобиля.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "body_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CAR_BODY_ID"))
    private Body body;

    /**
     * Связь с двигателем автомобиля.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engine_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CAR_ENGINE_ID"))
    private Engine engine;

    /**
     * Связь с типом трансмиссии автомобиля.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transmission_type_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CAR_TRANSMISSION_ID"))
    private TransmissionType transmissionType;

    /**
     * Связь с типом привода автомобиля.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_type_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CAR_DRIVE_ID"))
    private DriveType driveType;

    /**
     * Связь с типом окраса автомобиля.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_color_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CAR_COLOR_ID"))
    private CarColor carColor;

    /**
     * Связь с типом топлива автомобиля.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fuel_type_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CAR_FUEL_ID"))
    private FuelType fuelType;

    /**
     * Связь с типом руля автомобиля (левый/правый).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wheel_side_id", nullable = false, foreignKey = @ForeignKey(name = "FK_CAR_WHEEL_SIDE_ID"))
    private WheelSide wheelSide;
}