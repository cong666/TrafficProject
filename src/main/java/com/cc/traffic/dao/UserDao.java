package com.cc.traffic.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.cc.traffic.domain.User;

@Mapper
public interface UserDao {
	@Select("select * from user where id = #{id}")
	public User getById(@Param("id") int id);
	
	@Insert("insert into User(id, name) values(#{id},#{name})")
	public void insert(User user);
}
