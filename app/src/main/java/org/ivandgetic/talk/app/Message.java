package org.ivandgetic.talk.app;

public class Message {
    String name;
    String message;
    public Message(String name,String message) {
        this.name=name;
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }
}