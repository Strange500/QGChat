package fr.univ.lille.s4a021.dto;

public class Message {
    private int mid;
    private String contenu;
    private int senderId;
    private int channelId;

    public Message(int mid, String contenu, int senderId, int channelId) {
        this.mid = mid;
        this.contenu = contenu;
        this.senderId = senderId;
        this.channelId = channelId;
    }

    // Getters and Setters
    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "mid=" + mid +
                ", contenu='" + contenu + '\'' +
                ", senderId=" + senderId +
                ", channelId=" + channelId +
                '}';
    }
}
