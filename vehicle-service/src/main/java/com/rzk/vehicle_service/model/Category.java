package com.rzk.vehicle_service.model;

import jakarta.persistence.*;
import  lombok.Getter;
import  lombok.Setter;

@Getter
@Setter
@Entity

public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false)
    private String name;
}
