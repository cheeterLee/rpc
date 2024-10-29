package com.cheeterlee.rpc.api.service;

import com.cheeterlee.rpc.api.pojo.User;
import java.util.List;

public interface UserService {
    User queryUser();
    List<User> getAllUsers();
}