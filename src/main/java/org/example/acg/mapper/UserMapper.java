package org.example.acg.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.acg.entity.User;


@Mapper
public interface UserMapper {

    User getUserByName(@Param("name") String name);

    void insertUser(User user);
}
