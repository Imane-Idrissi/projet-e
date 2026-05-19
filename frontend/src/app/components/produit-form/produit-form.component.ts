import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { ProduitService } from '../../services/produit.service';
import { CategorieService } from '../../services/categorie.service';
import { CreateProduitRequest } from '../../models/produit.model';
import { Categorie } from '../../models/categorie.model';

@Component({
  selector: 'app-produit-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './produit-form.component.html',
  styleUrl: './produit-form.component.scss'
})
export class ProduitFormComponent implements OnInit {

  isEditing = false;
  produitId: number | null = null;
  categories: Categorie[] = [];
  loading = false;
  submitting = false;

  form: CreateProduitRequest = {
    designation: '',
    description: '',
    prixHT: 0,
    tauxTVA: 20,
    quantiteStock: 0,
    seuilAlerte: 10,
    categorieId: null
  };

  errors: { [key: string]: string } = {};

  constructor(
    private produitService: ProduitService,
    private categorieService: CategorieService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.chargerCategories();

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditing = true;
      this.produitId = +id;
      this.chargerProduit(this.produitId);
    }
  }

  chargerCategories(): void {
    this.categorieService.listerToutes().subscribe({
      next: (data) => this.categories = data
    });
  }

  chargerProduit(id: number): void {
    this.loading = true;
    this.produitService.obtenirProduit(id).subscribe({
      next: (produit) => {
        this.form = {
          designation: produit.designation,
          description: produit.description || '',
          prixHT: produit.prixHT,
          tauxTVA: produit.tauxTVA,
          quantiteStock: produit.quantiteStock,
          seuilAlerte: produit.seuilAlerte,
          categorieId: produit.categorieId
        };
        this.loading = false;
      },
      error: () => {
        this.router.navigate(['/produits']);
      }
    });
  }

  valider(): boolean {
    this.errors = {};

    if (!this.form.designation?.trim()) {
      this.errors['designation'] = 'La désignation est obligatoire';
    }
    if (this.form.prixHT == null || this.form.prixHT <= 0) {
      this.errors['prixHT'] = 'Le prix HT doit être supérieur à 0';
    }
    if (this.form.tauxTVA == null || this.form.tauxTVA < 0) {
      this.errors['tauxTVA'] = 'Le taux de TVA ne peut pas être négatif';
    }
    if (this.form.quantiteStock == null || this.form.quantiteStock < 0) {
      this.errors['quantiteStock'] = 'La quantité ne peut pas être négative';
    }

    return Object.keys(this.errors).length === 0;
  }

  get prixTTCCalcule(): number {
    if (!this.form.prixHT || !this.form.tauxTVA) return 0;
    return this.form.prixHT + (this.form.prixHT * this.form.tauxTVA / 100);
  }

  soumettre(): void {
    if (!this.valider()) return;

    this.submitting = true;

    const request = { ...this.form };
    if (!request.categorieId) {
      request.categorieId = null;
    }

    const operation = this.isEditing
      ? this.produitService.modifierProduit(this.produitId!, request)
      : this.produitService.creerProduit(request);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/produits']);
      },
      error: (err) => {
        this.submitting = false;
        if (err.error?.details) {
          this.errors = err.error.details;
        }
      }
    });
  }
}
