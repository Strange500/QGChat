DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS contient CASCADE;
DROP TABLE IF EXISTS aEnvoyer CASCADE;
DROP TABLE IF EXISTS estAbonne CASCADE;
DROP TABLE IF EXISTS Message CASCADE;
DROP TABLE IF EXISTS isAdmin CASCADE;
DROP TABLE IF EXISTS Channel CASCADE;
DROP TABLE IF EXISTS Utilisateur CASCADE;



CREATE TABLE Utilisateur (
                      uid SERIAL PRIMARY KEY,
                      username VARCHAR(1024) NOT NULL,
                      mail VARCHAR(1024) NOT NULL UNIQUE,
                      password VARCHAR(1024) NOT NULL,
                        CONSTRAINT check_mail_not_empty CHECK (mail <> ''),
                        CONSTRAINT check_username_not_empty CHECK (username <> ''),
                        CONSTRAINT check_password_not_empty CHECK (password <> '')
);

-- Création de la table Channel
CREATE TABLE Channel (
                         cid SERIAL PRIMARY KEY,
                         name VARCHAR(1024) NOT NULL
);

-- Création de la table Message
CREATE TABLE Message (
                         mid SERIAL PRIMARY KEY ,
                         contenu TEXT NOT NULL,
                         timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table de liaison estAbonne (User - Channel)
CREATE TABLE estAbonne (
                           uid INT,
                           cid INT,
                           PRIMARY KEY (uid, cid),
                           FOREIGN KEY (uid) REFERENCES Utilisateur(uid) ON DELETE CASCADE,
                           FOREIGN KEY (cid) REFERENCES Channel(cid) ON DELETE CASCADE
);

CREATE TABLE isAdmin (
                           uid INT,
                           cid INT,
                           PRIMARY KEY (uid, cid),
                           FOREIGN KEY (uid) REFERENCES Utilisateur(uid) ON DELETE CASCADE,
                           FOREIGN KEY (cid) REFERENCES Channel(cid) ON DELETE CASCADE
);

-- Table de liaison aEnvoyer (User - Message)
CREATE TABLE aEnvoyer (
                          uid INT,
                          mid INT,
                          PRIMARY KEY (uid, mid),
                          FOREIGN KEY (uid) REFERENCES Utilisateur(uid) ON DELETE CASCADE,
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

-- Tabme de liaison likes (Message - Utilisateur)
CREATE TABLE likes (
                          mid INT,
                          uid INT,
                          PRIMARY KEY (mid, uid),
                          CONSTRAINT fk_likes_message FOREIGN KEY (mid) REFERENCES Message(mid) ON DELETE CASCADE,
                          CONSTRAINT fk_likes_utilisateur FOREIGN KEY (uid) REFERENCES Utilisateur(uid) ON DELETE CASCADE
);

INSERT INTO Utilisateur (username, mail, password) VALUES
('user1', 'user1@example.com', MD5('password1')),
('user2', 'user2@example.com', MD5('password2')),
('user3', 'user3@example.com', MD5('password3'));

INSERT INTO Channel (name) VALUES
('General'),
('Random'),
('Announcements');

INSERT INTO isAdmin (uid, cid) VALUES
(1, 1),
(2, 2),
(3, 3);

INSERT INTO Message (contenu) VALUES
('Welcome to the General channel!'),
('This is a random message.'),
('Important announcement: Meeting at 3 PM.');

INSERT INTO estAbonne (uid, cid) VALUES
(1, 1),
(2, 1),
(2,3),
(3, 2);

INSERT INTO aEnvoyer (uid, mid) VALUES
(1, 1),
(2, 2),
(3, 3);

INSERT INTO contient (cid, mid) VALUES
(1, 1),
(2, 2),
(3, 3);
