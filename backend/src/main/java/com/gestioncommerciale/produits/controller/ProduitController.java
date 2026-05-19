package com.gestioncommerciale.produits.controller;

import com.gestioncommerciale.produits.dto.CreateProduitRequest;
import com.gestioncommerciale.produits.dto.ProduitDTO;
import com.gestioncommerciale.produits.service.ProduitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/produits")
@RequiredArgsConstructor
public class ProduitController {

    private final ProduitService produitService;

    // GET /api/produits — Lister les produits actifs
    @GetMapping
    public ResponseEntity<List<ProduitDTO>> listerProduits() {
        return ResponseEntity.ok(produitService.listerProduitsActifs());
    }

    // GET /api/produits/{id} — Obtenir un produit
    @GetMapping("/{id}")
    public ResponseEntity<ProduitDTO> obtenirProduit(@PathVariable Long id) {
        return ResponseEntity.ok(produitService.obtenirProduit(id));
    }

    // POST /api/produits — Créer un produit
    @PostMapping
    public ResponseEntity<ProduitDTO> creerProduit(@Valid @RequestBody CreateProduitRequest request) {
        ProduitDTO produit = produitService.creerProduit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(produit);
    }

    // PUT /api/produits/{id} — Modifier un produit
    @PutMapping("/{id}")
    public ResponseEntity<ProduitDTO> modifierProduit(@PathVariable Long id,
                                                       @Valid @RequestBody CreateProduitRequest request) {
        return ResponseEntity.ok(produitService.modifierProduit(id, request));
    }

    // DELETE /api/produits/{id} — Archiver un produit (RG-PRD-04)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archiverProduit(@PathVariable Long id) {
        produitService.archiverProduit(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/produits/recherche — Recherche multi-critères
    @GetMapping("/recherche")
    public ResponseEntity<List<ProduitDTO>> rechercher(
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) Long categorieId,
            @RequestParam(required = false) BigDecimal prixMin,
            @RequestParam(required = false) BigDecimal prixMax) {
        return ResponseEntity.ok(produitService.rechercher(designation, categorieId, prixMin, prixMax));
    }

    // GET /api/produits/stock-bas — Produits en alerte stock
    @GetMapping("/stock-bas")
    public ResponseEntity<List<ProduitDTO>> produitsStockBas() {
        return ResponseEntity.ok(produitService.listerProduitsStockBas());
    }

    // GET /api/produits/categorie/{id} — Produits par catégorie
    @GetMapping("/categorie/{categorieId}")
    public ResponseEntity<List<ProduitDTO>> produitsParCategorie(@PathVariable Long categorieId) {
        return ResponseEntity.ok(produitService.listerParCategorie(categorieId));
    }
}
