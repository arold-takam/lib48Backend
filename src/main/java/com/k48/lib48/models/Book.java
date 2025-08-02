package com.k48.lib48.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.k48.lib48.myEnum.EtatLivre;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;
    
    @Column(name = "auteur")
    private String auteur;

    @Column(name = "est_disponible", nullable = false)
    private boolean estDisponible;

    @Column(name = "editeur")
    private String editeur;

    @Column(name = "etat_livre", nullable = false)
    @Enumerated(EnumType.STRING)
    private EtatLivre etatLivre;


    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    public Book() {}
    
    public Book(String titre, String auteur, boolean estDisponible, String editeur, EtatLivre etatLivre, Category category) {
        this.titre = titre;
        this.auteur = auteur;
        this.estDisponible = estDisponible;
        this.editeur = editeur;
        this.etatLivre = etatLivre;
        this.category = category;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }
    
    public boolean isEstDisponible() {
        return estDisponible;
    }
    
    public void setEstDisponible(boolean estDisponible) {
        this.estDisponible = estDisponible;
    }
    
    public String getEditeur() {
        return editeur;
    }

    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    public EtatLivre getEtatLivre() {
        return etatLivre;
    }

    public void setEtatLivre(EtatLivre etatLivre) {
        this.etatLivre = etatLivre;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
