package fr.univ.lille.s4a021.dto;

import java.util.List;

public class Channel {
    private int cid;
    private String name;
    private List<Message> messages;

    public Channel(int cid, String name) {
        this.cid = cid;
        this.name = name;
    }

    // Getters and Setters
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
                ", messages=" + messages +
                '}';
    }
}