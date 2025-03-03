package fr.univ.lille.s4a021.dto;


public class ImgMessage extends Message {
    private String path;

    public ImgMessage(Message msg, String path) {
        super(msg.getMid(), msg.getContenu(), msg.getSenderId(), msg.getChannelId(), msg.getTimestamp());
        this.path = path;
    }

    public String getImg() {
        return path;
    }

    public void setImg(String img) {
        this.path = img;
    }
}
