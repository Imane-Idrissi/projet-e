package com.gestioncommerciale.produits.controller;

import com.gestioncommerciale.produits.dto.CategorieDTO;
import com.gestioncommerciale.produits.dto.CreateCategorieRequest;
import com.gestioncommerciale.produits.service.CategorieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    // GET /api/categories — Catégories racines avec hiérarchie
    @GetMapping
    public ResponseEntity<List<CategorieDTO>> listerCategories() {
        return ResponseEntity.ok(categorieService.listerCategoriesRacines());
    }

    // GET /api/categories/toutes — Liste plate de toutes les catégories
    @GetMapping("/toutes")
    public ResponseEntity<List<CategorieDTO>> listerToutes() {
        return ResponseEntity.ok(categorieService.listerToutes());
    }

    // GET /api/categories/{id} — Obtenir une catégorie
    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> obtenirCategorie(@PathVariable Long id) {
        return ResponseEntity.ok(categorieService.obtenirCategorie(id));
    }

    // POST /api/categories — Créer une catégorie
    @PostMapping
    public ResponseEntity<CategorieDTO> creerCategorie(@Valid @RequestBody CreateCategorieRequest request) {
        CategorieDTO categorie = categorieService.creerCategorie(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(categorie);
    }

    // PUT /api/categories/{id} — Modifier une catégorie
    @PutMapping("/{id}")
    public ResponseEntity<CategorieDTO> modifierCategorie(@PathVariable Long id,
                                                           @Valid @RequestBody CreateCategorieRequest request) {
        return ResponseEntity.ok(categorieService.modifierCategorie(id, request));
    }

    // DELETE /api/categories/{id} — Supprimer une catégorie
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCategorie(@PathVariable Long id) {
        categorieService.supprimerCategorie(id);
        return ResponseEntity.noContent().build();
    }
}
