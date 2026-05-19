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

  filtreTexte = '';
  filtreCategorie: number | null = null;

  private icons: string[] = ['💻', '🖥️', '🪑', '📦', '🖨️', '⌨️', '🖱️', '📄'];

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

  get produitsFiltres(): Produit[] {
    return this.produits.filter(p => {
      const matchTexte = !this.filtreTexte ||
        p.designation.toLowerCase().includes(this.filtreTexte.toLowerCase()) ||
        p.reference.toLowerCase().includes(this.filtreTexte.toLowerCase());
      const matchCat = this.filtreCategorie === null || p.categorieId === this.filtreCategorie;
      return matchTexte && matchCat;
    });
  }

  setFiltreCategorie(id: number | null): void {
    this.filtreCategorie = id;
  }

  getIcon(produit: Produit): string {
    const idx = produit.id ? produit.id % this.icons.length : 0;
    return this.icons[idx];
  }

  archiverProduit(event: Event, produit: Produit): void {
    event.stopPropagation();
    if (!confirm(`Archiver le produit "${produit.designation}" ?`)) return;

    this.produitService.archiverProduit(produit.id).subscribe({
      next: () => {
        this.showToast(`"${produit.designation}" archivé`, 'success');
        this.chargerProduits();
      },
      error: () => this.showToast('Erreur lors de l\'archivage', 'error')
    });
  }

  showToast(message: string, type: string): void {
    this.toast = { message, type };
    setTimeout(() => this.toast = null, 3000);
  }
}
