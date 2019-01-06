package com.example.twittertest.models;

public class User {

    public int id;
    public String name;

    @Override
    public String toString() {
        return "User{" +
                "Id="+id+
                ", Name="+name+
                "}";
    }
}
