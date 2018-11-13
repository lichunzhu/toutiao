package com.baine.toutiao.service;

import com.baine.toutiao.dao.LoginTicketDAO;
import com.baine.toutiao.dao.RegTicketDAO;
import com.baine.toutiao.dao.UserDAO;
import com.baine.toutiao.model.LoginTicket;
import com.baine.toutiao.model.RegTicket;
import com.baine.toutiao.model.User;
import com.baine.toutiao.util.ToutiaoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class UserService {
    @Autowired
    UserDAO userDAO;

    @Autowired
    LoginTicketDAO loginTicketDAO;

    @Autowired
    RegTicketDAO regTicketDAO;

    public Map<String, Object> registerCheck(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isEmpty(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user != null) {
            map.put("msgname", "用户名已经被注册");
            return map;
        }

        // 密码强度校验 未实现

        // 根据用户邮箱下发注册用的ticket, 发送邮件, 用户点击网页后才能完成注册
        RegTicket regTicket = new RegTicket();
        regTicket.setUsername(username);
        regTicket.setSalt(UUID.randomUUID().toString().substring(0, 5));
        regTicket.setPassword(ToutiaoUtil.MD5(password + regTicket.getSalt()));
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24);
        regTicket.setExpired(date);
        regTicket.setStatus(0);
        regTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));

        regTicketDAO.addRegTicket(regTicket);
        map.put("ticket", regTicket.getTicket());
        return map;
    }

    public Map<String, Object> register(String ticket) {
        Map<String, Object> map = new HashMap<>();
        RegTicket regTicket = regTicketDAO.selectByTicket(ticket);
        if (regTicket == null) {
            map.put("msg", "注册失败, ticket不存在");
            return map;
        }
        if (regTicket.getExpired().before(new Date()) || regTicket.getStatus() != 0) {
            map.put("msg", "注册失败, ticket过期或者已失效");
            return map;
        }
        User user = new User();
        Random random = new Random();
        user.setName(regTicket.getUsername());
        user.setSalt(regTicket.getSalt());
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
        user.setPassword(regTicket.getPassword());
        userDAO.addUser(user);
        // 将注册的ticket失效
        regTicketDAO.updateStatus(ticket, 1);
        // 下发登录的ticket
        String loginTicket = addLoginTicket(user.getId());
        map.put("ticket", loginTicket);

        return map;
    }

    public Map<String, Object> loginUser(String username, String password) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isEmpty(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);

        if (user == null) {
            map.put("msgname", "用户名不存在");
            return map;
        }

        if (!ToutiaoUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msgpwd", "密码不正确");
            return map;
        }

        map.put("userId", user.getId());
        // 下发ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    private String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }
}
