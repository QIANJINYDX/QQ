package com.example.qq.model;

import org.litepal.LitePalApplication;
import org.litepal.crud.LitePalSupport;
import org.litepal.exceptions.DataSupportException;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

public class Chat extends LitePalSupport {
    private int id;
    private String from_id;
    private String to_id;
    private String msg;
    private Date send_time;
    private int tpye;

    public int getTpye() {
        return tpye;
    }

    public void setTpye(int tpye) {
        this.tpye = tpye;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getTo_id() {
        return to_id;
    }

    public void setTo_id(String to_id) {
        this.to_id = to_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getSend_time() {
        return send_time;
    }

    public void setSend_time(Date send_time) {
        this.send_time = send_time;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", from_id='" + from_id + '\'' +
                ", to_id='" + to_id + '\'' +
                ", msg='" + msg + '\'' +
                ", send_time=" + send_time +
                ", tpye=" + tpye +
                '}';
    }
}
