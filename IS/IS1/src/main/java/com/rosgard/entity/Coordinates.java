package com.rosgard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@Entity
public class Coordinates {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull (message = "не может быть null")
    @DecimalMin(value = "-414", message = "x должен быть больше -415")
    @Column(columnDefinition = "double precision NOT NULL CHECK (x > -415)")
    private Double x; //Значение поля должно быть больше -415, Поле не может быть null

    @NotNull (message = "не может быть null")
    @DecimalMax(value = "663", message = "y должен быть меньше 663")
    @Column(columnDefinition = "real NOT NULL CHECK (y <= 663) ") // float java = real sql
    private Float y; //Максимальное значение поля: 663, Поле не может быть null

    public Coordinates() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }
}
