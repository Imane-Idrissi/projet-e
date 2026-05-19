import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Categorie, CreateCategorieRequest } from '../models/categorie.model';

@Injectable({
  providedIn: 'root'
})
export class CategorieService {

  private apiUrl = 'http://localhost:8080/api/categories';

  constructor(private http: HttpClient) {}

  listerCategoriesRacines(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(this.apiUrl);
  }

  listerToutes(): Observable<Categorie[]> {
    return this.http.get<Categorie[]>(`${this.apiUrl}/toutes`);
  }

  obtenirCategorie(id: number): Observable<Categorie> {
    return this.http.get<Categorie>(`${this.apiUrl}/${id}`);
  }

  creerCategorie(request: CreateCategorieRequest): Observable<Categorie> {
    return this.http.post<Categorie>(this.apiUrl, request);
  }

  modifierCategorie(id: number, request: CreateCategorieRequest): Observable<Categorie> {
    return this.http.put<Categorie>(`${this.apiUrl}/${id}`, request);
  }

  supprimerCategorie(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
