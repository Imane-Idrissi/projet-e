export interface Categorie {
  id: number;
  nom: string;
  description: string;
  parentId: number | null;
  parentNom: string | null;
  sousCategories: Categorie[];
  nombreProduits: number;
  dateCreation: string;
}

export interface CreateCategorieRequest {
  nom: string;
  description: string;
  parentId: number | null;
}
