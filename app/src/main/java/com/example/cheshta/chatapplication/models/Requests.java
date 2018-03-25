package com.example.cheshta.chatapplication.models;

/**
 * Created by chesh on 3/25/2018.
 */

public class Requests {
    private String request_type;

    public Requests() {
    }

    public Requests(String request_type) {
        this.request_type = request_type;
    }

    public String getrequest_type() {
        return request_type;
    }

    public void setrequest_type(String request_type) {
        this.request_type = request_type;
    }
}
