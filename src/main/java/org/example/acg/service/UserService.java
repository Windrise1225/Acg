package org.example.acg.service;


import jakarta.annotation.Resource;
import org.example.acg.entity.User;
import org.example.acg.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public List<User> list() {
        return userMapper.list();
    }

    public List<User> likeListByName(String name) {
        return userMapper.likeListByName(name);
    }

    public User getUserByName(String name) {
        return userMapper.getUserByName(name);
    }

    public User getUserById(Integer id) {
        return userMapper.getUserById(id);
    }

    public void insertUser(User user) {
        userMapper.insertUser(user);
    }

    public boolean updateUser(User user){
        int i = userMapper.updateUser(user);
        return i > 0;
    }

    public boolean deleteUser(Integer id){
        int i = userMapper.deleteUser(id);
        return i > 0;
    }

    public boolean replyUser(Integer id){
        int i = userMapper.replyUser(id);
        return i > 0;
    }
    public boolean save(User user){
        int i = userMapper.save(user);
        return i > 0;
    }
}
