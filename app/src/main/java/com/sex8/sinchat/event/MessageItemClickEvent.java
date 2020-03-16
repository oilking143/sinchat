package com.sex8.sinchat.event;

public class MessageItemClickEvent {
    public enum Type{
        ShowImage
    }
    private Type type;
    private String path;
    public MessageItemClickEvent(String path){
        type = Type.ShowImage;
        setPath(path);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Type getType(){
        return type;
    }
}
