package com.gestioncommerciale.produits.repository;

import com.gestioncommerciale.produits.entity.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategorieRepository extends JpaRepository<Categorie, Long> {

    Optional<Categorie> findByNom(String nom);

    // Catégories racines (sans parent)
    List<Categorie> findByParentIsNull();

    // Sous-catégories d'une catégorie
    List<Categorie> findByParentId(Long parentId);

    boolean existsByNom(String nom);
}
