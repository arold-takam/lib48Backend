package com.k48.lib48.models;

import jakarta.persistence.*;


@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column( nullable = false , unique = true)
    private String nom;

    @Column(name = "description")
    private String description;
    
    public Category(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }
    
    public Category() {}
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
