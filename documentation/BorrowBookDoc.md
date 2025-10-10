# 📘 Documentation API — BorrowBookController

Ce contrôleur gère la création et la consultation des enregistrements d'emprunts dans le système.

---

## 🔹 1. Créer un Emprunt (Initiation par l’Abonné)

### `POST /borrowBook/create/{gerantID}`

| Champ            | Description                                                                                                                                 |
|------------------|---------------------------------------------------------------------------------------------------------------------------------------------|
| **But**          | Permettre à un **Abonné** d’initier une demande d’emprunt. L’emprunt est enregistré après validation du Gérant.                            |
| **Chemin**       | `POST` sur `{{baseURL}}/borrowBook/create/{gerantID}`                                                                                       |
| **UX Relatif**   | L’Abonné soumet la demande. Le Gérant est notifié et, après validation physique/hors-API, l’Abonné est invité à récupérer le livre.        |
| **Contraintes**  | Rôle requis : `ABONNE`. Le livre doit être **disponible**. L’Abonné et le Gérant doivent être **actifs**. La carte d’abonnement doit être **valide**. |
| **Codes Retour** | `201 Created`, `400 Bad Request` (livre non disponible, limite atteinte, carte invalide, etc.)                                              |

---

## 🔹 2. Consultation des Emprunts (Accès Gérant)

### `GET /borrowBook/get/{gerantID}?borrowID={id_emprunt}&abonneID={id_abonne}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Récupérer un **emprunt spécifique** dans le contexte d’un Abonné.                                    |
| **Chemin**       | `GET` sur `{{baseURL}}/borrowBook/get/{gerantID}?borrowID={id_emprunt}&abonneID={id_abonne}`         |
| **UX Relatif**   | Utilisé par le Gérant pour vérifier les détails d’une transaction (ex : lors du retour d’un livre).  |
| **Contraintes**  | Rôle requis : `GERANT`. Les trois ID doivent exister.                                                |
| **Codes Retour** | `200 OK`, `404 Not Found` (emprunt, abonné ou gérant inexistant)                                     |

---

### `GET /borrowBook/get/all/{gerantID}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Obtenir la **liste complète** des emprunts enregistrés dans le système.                              |
| **Chemin**       | `GET` sur `{{baseURL}}/borrowBook/get/all/{gerantID}`                                                |
| **UX Relatif**   | Utilisé comme **tableau de bord principal** pour la supervision des emprunts.                        |
| **Contraintes**  | Rôle requis : `GERANT`. Le `gerantID` est utilisé pour valider l’opérateur.                          |
| **Codes Retour** | `200 OK` (retourne `[]` si vide), `400 Bad Request` (validation échouée)                             |

---

### `GET /borrowBook/get/all/byAbonneID/{abonneId}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Récupérer l’**historique complet** des emprunts d’un Abonné.                                         |
| **Chemin**       | `GET` sur `{{baseURL}}/borrowBook/get/all/byAbonneID/{abonneId}`                                     |
| **UX Relatif**   | Utilisé par le Gérant pour consulter la **fiche client** d’un Abonné (retards, livres détenus, etc.).|
| **Contraintes**  | Rôle requis : `GERANT`. L’ID de l’Abonné doit exister.                                               |
| **Codes Retour** | `200 OK`, `404 Not Found` (abonné inexistant)                                                        |
