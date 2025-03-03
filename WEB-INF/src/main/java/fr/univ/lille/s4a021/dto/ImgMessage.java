package fr.univ.lille.s4a021.dto;


public class ImgMessage extends Message {
    private String content;
    private static final String BEGIN = "img:";

    public ImgMessage(Message msg, String path) {
        super(msg.getMid(), msg.getContenu(), msg.getSenderId(), msg.getChannelId(), msg.getTimestamp());
        this.content = path;
    }

    public String getImg() {
        return this.getContenu().substring(BEGIN.length());
    }

    public void setImg(String img) {
        this.content = img;
    }
}
