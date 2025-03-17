package fr.univ.lille.s4a021.dto;

import java.util.List;

public class Channel {
    private int cid;
    private String name;
    private int minuteBeforeExpiration;
    private List<Message> messages;

    public Channel(int cid, String name, int minuteBeforeExpiration) {
        this.cid = cid;
        this.name = name;
        this.minuteBeforeExpiration = minuteBeforeExpiration;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinuteBeforeExpiration() {
        return minuteBeforeExpiration;
    }

    public void setMinuteBeforeExpiration(int minuteBeforeExpiration) {
        this.minuteBeforeExpiration = minuteBeforeExpiration;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                '}';
    }
}