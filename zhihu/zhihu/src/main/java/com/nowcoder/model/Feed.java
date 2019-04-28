package com.nowcoder.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class Feed {
    private int id;
    private int type;
    private int userId;
    private Date createdDate;
    //JSON
    private String data;
    private JSONObject dataJSON = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        dataJSON = JSONObject.parseObject(data); //初始化JSON对象
    }

    public String get(String key){ //有了这个函数以后直接在html就可以用vo.userName这样的
        return dataJSON == null? null : dataJSON.getString(key);
    }
}
