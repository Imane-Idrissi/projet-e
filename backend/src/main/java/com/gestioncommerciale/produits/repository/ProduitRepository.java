package com.gestioncommerciale.produits.repository;

import com.gestioncommerciale.produits.entity.Produit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduitRepository extends JpaRepository<Produit, Long> {

    Optional<Produit> findByReference(String reference);

    List<Produit> findByActifTrue();

    List<Produit> findByCategorieIdAndActifTrue(Long categorieId);

    // Recherche par désignation (US-PRD-04)
    List<Produit> findByDesignationContainingIgnoreCaseAndActifTrue(String designation);

    // Produits avec stock bas (RG-PRD-05)
    @Query("SELECT p FROM Produit p WHERE p.quantiteStock <= p.seuilAlerte AND p.actif = true")
    List<Produit> findProduitsStockBas();

    // Compter les produits actifs par catégorie
    long countByCategorieIdAndActifTrue(Long categorieId);

    // Dernière référence pour génération auto (RG-PRD-01)
    @Query("SELECT p.reference FROM Produit p ORDER BY p.id DESC")
    List<String> findDerniereReference(Pageable pageable);

    // Recherche multi-critères
    @Query("SELECT p FROM Produit p WHERE p.actif = true " +
           "AND (:designation IS NULL OR LOWER(p.designation) LIKE LOWER(CONCAT('%', :designation, '%'))) " +
           "AND (:categorieId IS NULL OR p.categorie.id = :categorieId) " +
           "AND (:prixMin IS NULL OR p.prixHT >= :prixMin) " +
           "AND (:prixMax IS NULL OR p.prixHT <= :prixMax)")
    List<Produit> rechercher(
            @Param("designation") String designation,
            @Param("categorieId") Long categorieId,
            @Param("prixMin") java.math.BigDecimal prixMin,
            @Param("prixMax") java.math.BigDecimal prixMax
    );
}
