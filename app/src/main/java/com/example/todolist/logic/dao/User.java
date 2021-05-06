package com.example.todolist.logic.dao;

import org.litepal.crud.DataSupport;

public class User extends DataSupport {
    private String count;

    private String password;

    private boolean isRemember;

    public User() {

    }

    public boolean isRemember() {
        return isRemember;
    }

    public void setRemember(boolean remember) {
        isRemember = remember;
    }

    public User(String count, String password, boolean isRemember) {
        this.count = count;
        this.password = password;
        this.isRemember = isRemember;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String id) {
        this.count = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
