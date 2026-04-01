package org.example.acg.service;


import jakarta.annotation.Resource;
import org.example.acg.entity.User;
import org.example.acg.mapper.UserMapper;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public User getUserByName(String name) {
        return userMapper.getUserByName(name);
    }
    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    public boolean updateUser(User user){
        int i = userMapper.updateUser(user);
        return i > 0;
    }
}
