export interface Produit {
  id: number;
  reference: string;
  designation: string;
  description: string;
  prixHT: number;
  tauxTVA: number;
  prixTTC: number;
  quantiteStock: number;
  seuilAlerte: number;
  categorieId: number | null;
  categorieNom: string | null;
  actif: boolean;
  stockBas: boolean;
  dateCreation: string;
  dateModification: string;
}

export interface CreateProduitRequest {
  designation: string;
  description: string;
  prixHT: number;
  tauxTVA: number;
  quantiteStock: number;
  seuilAlerte: number;
  categorieId: number | null;
}
