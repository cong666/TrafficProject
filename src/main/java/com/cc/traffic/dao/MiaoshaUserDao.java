package com.cc.traffic.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.cc.traffic.domain.MiaoshaUser;

@Mapper
public interface MiaoshaUserDao {
	
	@Select("select * from miaoshauser where id = #{id}")
	public MiaoshaUser getById(@Param("id")long id);
}
