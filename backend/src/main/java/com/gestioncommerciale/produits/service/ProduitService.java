package com.gestioncommerciale.produits.service;

import com.gestioncommerciale.produits.dto.CreateProduitRequest;
import com.gestioncommerciale.produits.dto.ProduitDTO;
import com.gestioncommerciale.produits.entity.Categorie;
import com.gestioncommerciale.produits.entity.Produit;
import com.gestioncommerciale.produits.repository.CategorieRepository;
import com.gestioncommerciale.produits.repository.ProduitRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProduitService {

    private final ProduitRepository produitRepository;
    private final CategorieRepository categorieRepository;

    // Lister tous les produits actifs
    @Transactional(readOnly = true)
    public List<ProduitDTO> listerProduitsActifs() {
        return produitRepository.findByActifTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Obtenir un produit par ID
    @Transactional(readOnly = true)
    public ProduitDTO obtenirProduit(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'id: " + id));
        return toDTO(produit);
    }

    // Créer un nouveau produit (US-PRD-01)
    public ProduitDTO creerProduit(CreateProduitRequest request) {
        Produit produit = Produit.builder()
                .reference(genererReference())
                .designation(request.getDesignation())
                .description(request.getDescription())
                .prixHT(request.getPrixHT())
                .tauxTVA(request.getTauxTVA())
                .quantiteStock(request.getQuantiteStock())
                .seuilAlerte(request.getSeuilAlerte() != null ? request.getSeuilAlerte() : 10)
                .actif(true)
                .build();

        // Associer la catégorie si spécifiée
        if (request.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(request.getCategorieId())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée avec l'id: " + request.getCategorieId()));
            produit.setCategorie(categorie);
        }

        Produit saved = produitRepository.save(produit);
        return toDTO(saved);
    }

    // Modifier un produit (US-PRD-02)
    public ProduitDTO modifierProduit(Long id, CreateProduitRequest request) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'id: " + id));

        produit.setDesignation(request.getDesignation());
        produit.setDescription(request.getDescription());
        produit.setPrixHT(request.getPrixHT());
        produit.setTauxTVA(request.getTauxTVA());
        produit.setQuantiteStock(request.getQuantiteStock());

        if (request.getSeuilAlerte() != null) {
            produit.setSeuilAlerte(request.getSeuilAlerte());
        }

        if (request.getCategorieId() != null) {
            Categorie categorie = categorieRepository.findById(request.getCategorieId())
                    .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée"));
            produit.setCategorie(categorie);
        } else {
            produit.setCategorie(null);
        }

        Produit updated = produitRepository.save(produit);
        return toDTO(updated);
    }

    // Archiver un produit au lieu de le supprimer (RG-PRD-04)
    public void archiverProduit(Long id) {
        Produit produit = produitRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Produit non trouvé avec l'id: " + id));
        produit.setActif(false);
        produitRepository.save(produit);
    }

    // Recherche multi-critères (US-PRD-04)
    @Transactional(readOnly = true)
    public List<ProduitDTO> rechercher(String designation, Long categorieId, BigDecimal prixMin, BigDecimal prixMax) {
        return produitRepository.rechercher(designation, categorieId, prixMin, prixMax)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Lister les produits en stock bas (US-PRD-05 / RG-PRD-05)
    @Transactional(readOnly = true)
    public List<ProduitDTO> listerProduitsStockBas() {
        return produitRepository.findProduitsStockBas()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Produits par catégorie
    @Transactional(readOnly = true)
    public List<ProduitDTO> listerParCategorie(Long categorieId) {
        return produitRepository.findByCategorieIdAndActifTrue(categorieId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Génération automatique de référence PRD-XXXXX (RG-PRD-01)
    private String genererReference() {
        return produitRepository.findDerniereReference()
                .map(ref -> {
                    int numero = Integer.parseInt(ref.substring(4)) + 1;
                    return String.format("PRD-%05d", numero);
                })
                .orElse("PRD-00001");
    }

    // Conversion entité vers DTO
    private ProduitDTO toDTO(Produit produit) {
        return ProduitDTO.builder()
                .id(produit.getId())
                .reference(produit.getReference())
                .designation(produit.getDesignation())
                .description(produit.getDescription())
                .prixHT(produit.getPrixHT())
                .tauxTVA(produit.getTauxTVA())
                .prixTTC(produit.getPrixTTC())
                .quantiteStock(produit.getQuantiteStock())
                .seuilAlerte(produit.getSeuilAlerte())
                .categorieId(produit.getCategorie() != null ? produit.getCategorie().getId() : null)
                .categorieNom(produit.getCategorie() != null ? produit.getCategorie().getNom() : null)
                .actif(produit.getActif())
                .stockBas(produit.isStockBas())
                .dateCreation(produit.getDateCreation())
                .dateModification(produit.getDateModification())
                .build();
    }
}
