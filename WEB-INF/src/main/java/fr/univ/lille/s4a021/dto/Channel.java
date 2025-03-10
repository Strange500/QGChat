package fr.univ.lille.s4a021.dto;

public class Channel {
    private int cid;
    private String name;

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

    @Override
    public String toString() {
        return "Channel{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                '}';
    }
}