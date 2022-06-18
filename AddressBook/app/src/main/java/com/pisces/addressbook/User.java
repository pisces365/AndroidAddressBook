package com.pisces.addressbook;

public class User {
    private String id;
    private String name;
    private String phone;
    private String work;
    private String address;

    public User() {
    }

    public User(String name, String phone, String work, String address) {
        this.name = name;
        this.phone = phone;
        this.work = work;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
