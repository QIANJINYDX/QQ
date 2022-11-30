package com.example.qq.db.model;

public class Concater {

    private Integer id;
    private String name;
    private String phone;
    private Integer pic;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Concater() {
    }

    public Concater(Integer id, String name, String phone, Integer pic) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.pic = pic;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPic() {
        return pic;
    }

    public void setPic(Integer pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        return "Concater{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", pic='" + pic + '\'' +
                '}';
    }
}
