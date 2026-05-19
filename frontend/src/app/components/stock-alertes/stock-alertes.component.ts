import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProduitService } from '../../services/produit.service';
import { Produit } from '../../models/produit.model';

@Component({
  selector: 'app-stock-alertes',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './stock-alertes.component.html',
  styleUrl: './stock-alertes.component.scss'
})
export class StockAlertesComponent implements OnInit {

  produitsStockBas: Produit[] = [];
  loading = true;

  constructor(private produitService: ProduitService) {}

  ngOnInit(): void {
    this.charger();
  }

  charger(): void {
    this.loading = true;
    this.produitService.listerStockBas().subscribe({
      next: (data) => {
        this.produitsStockBas = data.sort((a, b) => a.quantiteStock - b.quantiteStock);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  getNiveauUrgence(produit: Produit): string {
    const ratio = produit.quantiteStock / produit.seuilAlerte;
    if (ratio <= 0.3) return 'critique';
    if (ratio <= 0.7) return 'warning';
    return 'attention';
  }

  getProgressWidth(produit: Produit): number {
    return Math.min((produit.quantiteStock / produit.seuilAlerte) * 100, 100);
  }
}
