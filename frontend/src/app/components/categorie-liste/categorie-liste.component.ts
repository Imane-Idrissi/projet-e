import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CategorieService } from '../../services/categorie.service';
import { Categorie, CreateCategorieRequest } from '../../models/categorie.model';

@Component({
  selector: 'app-categorie-liste',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './categorie-liste.component.html',
  styleUrl: './categorie-liste.component.scss'
})
export class CategorieListeComponent implements OnInit {

  categoriesRacines: Categorie[] = [];
  toutesCategories: Categorie[] = [];
  loading = true;
  showModal = false;
  editingCategorie: Categorie | null = null;
  toast: { message: string; type: string } | null = null;

  form: CreateCategorieRequest = {
    nom: '',
    description: '',
    parentId: null
  };

  constructor(private categorieService: CategorieService) {}

  ngOnInit(): void {
    this.chargerCategories();
  }

  chargerCategories(): void {
    this.loading = true;
    this.categorieService.listerCategoriesRacines().subscribe({
      next: (data) => {
        this.categoriesRacines = data;
        this.loading = false;
      }
    });
    this.categorieService.listerToutes().subscribe({
      next: (data) => this.toutesCategories = data
    });
  }

  ouvrirModal(categorie?: Categorie): void {
    if (categorie) {
      this.editingCategorie = categorie;
      this.form = {
        nom: categorie.nom,
        description: categorie.description || '',
        parentId: categorie.parentId
      };
    } else {
      this.editingCategorie = null;
      this.form = { nom: '', description: '', parentId: null };
    }
    this.showModal = true;
  }

  fermerModal(): void {
    this.showModal = false;
    this.editingCategorie = null;
  }

  sauvegarder(): void {
    if (!this.form.nom?.trim()) return;

    const operation = this.editingCategorie
      ? this.categorieService.modifierCategorie(this.editingCategorie.id, this.form)
      : this.categorieService.creerCategorie(this.form);

    operation.subscribe({
      next: () => {
        this.showToast(
          this.editingCategorie ? 'Catégorie modifiée' : 'Catégorie créée',
          'success'
        );
        this.fermerModal();
        this.chargerCategories();
      },
      error: (err) => {
        this.showToast(err.error?.message || 'Erreur', 'error');
      }
    });
  }

  supprimer(categorie: Categorie): void {
    if (!confirm(`Supprimer la catégorie "${categorie.nom}" ?`)) return;

    this.categorieService.supprimerCategorie(categorie.id).subscribe({
      next: () => {
        this.showToast('Catégorie supprimée', 'success');
        this.chargerCategories();
      },
      error: (err) => {
        this.showToast(err.error?.message || 'Impossible de supprimer cette catégorie', 'error');
      }
    });
  }

  showToast(message: string, type: string): void {
    this.toast = { message, type };
    setTimeout(() => this.toast = null, 3000);
  }
}
