package fr.univ.lille.s4a021.dto;

public class User {
    private int uid;
    private String username;
    private String mail;

    // Constructeur
    public User(int uid, String username, String mail) {
        this.uid = uid;
        this.username = username;
        this.mail = mail;
    }

    // Getters
    public int getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getMail() {
        return mail;
    }

    // Setters
    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", mail='" + mail + '\'' +
                '}';
    }
}
