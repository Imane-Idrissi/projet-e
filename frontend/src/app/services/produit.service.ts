import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Produit, CreateProduitRequest } from '../models/produit.model';

@Injectable({
  providedIn: 'root'
})
export class ProduitService {

  private apiUrl = 'http://localhost:8080/api/produits';

  constructor(private http: HttpClient) {}

  listerProduits(): Observable<Produit[]> {
    return this.http.get<Produit[]>(this.apiUrl);
  }

  obtenirProduit(id: number): Observable<Produit> {
    return this.http.get<Produit>(`${this.apiUrl}/${id}`);
  }

  creerProduit(request: CreateProduitRequest): Observable<Produit> {
    return this.http.post<Produit>(this.apiUrl, request);
  }

  modifierProduit(id: number, request: CreateProduitRequest): Observable<Produit> {
    return this.http.put<Produit>(`${this.apiUrl}/${id}`, request);
  }

  archiverProduit(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  rechercher(designation?: string, categorieId?: number, prixMin?: number, prixMax?: number): Observable<Produit[]> {
    let params = new HttpParams();
    if (designation) params = params.set('designation', designation);
    if (categorieId) params = params.set('categorieId', categorieId.toString());
    if (prixMin != null) params = params.set('prixMin', prixMin.toString());
    if (prixMax != null) params = params.set('prixMax', prixMax.toString());
    return this.http.get<Produit[]>(`${this.apiUrl}/recherche`, { params });
  }

  listerStockBas(): Observable<Produit[]> {
    return this.http.get<Produit[]>(`${this.apiUrl}/stock-bas`);
  }
}
