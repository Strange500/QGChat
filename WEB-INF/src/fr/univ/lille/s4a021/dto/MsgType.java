package fr.univ.lille.s4a021.dto;

public enum MsgType {
    TEXT("text"),
    IMAGE("image");


    private String type;


    MsgType(String type) {
        this.type = type;
    }

    public static MsgType fromString(String type) {
        for (MsgType t : MsgType.values()) {
            if (t.getType().equals(type)) {
                return t;
            }
        }
        return null;
    }

    public String getType() {
        return type;
    }
}
