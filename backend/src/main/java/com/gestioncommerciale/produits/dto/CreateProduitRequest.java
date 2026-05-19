package com.gestioncommerciale.produits.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProduitRequest {

    @NotBlank(message = "La désignation est obligatoire")
    @Size(max = 200, message = "La désignation ne doit pas dépasser 200 caractères")
    private String designation;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères")
    private String description;

    @NotNull(message = "Le prix HT est obligatoire")
    @DecimalMin(value = "0.01", message = "Le prix HT doit être supérieur à 0 (RG-PRD-02)")
    private BigDecimal prixHT;

    @NotNull(message = "Le taux de TVA est obligatoire")
    @DecimalMin(value = "0.00", message = "Le taux de TVA ne peut pas être négatif")
    private BigDecimal tauxTVA;

    @NotNull(message = "La quantité en stock est obligatoire")
    @Min(value = 0, message = "La quantité en stock ne peut pas être négative")
    private Integer quantiteStock;

    @Min(value = 1, message = "Le seuil d'alerte doit être au minimum 1")
    private Integer seuilAlerte = 10;

    private Long categorieId;
}
