package com.baine.toutiao.service;

import com.baine.toutiao.dao.UserDAO;
import com.baine.toutiao.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserDAO userDAO;

    @Autowired
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User getUser(int id) {
        return userDAO.selectById(id);
    }
}
