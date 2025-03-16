package fr.univ.lille.s4a021.dto;

public class Channel {
    private int cid;
    private String name;
    int minuteBeforeExpiration;

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

    @Override
    public String toString() {
        return "Channel{" +
                "cid=" + cid +
                ", name='" + name + '\'' +
                '}';
    }
}