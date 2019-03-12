package com.mimmarcelo.chatbluetooth.bluetooth;

public class Message {
    public static final int ERROR = 0;
    public static final int STATUS = 1;
    public static final int MESSAGE = 2;

    private int code;
    private String message;

    public Message() {
        this.code = STATUS;
        this.message = "";
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage(byte[] message){
        this.message = new String(message);
    }

    public boolean hasMessage() {
        return !message.trim().equals("");
    }
}
