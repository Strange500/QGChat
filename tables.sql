-- Création de la table User
CREATE TABLE User (
                      uid INT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(1024) NOT NULL,
                      mail VARCHAR(1024) NOT NULL UNIQUE,
                      password VARCHAR(1024) NOT NULL
);

-- Création de la table Channel
CREATE TABLE Channel (
                         cid INT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(1024) NOT NULL
);

-- Création de la table Message
CREATE TABLE Message (
                         mid INT PRIMARY KEY AUTO_INCREMENT,
                         contenu VARCHAR(1024) NOT NULL
);

-- Table de liaison estAbonne (User - Channel)
CREATE TABLE estAbonne (
                           uid INT,
                           cid INT,
                           PRIMARY KEY (uid, cid),
                           FOREIGN KEY (uid) REFERENCES User(uid) ON DELETE CASCADE,
                           FOREIGN KEY (cid) REFERENCES Channel(cid) ON DELETE CASCADE
);

-- Table de liaison aEnvoyer (User - Message)
CREATE TABLE aEnvoyer (
                          uid INT,
                          mid INT,
                          PRIMARY KEY (uid, mid),
                          FOREIGN KEY (uid) REFERENCES User(uid) ON DELETE CASCADE,
                          FOREIGN KEY (mid) REFERENCES Message(mid) ON DELETE CASCADE
);

-- Table de liaison contient (Channel - Message)
CREATE TABLE contient (
                          cid INT,
                          mid INT UNIQUE,  -- Chaque message peut être dans un seul channel
                          PRIMARY KEY (cid, mid),
                          FOREIGN KEY (cid) REFERENCES Channel(cid) ON DELETE CASCADE,
                          FOREIGN KEY (mid) REFERENCES Message(mid) ON DELETE CASCADE
);
