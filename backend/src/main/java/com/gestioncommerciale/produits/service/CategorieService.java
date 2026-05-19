package com.gestioncommerciale.produits.service;

import com.gestioncommerciale.produits.dto.CategorieDTO;
import com.gestioncommerciale.produits.dto.CreateCategorieRequest;
import com.gestioncommerciale.produits.entity.Categorie;
import com.gestioncommerciale.produits.repository.CategorieRepository;
import com.gestioncommerciale.produits.repository.ProduitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;

    // Lister toutes les catégories racines avec sous-catégories
    @Transactional(readOnly = true)
    public List<CategorieDTO> listerCategoriesRacines() {
        return categorieRepository.findByParentIsNull()
                .stream()
                .map(this::toDTOAvecEnfants)
                .collect(Collectors.toList());
    }

    // Lister toutes les catégories (liste plate)
    @Transactional(readOnly = true)
    public List<CategorieDTO> listerToutes() {
        return categorieRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Obtenir une catégorie par ID
    @Transactional(readOnly = true)
    public CategorieDTO obtenirCategorie(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée avec l'id: " + id));
        return toDTOAvecEnfants(categorie);
    }

    // Créer une catégorie (US-PRD-03)
    public CategorieDTO creerCategorie(CreateCategorieRequest request) {
        if (categorieRepository.existsByNom(request.getNom())) {
            throw new IllegalArgumentException("Une catégorie avec ce nom existe déjà: " + request.getNom());
        }

        Categorie categorie = Categorie.builder()
                .nom(request.getNom())
                .description(request.getDescription())
                .build();

        // Associer le parent si spécifié (RG-PRD-03 : hiérarchie)
        if (request.getParentId() != null) {
            Categorie parent = categorieRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie parente non trouvée"));
            categorie.setParent(parent);
        }

        Categorie saved = categorieRepository.save(categorie);
        return toDTO(saved);
    }

    // Modifier une catégorie
    public CategorieDTO modifierCategorie(Long id, CreateCategorieRequest request) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée avec l'id: " + id));

        categorie.setNom(request.getNom());
        categorie.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new IllegalArgumentException("Une catégorie ne peut pas être son propre parent");
            }
            Categorie parent = categorieRepository.findById(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie parente non trouvée"));
            categorie.setParent(parent);
        } else {
            categorie.setParent(null);
        }

        Categorie updated = categorieRepository.save(categorie);
        return toDTO(updated);
    }

    // Supprimer une catégorie
    public void supprimerCategorie(Long id) {
        Categorie categorie = categorieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée avec l'id: " + id));

        long nbProduits = produitRepository.countByCategorieIdAndActifTrue(id);
        if (nbProduits > 0) {
            throw new IllegalStateException("Impossible de supprimer la catégorie: " + nbProduits + " produit(s) actif(s) y sont associés");
        }

        categorieRepository.delete(categorie);
    }

    private CategorieDTO toDTO(Categorie categorie) {
        return CategorieDTO.builder()
                .id(categorie.getId())
                .nom(categorie.getNom())
                .description(categorie.getDescription())
                .parentId(categorie.getParent() != null ? categorie.getParent().getId() : null)
                .parentNom(categorie.getParent() != null ? categorie.getParent().getNom() : null)
                .nombreProduits((int) produitRepository.countByCategorieIdAndActifTrue(categorie.getId()))
                .dateCreation(categorie.getDateCreation())
                .build();
    }

    private CategorieDTO toDTOAvecEnfants(Categorie categorie) {
        CategorieDTO dto = toDTO(categorie);
        dto.setSousCategories(
                categorie.getSousCategories().stream()
                        .map(this::toDTOAvecEnfants)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
