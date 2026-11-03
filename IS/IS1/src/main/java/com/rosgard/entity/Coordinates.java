package com.rosgard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Coordinates {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull (message = "не может быть null")
    @DecimalMin(value = "-415", inclusive = false, message = "x должен быть больше -415")
    @Column(columnDefinition = "double precision NOT NULL CHECK (x > -415)")
    private Double x; //Значение поля должно быть больше -415, Поле не может быть null

    @NotNull (message = "не может быть null")
    @DecimalMax(value = "663", message = "y должен быть меньше 663")
    @Column(columnDefinition = "real NOT NULL CHECK (y <= 663) ") // float java = real sql
    private Float y; //Максимальное значение поля: 663, Поле не может быть null
}
