package com.itmsd.medical.entities;

public class Email {

    private Integer id;
    private String name;
    private String sender;
    private String receiver;
    private String message;

    public Email(String name, String sender, String receiver, String message) {
        this.name = name;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

//getters and setters


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}