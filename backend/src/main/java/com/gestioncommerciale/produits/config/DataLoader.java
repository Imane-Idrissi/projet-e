package com.gestioncommerciale.produits.config;

import com.gestioncommerciale.produits.entity.Categorie;
import com.gestioncommerciale.produits.entity.Produit;
import com.gestioncommerciale.produits.repository.CategorieRepository;
import com.gestioncommerciale.produits.repository.ProduitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategorieRepository categorieRepository;
    private final ProduitRepository produitRepository;

    @Override
    public void run(String... args) {
        if (categorieRepository.count() > 0) return;

        // Catégories
        Categorie informatique = categorieRepository.save(
                Categorie.builder().nom("Informatique").description("Matériel et logiciels informatiques").build()
        );
        Categorie ordinateurs = categorieRepository.save(
                Categorie.builder().nom("Ordinateurs").description("PC portables et de bureau").parent(informatique).build()
        );
        Categorie peripheriques = categorieRepository.save(
                Categorie.builder().nom("Périphériques").description("Claviers, souris, écrans").parent(informatique).build()
        );
        Categorie bureautique = categorieRepository.save(
                Categorie.builder().nom("Bureautique").description("Fournitures de bureau").build()
        );
        Categorie mobilier = categorieRepository.save(
                Categorie.builder().nom("Mobilier").description("Mobilier de bureau").build()
        );

        // Produits
        produitRepository.save(Produit.builder()
                .reference("PRD-00001").designation("Laptop HP ProBook 450")
                .description("Ordinateur portable professionnel 15.6 pouces, i5, 8Go RAM, 256Go SSD")
                .prixHT(new BigDecimal("8500.00")).tauxTVA(new BigDecimal("20.00"))
                .quantiteStock(25).seuilAlerte(5).categorie(ordinateurs).actif(true).build());

        produitRepository.save(Produit.builder()
                .reference("PRD-00002").designation("Ecran Dell 24 pouces")
                .description("Moniteur Full HD IPS, connectique HDMI/DP")
                .prixHT(new BigDecimal("2200.00")).tauxTVA(new BigDecimal("20.00"))
                .quantiteStock(15).seuilAlerte(5).categorie(peripheriques).actif(true).build());

        produitRepository.save(Produit.builder()
                .reference("PRD-00003").designation("Clavier Logitech MK270")
                .description("Clavier sans fil avec souris")
                .prixHT(new BigDecimal("350.00")).tauxTVA(new BigDecimal("20.00"))
                .quantiteStock(3).seuilAlerte(10).categorie(peripheriques).actif(true).build());

        produitRepository.save(Produit.builder()
                .reference("PRD-00004").designation("Ramette papier A4 80g")
                .description("Paquet de 500 feuilles papier blanc A4")
                .prixHT(new BigDecimal("45.00")).tauxTVA(new BigDecimal("20.00"))
                .quantiteStock(200).seuilAlerte(50).categorie(bureautique).actif(true).build());

        produitRepository.save(Produit.builder()
                .reference("PRD-00005").designation("Bureau ergonomique ajustable")
                .description("Bureau assis-debout électrique, plateau 160x80cm")
                .prixHT(new BigDecimal("4500.00")).tauxTVA(new BigDecimal("20.00"))
                .quantiteStock(2).seuilAlerte(3).categorie(mobilier).actif(true).build());

        produitRepository.save(Produit.builder()
                .reference("PRD-00006").designation("Imprimante HP LaserJet Pro")
                .description("Imprimante laser monochrome réseau, recto-verso auto")
                .prixHT(new BigDecimal("3200.00")).tauxTVA(new BigDecimal("20.00"))
                .quantiteStock(8).seuilAlerte(3).categorie(informatique).actif(true).build());
    }
}
