import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/produits', pathMatch: 'full' },
  {
    path: 'produits',
    loadComponent: () => import('./components/produit-liste/produit-liste.component').then(m => m.ProduitListeComponent)
  },
  {
    path: 'produits/nouveau',
    loadComponent: () => import('./components/produit-form/produit-form.component').then(m => m.ProduitFormComponent)
  },
  {
    path: 'produits/modifier/:id',
    loadComponent: () => import('./components/produit-form/produit-form.component').then(m => m.ProduitFormComponent)
  },
  {
    path: 'categories',
    loadComponent: () => import('./components/categorie-liste/categorie-liste.component').then(m => m.CategorieListeComponent)
  },
  {
    path: 'stock-alertes',
    loadComponent: () => import('./components/stock-alertes/stock-alertes.component').then(m => m.StockAlertesComponent)
  }
];
