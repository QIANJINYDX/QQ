package com.example.qq.Entities;

public class AddPeople {
    private String name;
    private String phone;
    private String number;

    public AddPeople(String name, String phone, String number) {
        this.name = name;
        this.phone = phone;
        this.number = number;
    }

    @Override
    public String toString() {
        return "AddPeople{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", number='" + number + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
