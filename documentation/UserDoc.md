# 👤 Documentation API — UserController

Ce contrôleur gère l’authentification, la gestion des utilisateurs (Gérants et Abonnés), ainsi que les transactions liées aux cartes d’abonnement.

---

## 🔹 1. Gestion des Utilisateurs et Authentification

| Endpoint                     | But                                                           | Rôle Requis | Contraintes Principales                                                                 | Codes de Retour Clés                                      |
|------------------------------|---------------------------------------------------------------|-------------|------------------------------------------------------------------------------------------|------------------------------------------------------------|
| `POST /register`             | Créer un compte utilisateur (Abonné ou Gérant).               | Public      | `mail` unique, `UserRequestDTO` valide, `roleName` obligatoire.                         | `201 Created`, `400 Bad Request` (email déjà utilisé)      |
| `POST /login`                | Authentifier un utilisateur et retourner un token de session. | Public      | `mail` et `password` doivent correspondre.                                               | `200 OK`, `401 Unauthorized`                               |
| `GET /get/{userID}`          | Récupérer les détails d’un utilisateur par ID.                | GERANT      | L’ID doit exister.                                                                      | `200 OK`, `404 Not Found`                                  |
| `GET /get/byRole`            | Rechercher un utilisateur par rôle et nom.                    | GERANT      | Le rôle et le nom doivent exister.                                                      | `200 OK`, `404 Not Found`                                  |
| `GET /get`                   | Obtenir la liste de tous les utilisateurs.                    | GERANT      | Utilisé pour le tableau de bord d’administration.                                       | `200 OK`                                                   |
| `GET /get/all/{role}`        | Obtenir la liste des utilisateurs d’un rôle spécifique.       | GERANT      | Le rôle doit exister (`ABONNE`, `GERANT`).                                              | `200 OK`                                                   |
| `PUT /update/{userID}`       | Mettre à jour les informations d’un utilisateur.              | Public      | L’ID doit exister. `roleName` obligatoire. Validation de `UserRequestDTO`.              | `200 OK`, `404 Not Found`, `400 Bad Request`               |
| `DELETE /delete/{userID}`    | Supprimer un compte utilisateur.                              | GERANT ou l’utilisateur lui-même | L’utilisateur doit exister. **Interdit si emprunts en cours.** | `204 No Content`, `404 Not Found`, `401 Unauthorized`      |

---

## 🔹 2. Gestion des Cartes d’Abonnement (Abonné)

| Endpoint                         | But                                    | Rôle Requis | Contraintes Principales                                                                                 | Codes de Retour Clés                            |
|----------------------------------|----------------------------------------|-------------|---------------------------------------------------------------------------------------------------------|-------------------------------------------------|
| `POST /create/card/{abonneID}`   | Créer la carte d’abonnement initiale.  | ABONNE      | Type d’abonnement obligatoire. L’Abonné ne doit pas déjà avoir de carte.                                | `200 OK`, `400 Bad Request`                     |
| `PUT /subscribe/card/byAbonne`   | Renouveler ou modifier son abonnement. | ABONNE      | Type d’abonnement obligatoire. L’Abonné doit être authentifié. <br/>NB:Un réabonnement écrase l'ancien. | `200 OK`, `401 Unauthorized`, `400 Bad Request` |
| `DELETE /delete/card/{abonneID}` | Supprimer sa carte d’abonnement.       | ABONNE      | **Interdit si des livres sont en cours d’emprunt.**                                                     | `204 No Content`, `404 Not Found`               |

---

## 🔹 3. Gestion des Cartes (Gérant)

| Endpoint                          | But                                                       | Rôle Requis | Contraintes Principales                                                  | Codes de Retour Clés                          |
|-----------------------------------|------------------------------------------------------------|-------------|---------------------------------------------------------------------------|------------------------------------------------|
| `GET /get/card/{abonneID}`        | Récupérer les détails de la carte d’un Abonné.            | GERANT      | L’ID de l’Abonné doit exister et avoir une carte.                        | `200 OK`, `404 Not Found`                     |
| `GET /get/card/byGerant`          | Obtenir la liste de toutes les cartes d’abonnement.       | GERANT      | Le `gerantID` est utilisé pour valider l’opérateur.                      | `200 OK`                                      |
| `PUT /revoque/card/{abonneID}`    | Révoquer le droit d’emprunt d’un Abonné.                  | GERANT      | L’Abonné et le Gérant doivent exister. Met à jour le statut à "révoqué".| `200 OK`, `404 Not Found`                     |
 