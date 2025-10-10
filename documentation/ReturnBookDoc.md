# 🔄 Documentation API — ReturnBookController

Ce contrôleur gère la gestion des retours de livres. Toutes les opérations sont réservées au rôle **`GERANT`** pour garantir l’intégrité de l’inventaire et des transactions.

---

## 🔹 1. Enregistrement d’un Retour

### `POST /returnBook/create/{idReturnGerantID}?etatLivre={etat}`

| Champ            | Description                                                                                                                         |
|------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| **But**          | Finaliser un emprunt en enregistrant le retour physique d’un livre.                                                                 |
| **Chemin**       | `POST` sur `{{baseURL}}/returnBook/create/{idReturnGerantID}?etatLivre={etat}`                                                      |
| **UX Relatif**   | Utilisé par le Gérant au comptoir. Il inspecte le livre, détermine son **état physique** (`EtatLivre`), et met à jour l’inventaire. |
| **Contraintes**  | Rôle requis : `GERANT`. L’emprunt doit exister et ne pas être déjà retourné. Le `etatLivre` est obligatoire.                        |
| **Codes Retour** | `200 OK` (succès), `201 Created` (si nouvel enregistrement), `400 Bad Request` (livre non emprunté, ID invalide, etc.)              |

---

## 🔹 2. Consultation des Retours (Accès Gérant)

### `GET /returnBook/get/{gerantID}?returnID={id_retour}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Récupérer les détails d’une **transaction de retour spécifique**.                                    |
| **Chemin**       | `GET` sur `{{baseURL}}/returnBook/get/{gerantID}?returnID={id_retour}`                               |
| **UX Relatif**   | Utilisé pour auditer ou consulter un **reçu de retour** précis.                                      |
| **Contraintes**  | Rôle requis : `GERANT`. L’ID du retour doit exister.                                                 |
| **Codes Retour** | `200 OK`, `404 Not Found` (ID de retour ou gérant inexistant)                                        |

---

### `GET /returnBook/get/byAbonneID/{gerantID}?abonneID={id_abonne}`

| Champ            | Description                                                                       |
|------------------|-----------------------------------------------------------------------------------|
| **But**          | Récupérer l’**historique des retours** effectués par un Abonné.                   |
| **Chemin**       | `GET` sur `{{baseURL}}/returnBook/get/byAbonneID/{gerantID}?abonneID={id_abonne}` |
| **UX Relatif**   | Utilisé dans la fiche client pour auditer la fiabilité d’un Abonné.               |
| **Contraintes**  | Rôle requis : `GERANT`. L’ID de l’Abonné doit exister.                            |
| **Codes Retour** | `200 OK` (retourne `[]` si aucun retour), `400 Bad Request` (ID invalide)         |

---

### `GET /returnBook/get/all/{gerantID}`

| Champ            | Description                                                                                          |
|------------------|------------------------------------------------------------------------------------------------------|
| **But**          | Obtenir la **liste complète** de toutes les transactions de retour.                                  |
| **Chemin**       | `GET` sur `{{baseURL}}/returnBook/get/all/{gerantID}`                                                |
| **UX Relatif**   | Utilisé comme **tableau de bord** d’activité pour le suivi global.                                   |
| **Contraintes**  | Rôle requis : `GERANT`.                                                                              |
| **Codes Retour** | `200 OK` (retourne `[]` si vide)                                                                     |

---

### `GET /returnBook/get/all/byDate/{gerantID}?dateRetour={AAAA-MM-JJ}`

| Champ            | Description                                                                          |
|------------------|--------------------------------------------------------------------------------------|
| **But**          | Filtrer les retours effectués à une **date précise**.                                |
| **Chemin**       | `GET` sur `{{baseURL}}/returnBook/get/all/byDate/{gerantID}?dateRetour={AAAA-MM-JJ}` |
| **UX Relatif**   | Utilisé pour les **rapports quotidiens** ou les audits ciblés.                       |
| **Contraintes**  | Rôle requis : `GERANT`. La date doit être au format `AAAA-MM-JJ`.                    |
| **Codes Retour** | `200 OK` (retourne `[]` si aucun retour ce jour-là)                                  |
