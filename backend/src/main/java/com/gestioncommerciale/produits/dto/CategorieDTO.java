package com.gestioncommerciale.produits.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorieDTO {

    private Long id;
    private String nom;
    private String description;
    private Long parentId;
    private String parentNom;
    private List<CategorieDTO> sousCategories;
    private int nombreProduits;
    private LocalDateTime dateCreation;
}
