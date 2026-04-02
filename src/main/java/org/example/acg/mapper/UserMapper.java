package org.example.acg.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.acg.entity.User;

import java.util.List;


@Mapper
public interface UserMapper {

    List<User> list();

    List<User> likeListByName(@Param("name") String name);

    User getUserByName(@Param("name") String name);

    User getUserById(@Param("id") Integer id);

    void insertUser(User user);

    int updateUser(User user);

    int deleteUser(@Param("id") Integer id);

    int replyUser(@Param("id") Integer id);
}
