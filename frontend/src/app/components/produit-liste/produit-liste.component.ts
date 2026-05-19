import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProduitService } from '../../services/produit.service';
import { CategorieService } from '../../services/categorie.service';
import { Produit } from '../../models/produit.model';
import { Categorie } from '../../models/categorie.model';

@Component({
  selector: 'app-produit-liste',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './produit-liste.component.html',
  styleUrl: './produit-liste.component.scss'
})
export class ProduitListeComponent implements OnInit {

  produits: Produit[] = [];
  categories: Categorie[] = [];
  loading = true;
  toast: { message: string; type: string } | null = null;

  // Filtres de recherche
  filtreDesignation = '';
  filtreCategorie: number | null = null;
  filtrePrixMin: number | null = null;
  filtrePrixMax: number | null = null;

  constructor(
    private produitService: ProduitService,
    private categorieService: CategorieService
  ) {}

  ngOnInit(): void {
    this.chargerProduits();
    this.chargerCategories();
  }

  chargerProduits(): void {
    this.loading = true;
    this.produitService.listerProduits().subscribe({
      next: (data) => {
        this.produits = data;
        this.loading = false;
      },
      error: () => {
        this.showToast('Erreur lors du chargement des produits', 'error');
        this.loading = false;
      }
    });
  }

  chargerCategories(): void {
    this.categorieService.listerToutes().subscribe({
      next: (data) => this.categories = data
    });
  }

  rechercher(): void {
    this.loading = true;
    this.produitService.rechercher(
      this.filtreDesignation || undefined,
      this.filtreCategorie || undefined,
      this.filtrePrixMin ?? undefined,
      this.filtrePrixMax ?? undefined
    ).subscribe({
      next: (data) => {
        this.produits = data;
        this.loading = false;
      },
      error: () => {
        this.showToast('Erreur lors de la recherche', 'error');
        this.loading = false;
      }
    });
  }

  reinitialiserFiltres(): void {
    this.filtreDesignation = '';
    this.filtreCategorie = null;
    this.filtrePrixMin = null;
    this.filtrePrixMax = null;
    this.chargerProduits();
  }

  archiverProduit(produit: Produit): void {
    if (!confirm(`Archiver le produit "${produit.designation}" ?`)) return;

    this.produitService.archiverProduit(produit.id).subscribe({
      next: () => {
        this.showToast(`Produit "${produit.designation}" archivé`, 'success');
        this.chargerProduits();
      },
      error: () => {
        this.showToast('Erreur lors de l\'archivage', 'error');
      }
    });
  }

  get totalProduits(): number {
    return this.produits.length;
  }

  get produitsStockBas(): number {
    return this.produits.filter(p => p.stockBas).length;
  }

  get valeurTotaleStock(): number {
    return this.produits.reduce((total, p) => total + (p.prixHT * p.quantiteStock), 0);
  }

  showToast(message: string, type: string): void {
    this.toast = { message, type };
    setTimeout(() => this.toast = null, 3000);
  }
}
