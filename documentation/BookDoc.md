# 📚 Documentation API — BookController

Ce contrôleur gère toutes les opérations liées aux livres de la librairie : ajout, consultation, modification et suppression.

---

## 🔹 1. Création d’un Nouveau Livre

### `POST /books/create`

| Champ            | Description                                                                                                                                      |
|------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| **But**          | Permettre au **Gérant** d’enregistrer un nouveau livre physique dans l’inventaire.                                                               |
| **Chemin**       | `POST` sur `{{baseURL}}/books/create` — Consomme `multipart/form-data`                                                                           |
| **UX Relatif**   | Le livre apparaît immédiatement dans l’inventaire avec le statut `disponible`.                                                                   |
| **Contraintes**  | Rôle requis : `GERANT`. Le **titre** doit être **unique**. L’ID de **catégorie** doit exister. La **couverture** (`coverImage`) est obligatoire. |
| **Codes Retour** | `201 Created`, `400 Bad Request` (données manquantes), `409 Conflict` (titre déjà existant)                                                      |

---

## 🔹 2. Consultation des Livres (Accès Public)

### `GET /books/get/All`

| Champ            | Description                                                                            |
|------------------|----------------------------------------------------------------------------------------|
| **But**          | Fournir la **liste complète** des livres enregistrés.                                  |
| **Chemin**       | `GET` sur `{{baseURL}}/books/get/All`                                                  |
| **UX Relatif**   | Utilisé pour la page d’accueil et le catalogue. Affichage en liste ou grille.          |
| **Contraintes**  | Rôle requis : `Public` (`permitAll()`). Pagination recommandée au-delà de 1000 livres. |
| **Codes Retour** | `200 OK` (succès, retourne `[]` si vide)                                               |

---

### `GET /books/get/byID/{id}`

| Champ            | Description                                                                 |
|------------------|-----------------------------------------------------------------------------|
| **But**          | Récupérer les détails d’un livre par son identifiant (`ID`).                |
| **Chemin**       | `GET` sur `{{baseURL}}/books/get/byID/{id}`                                 |
| **UX Relatif**   | Déclenché par un clic ou une recherche. Affiche la fiche complète du livre. |
| **Contraintes**  | Rôle requis : `Public`. Le livre doit exister.                              |
| **Codes Retour** | `200 OK`, `404 Not Found` (livre inexistant)                                |

---

### `GET /books/get/byTitle?title={titre}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Rechercher un livre par son **titre unique**.                                                        |
| **Chemin**       | `GET` sur `{{baseURL}}/books/get/byTitle?title={titre}`                                              |
| **UX Relatif**   | Utilisé dans la barre de recherche principale.                                                       |
| **Contraintes**  | Rôle requis : `Public`. Le titre doit exister.                                                       |
| **Codes Retour** | `200 OK`, `404 Not Found`                                                                            |

---

### `GET /books/get/byCategory?categorie={nom}`

| Champ            | Description                                                                         |
|------------------|-------------------------------------------------------------------------------------|
| **But**          | Filtrer les livres par **catégorie**.                                               |
| **Chemin**       | `GET` sur `{{baseURL}}/books/get/byCategory?categorie={nom}`                        |
| **UX Relatif**   | Déclenché par clic sur une catégorie. Affiche un sous-ensemble du catalogue.        |
| **Contraintes**  | Rôle requis : `Public`. Le nom de la catégorie doit exister. Retourne `[]` si vide. |
| **Codes Retour** | `200 OK`, `404 Not Found`                                                           |

---

## 🔹 3. Modification et Suppression (Accès Gérant)

### `PUT /books/update/{id}`

| Champ            | Description                                                                        |
|------------------|------------------------------------------------------------------------------------|
| **But**          | Modifier les informations d’un livre existant (titre, description, état physique). |
| **Chemin**       | `PUT` sur `{{baseURL}}/books/update/{id}` — Consomme `multipart/form-data`         |
| **UX Relatif**   | Utilisé dans le tableau de bord de gestion. Supporte la mise à jour partielle.     |
| **Contraintes**  | Rôle requis : `GERANT`. L’ID doit exister. Le titre modifié doit rester unique.    |
| **Codes Retour** | `200 OK`, `400 Bad Request` (ID inexistant ou titre déjà utilisé)                  |

---

### `DELETE /books/delete/{id}`

| Champ            | Description                                                                       |
|------------------|-----------------------------------------------------------------------------------|
| **But**          | Supprimer définitivement un livre de l’inventaire.                                |
| **Chemin**       | `DELETE` sur `{{baseURL}}/books/delete/{id}`                                      |
| **UX Relatif**   | Le livre disparaît du catalogue. Opération irréversible.                          |
| **Contraintes**  | Rôle requis : `GERANT`. L’ID doit exister. **Interdit si le livre est emprunté.** |
| **Codes Retour** | `204 No Content`, `400 Bad Request` (livre inexistant ou emprunté)                |


## 🔹 4. Endpoints spécifiques aux images de couverture

### `GET /books/{id}/cover-image`

| Champ            | Description                                                                                                 |
|------------------|-------------------------------------------------------------------------------------------------------------|
| **But**          | Récupérer les images de couverture des livres.                                                              |
| **Chemin**       | `GET` sur `{{baseURL}}/books/get/{id}/cover-image}`                                                         |
| **UX Relatif**   | Permet de récupérer les images de couverture d'un livre par son ID pour les afficher au niveau du Frontend. |
| **Contraintes**  | Rôle requis : `Public`.                                                                                     |
| **Codes Retour** | `200 OK`, `404 Not Found`                                                                                   |


### `GET /books/{id}/cover-image-url`

| Champ            | Description                                                               |
|------------------|---------------------------------------------------------------------------|
| **But**          | Récupérer l'URL de l'image de couverture des livres .                     |
| **Chemin**       | `GET` sur `{{baseURL}}/books/get/{id}/cover-image-url}`                   |
| **UX Relatif**   | Permet de récupérer l'URL de l'image de couverture d'un livre par son ID. |
| **Contraintes**  | Rôle requis : `Public`.                                                   |
| **Codes Retour** | `200 OK`, `404 Not Found`                                                 |

### `GET /books/{id}/has-cover-image`

| Champ            | Description                                                                                               |
|------------------|-----------------------------------------------------------------------------------------------------------|
| **But**          | Vérifier si un livre possède une  image de couverture.                                                    |
| **Chemin**       | `GET` sur `{{baseURL}}/books/get/{id}/has-cover-image}`                                                   |
| **UX Relatif**   | Permet de vérifier si un livre possède une  image de couverture par son ID pour effectuer les opérations. |
| **Contraintes**  | Rôle requis : `Public`.                                                                                   |
| **Codes Retour** | `200 OK`, `404 Not Found`                                                                                 |

