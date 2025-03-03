---
title: "Rapport SAé S4.A02.1 : Web Backend"
author: "Theron Milan, Roget Benjamin"
date: "2024-2025"
groupe: "Groupe X"
supervisor: "Philippe Mathieu"
documentclass: article
geometry: margin=2.5cm
---

# Description Générale de l'Application
QGChat est une application web permettant aux utilisateurs de créer et gérer des fils de discussion avec un ou plusieurs participants. Chaque utilisateur peut poster et lire des messages dans ces fils. L’application suit une architecture MVC en JEE, avec une interface responsive compatible avec ordinateur et mobile.

# Modélisation

## Modèle Conceptuel de Données (MCD)
![MCD](./res/documentation/MCD.png)

## Modèle Logique de Données (MLD)
```
// Insérer ici le MLD sous forme textuelle
```

# Requêtes SQL Pertinentes

### Création des Tables
```sql
CREATE TABLE Utilisateur (
    uid SERIAL PRIMARY KEY,
    username VARCHAR(1024) NOT NULL,
    mail VARCHAR(1024) UNIQUE NOT NULL,
    password VARCHAR(1024) NOT NULL
);

CREATE TABLE Channel (
    cid SERIAL PRIMARY KEY,
    name VARCHAR(1024) NOT NULL
);

CREATE TABLE Message (
    mid SERIAL PRIMARY KEY,
    contenu VARCHAR(1024) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE estAbonne (
    uid INT,
    cid INT,
    PRIMARY KEY (uid, cid),
    FOREIGN KEY (uid) REFERENCES Utilisateur(uid) ON DELETE CASCADE,
    FOREIGN KEY (cid) REFERENCES Channel(cid) ON DELETE CASCADE
);

CREATE TABLE aEnvoyer (
    uid INT,
    mid INT,
    PRIMARY KEY (uid, mid),
    FOREIGN KEY (uid) REFERENCES Utilisateur(uid) ON DELETE CASCADE,
    FOREIGN KEY (mid) REFERENCES Message(mid) ON DELETE CASCADE
);

CREATE TABLE contient (
    cid INT,
    mid INT UNIQUE,
    PRIMARY KEY (cid, mid),
    FOREIGN KEY (cid) REFERENCES Channel(cid) ON DELETE CASCADE,
    FOREIGN KEY (mid) REFERENCES Message(mid) ON DELETE CASCADE
);
```

### Insertion de Données Exemple
```sql
INSERT INTO Utilisateur (username, mail, password) VALUES
('user1', 'user1@example.com', MD5('password1')),
('user2', 'user2@example.com', MD5('password2')),
('user3', 'user3@example.com', MD5('password3'));

INSERT INTO Channel (name) VALUES
('General'),
('Random'),
('Announcements');

INSERT INTO Message (contenu) VALUES
('Welcome to the General channel!'),
('This is a random message.'),
('Important announcement: Meeting at 3 PM.');

INSERT INTO estAbonne (uid, cid) VALUES
(1, 1),
(2, 1),
(3, 2);

INSERT INTO aEnvoyer (uid, mid) VALUES
(1, 1),
(2, 2),
(3, 3);

INSERT INTO contient (cid, mid) VALUES
(1, 1),
(2, 2),
(3, 3);
```

### Requêtes Fréquentes
```sql
-- Récupérer tous les utilisateurs
SELECT * FROM Utilisateur;

-- Récupérer tous les canaux
SELECT * FROM Channel;

-- Récupérer tous les messages d’un canal donné
SELECT m.* FROM Message m
JOIN contient c ON m.mid = c.mid
WHERE c.cid = ?;

-- Récupérer tous les abonnements d’un utilisateur
SELECT c.* FROM Channel c
JOIN estAbonne e ON c.cid = e.cid
WHERE e.uid = ?;

-- Récupérer les messages envoyés par un utilisateur
SELECT m.* FROM Message m
JOIN aEnvoyer a ON m.mid = a.mid
WHERE a.uid = ?;

-- Vérifier si un utilisateur est abonné à un canal
SELECT * FROM estAbonne WHERE uid = ? AND cid = ?;

-- Ajouter un nouvel utilisateur
INSERT INTO Utilisateur (username, mail, password) VALUES (?, ?, MD5(?));

-- Ajouter un nouveau message dans un canal
INSERT INTO Message (contenu) VALUES (?);
INSERT INTO contient (cid, mid) VALUES (?, LAST_INSERT_ID());

-- Ajouter un abonnement
INSERT INTO estAbonne (uid, cid) VALUES (?, ?);
```

# Arborescence Globale de l’Application
```
/Projet_SAE
│── src/
│   ├── dto/
│   ├── model/
│   ├── servlet/
│   ├── dao/
│── web/
│   ├── js/
│── doc/
│── README.md
```

# Liste des Entrées des Contrôleurs
| Route | Fonctionnalité |
|---|---|
| `/register` | Inscription d’un utilisateur |
| `/login` | Connexion d’un utilisateur |
| `/fil/create` | Création d’un fil de discussion |
| `/fil/{id}` | Consultation d’un fil |
| `/message/post` | Publication d’un message |

# Points Techniques Difficiles et Résolutions

- **Sécurité des requêtes SQL** : Utilisation de requêtes préparées pour éviter les injections SQL.
- **XSS Protection** : Filtrage des entrées utilisateur avec `HTMLEncode`.
- **Gestion des sessions** : Implémentation d’un système sécurisé basé sur des tokens JWT.

---
**Fin du document**
