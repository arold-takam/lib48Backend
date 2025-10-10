# 📑 Documentation API — HistoryController

Ce contrôleur gère la consultation du journal des opérations de la librairie. Toutes les opérations sont réservées au rôle **`GERANT`** pour garantir la sécurité et la confidentialité des données.

---

## 🔹 1. Consultation Unitaire

### `GET /history/get/{historyID}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Récupérer les détails d’une **entrée spécifique** du journal d’historique.                           |
| **Chemin**       | `GET` sur `{{baseURL}}/history/get/{historyID}`                                                      |
| **UX Relatif**   | Utilisé par le Gérant pour vérifier une transaction précise (alerte ou audit ponctuel).              |
| **Contraintes**  | Rôle requis : `GERANT`. L’ID de l’historique doit exister.                                           |
| **Codes Retour** | `200 OK`, `404 Not Found` (historique inexistant), `400 Bad Request` (erreur de validation)          |

---

## 🔹 2. Consultation par Type d’Opération

### `GET /history/get/all/byTypeOperation?typeOpperation={type}`

| Champ            | Description                                                                                        |
|------------------|----------------------------------------------------------------------------------------------------|
| **But**          | Filtrer les opérations selon leur **nature** (`TypeOpperation` : Prêt, Retour, Ajout Livre, etc.). |
| **Chemin**       | `GET` sur `{{baseURL}}/history/get/all/byTypeOperation?typeOpperation={type}`                      |
| **UX Relatif**   | Utilisé pour obtenir un aperçu rapide d’une catégorie d’activité (ex : prêts du mois).             |
| **Contraintes**  | Rôle requis : `GERANT`. Le paramètre `typeOpperation` est obligatoire.                             |
| **Codes Retour** | `200 OK` (retourne `[]` même si vide)                                                              |

---

## 🔹 3. Consultation par État d’Opération

### `GET /history/All/byEtat?etatOpperation={etat}`

| Champ            | Description                                                                                              |
|------------------|:---------------------------------------------------------------------------------------------------------|
| **But**          | Filtrer les opérations selon leur **statut d’exécution** (`EtatOpperation` : Succès, Échec, En attente). |
| **Chemin**       | `GET` sur `{{baseURL}}/history/All/byEtat?etatOpperation={etat}`                                         |
| **UX Relatif**   | Utilisé comme tableau de bord de **surveillance** pour identifier les anomalies ou blocages.             |
| **Contraintes**  | Rôle requis : `GERANT`. Le paramètre `etatOpperation` est obligatoire.                                   |
| **Codes Retour** | `200 OK` (retourne `[]` même si vide)                                                                    |

---

## 🔹 4. Consultation par Utilisateur

### `GET /history/get/user?userName={userName}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Récupérer l’historique de toutes les opérations associées à un utilisateur donné.                    |
| **Chemin**       | `GET` sur `{{baseURL}}/history/get/user?userName={userName}`                                         |
| **UX Relatif**   | Utilisé pour un **audit de compte** lors d’une vérification ou d’un litige.                          |
| **Contraintes**  | Rôle requis : `GERANT`. Le `userName` doit correspondre à un utilisateur existant.                   |
| **Codes Retour** | `200 OK`, `404 Not Found` (utilisateur inexistant)                                                   |
