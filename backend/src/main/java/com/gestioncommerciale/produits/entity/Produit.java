package com.gestioncommerciale.produits.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence unique du produit (RG-PRD-01 : format PRD-XXXXX)
    @Column(nullable = false, unique = true, length = 20)
    private String reference;

    @Column(nullable = false, length = 200)
    private String designation;

    @Column(length = 1000)
    private String description;

    // Prix HT en DH (RG-PRD-02 : doit être positif)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixHT;

    // Taux de TVA applicable (ex: 20.00 pour 20%)
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tauxTVA;

    // Quantité en stock (RG-PRD-05 : alerte si < seuil)
    @Column(nullable = false)
    private Integer quantiteStock;

    // Seuil d'alerte stock bas
    @Column(nullable = false)
    @Builder.Default
    private Integer seuilAlerte = 10;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    // RG-PRD-04 : archivage au lieu de suppression
    @Column(nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    private LocalDateTime dateModification;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = LocalDateTime.now();
    }

    // Calcul du prix TTC
    public BigDecimal getPrixTTC() {
        if (prixHT == null || tauxTVA == null) return BigDecimal.ZERO;
        return prixHT.add(prixHT.multiply(tauxTVA).divide(BigDecimal.valueOf(100)));
    }

    // Vérifier si le stock est bas (RG-PRD-05)
    public boolean isStockBas() {
        return quantiteStock != null && quantiteStock <= seuilAlerte;
    }
}
