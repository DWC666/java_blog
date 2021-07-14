package com.dwc.blog.service.serviceImpl;

import com.dwc.blog.dao.UserRepository;
import com.dwc.blog.entity.User;
import com.dwc.blog.service.UserService;
import com.dwc.blog.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;


    @Override
    public User checkUser(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, MD5Util.encrypt(password));
        return user;
    }
}
