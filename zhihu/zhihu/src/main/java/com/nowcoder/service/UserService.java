package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ZhihuUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    public User selectById(int id){
        return userDAO.selectById(id);
    }
    public User selectByName(String name){
        return userDAO.selectByName(name);
    }
    public Map<String, Object> register(String username, String password){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user != null){
            map.put("msg","用户名已存在");
            return map;
        }

        user = new User(username);
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        String head = String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000));
        user.setHeadUrl(head);
        user.setPassword(ZhihuUtil.MD5(password + user.getSalt()));
        userDAO.addUser(user);

        //直接登录
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    public Map<String, Object> login(String username, String password){
        Map<String, Object> map = new HashMap<>();
        if(StringUtils.isBlank(username)){
            map.put("msg","用户名不能为空");
            return map;
        }
        if(StringUtils.isBlank(password)){
            map.put("msg","密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if(user == null){
            map.put("msg","用户不存在");
            return map;
        }

        if(!ZhihuUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msg","密码不正确");
            return map;
        }

        //直接登录
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        map.put("userId",user.getId());
        return map;
    }

    public String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        ticket.setStatus(0);
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        ticket.setExpired(date);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addLoginTicket(ticket);
        return ticket.getTicket();
    }

    public void logout(String ticket){
        loginTicketDAO.updateStatus(1,ticket);
    }
}
