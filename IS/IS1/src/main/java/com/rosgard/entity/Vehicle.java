package com.rosgard.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "bigserial CHECK (id > 0)")
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически

    @NotBlank(message = "Имя не может быть пустым")
    @Column(columnDefinition = "varchar(255) NOT NULL CHECK (trim(name) <> '')")
    private String name; //Поле не может быть null, Строка не может быть пустой

    @NotNull (message = "Введите координаты") // иначе @Valid просто попытается проверить пустой обьект и скажет все ок, проверять нечего
    @Valid
    @ManyToOne(optional = false, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates; //Поле не может быть null

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false, updatable = false)
    private Date creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически

    @NotNull(message = "Тип обязателен")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;  //Поле не может быть null

    @Positive(message = "Мощность двигателя должна быть больше 0")
    @Column(columnDefinition = "integer NOT NULL CHECK (enginePower > 0)")
    private int enginePower; //Значение поля должно быть больше 0

    @Positive(message = "Число колёс должно быть больше 0")
    @Column(columnDefinition = "integer NOT NULL CHECK (numberOfWheels > 0)")
    private int numberOfWheels; //Значение поля должно быть больше 0

    @Positive(message = "Вместимость должна быть больше 0")
    @Column(columnDefinition = "bigint NOT NULL CHECK (capacity > 0)")
    private long capacity; //Значение поля должно быть больше 0

    @Positive(message = "Пробег должен быть больше 0")
    @Column(columnDefinition = "real CHECK (distanceTravelled > 0)")
    private Float distanceTravelled;  //Поле может быть null, Значение поля должно быть больше 0

    @Positive(message = "Расход топлива должен быть больше 0")
    @Column(columnDefinition = "integer NOT NULL CHECK (fuelConsumption > 0)")
    private int fuelConsumption; //Значение поля должно быть больше 0

    @NotNull(message = "Тип топлива обязателен")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType; //Поле не может быть null

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
    }
}
