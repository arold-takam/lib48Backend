# 🗂️ Documentation API — CategoryController

Ce contrôleur gère les opérations CRUD sur les catégories de livres. Les endpoints de consultation sont publics pour permettre le filtrage du catalogue.

---

## 🔹 1. Création de Catégorie (Accès Gérant)

### `POST /categories/create`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Ajouter une nouvelle thématique de classification dans le système.                                   |
| **Chemin**       | `POST` sur `{{baseURL}}/categories/create` — Consomme `application/json`                             |
| **UX Relatif**   | La catégorie apparaît dans le menu de sélection pour l’ajout de livres et devient un filtre public.  |
| **Contraintes**  | Rôle requis : `GERANT`. Le nom doit être **unique** et **non vide**.                                 |
| **Codes Retour** | `201 Created`, `400 Bad Request` (nom déjà existant ou invalide)                                     |

---

## 🔹 2. Consultation des Catégories (Accès Public)

### `GET /categories/get/All`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Obtenir la liste complète des catégories existantes.                                                 |
| **Chemin**       | `GET` sur `{{baseURL}}/categories/get/All`                                                           |
| **UX Relatif**   | Utilisé pour les filtres du catalogue et le menu déroulant lors de la création d’un livre.           |
| **Contraintes**  | Rôle requis : `Public` (`permitAll()`)                                                               |
| **Codes Retour** | `200 OK` (retourne `[]` si vide)                                                                     |

---

### `GET /categories/get/byID/{id}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Récupérer les détails d’une catégorie par son identifiant.                                           |
| **Chemin**       | `GET` sur `{{baseURL}}/categories/get/byID/{id}`                                                     |
| **UX Relatif**   | Utilisé pour vérifier l’existence ou les détails avant de charger les livres associés.               |
| **Contraintes**  | Rôle requis : `Public`. L’ID doit exister.                                                           |
| **Codes Retour** | `200 OK`, `404 Not Found`                                                                            |

---

### `GET /categories/get/byName?name={nom}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Rechercher une catégorie par son nom.                                                                |
| **Chemin**       | `GET` sur `{{baseURL}}/categories/get/byName?name={nom}`                                             |
| **UX Relatif**   | Utilisé pour la recherche ou la navigation directe.                                                  |
| **Contraintes**  | Rôle requis : `Public`. Le nom doit exister et correspondre.                                         |
| **Codes Retour** | `200 OK`, `404 Not Found`                                                                            |

---

## 🔹 3. Modification et Suppression (Accès Gérant)

### `PUT /categories/update/{id}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Modifier le nom d’une catégorie existante.                                                           |
| **Chemin**       | `PUT` sur `{{baseURL}}/categories/update/{id}` — Consomme `application/json`                         |
| **UX Relatif**   | Utilisé dans le tableau de bord d’administration. Mise à jour immédiate pour les livres associés.    |
| **Contraintes**  | Rôle requis : `GERANT`. L’ID doit exister. Le nouveau nom doit être **unique**.                      |
| **Codes Retour** | `200 OK`, `400 Bad Request` (catégorie inexistante ou nom déjà utilisé)                              |

---

### `DELETE /categories/delete/{id}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Supprimer une catégorie obsolète ou erronée.                                                        |
| **Chemin**       | `DELETE` sur `{{baseURL}}/categories/delete/{id}`                                                    |
| **UX Relatif**   | La catégorie disparaît des menus et filtres. Opération irréversible.                                 |
| **Contraintes**  | Rôle requis : `GERANT`. L’ID doit exister. **Interdit si des livres sont encore associés.**         |
| **Codes Retour** | `204 No Content`, `400 Bad Request` (catégorie inexistante ou suppression interdite)                |
