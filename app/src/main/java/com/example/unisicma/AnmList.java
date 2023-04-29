package com.example.unisicma;

public class AnmList {


    public AnmList(String request_text, String request_id) {
        this.request_text = request_text;
        this.request_id = request_id;
    }

    public String getRequest_text() {
        return request_text;
    }

    public void setRequest_text(String request_text) {
        this.request_text = request_text;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    private String request_text;
    private String request_id;
}
