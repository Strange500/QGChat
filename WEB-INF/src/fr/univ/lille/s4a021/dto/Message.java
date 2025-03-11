package fr.univ.lille.s4a021.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private int mid;
    private String contenu;
    private int senderId;
    private int channelId;
    private final String timestamp;

    private Date dateSend;



    public Message(int mid, String contenu, int senderId, int channelId, String timestamp) {
        this.mid = mid;
        this.contenu = contenu;
        this.senderId = senderId;
        this.channelId = channelId;
        this.timestamp = timestamp;
        try {
            this.dateSend = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public Date getDateSend() {
        return dateSend;
    }

    public String getTimestamp() {
        return timestamp;
    }


    public String getTimeAgo() {
        long diff = new Date().getTime() - dateSend.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        if (diffDays > 0) {
            return new SimpleDateFormat("dd/MM/yyyy 'à' HH:mm").format(dateSend);
        } else if (diffHours > 0) {
            return diffHours + " hour" + (diffHours > 1 ? "s" : "") + " ago";
        } else if (diffMinutes > 0) {
            return diffMinutes + " minute" + (diffMinutes > 1 ? "s" : "") + " ago";
        } else {
            return diffSeconds + " second" + (diffSeconds > 1 ? "s" : "") + " ago";
        }
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

    public String getImg() {
        return null;
    }
}
