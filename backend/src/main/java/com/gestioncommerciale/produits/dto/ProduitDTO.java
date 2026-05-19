package com.gestioncommerciale.produits.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProduitDTO {

    private Long id;
    private String reference;
    private String designation;
    private String description;
    private BigDecimal prixHT;
    private BigDecimal tauxTVA;
    private BigDecimal prixTTC;
    private Integer quantiteStock;
    private Integer seuilAlerte;
    private Long categorieId;
    private String categorieNom;
    private Boolean actif;
    private Boolean stockBas;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}
