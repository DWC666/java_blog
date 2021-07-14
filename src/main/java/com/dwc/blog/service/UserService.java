package com.dwc.blog.service;

import com.dwc.blog.entity.User;

public interface UserService {
    User checkUser(String username, String password);
}
