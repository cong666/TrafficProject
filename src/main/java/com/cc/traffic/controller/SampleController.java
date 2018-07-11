package com.cc.traffic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cc.traffic.domain.User;
import com.cc.traffic.redis.RedisService;
import com.cc.traffic.redis.UserKey;
import com.cc.traffic.result.Result;
import com.cc.traffic.service.UserService;

@Controller
@RequestMapping("/demo")
public class SampleController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private RedisService redisService;

	@RequestMapping("/db/get")
	@ResponseBody
	public Result<User> dbGet() {
		User user = userService.getById(1);
		return Result.success(user);
	}
	
	@RequestMapping("db/tx")
	@ResponseBody
	public Result<Boolean> doTx() {
		userService.transactionTest();
		return Result.success(true);
	}
	@RequestMapping("redis/set")
	@ResponseBody
	public Result<Boolean> redisSet() {
		User user = new User();
		user.setId(4);
		user.setName("4444");
		redisService.set(UserKey.getById, ""+4, user);
		return Result.success(true);
	}
	@RequestMapping("redis/get")
	@ResponseBody
	public Result<User> redisGet() {
		User user = redisService.get(UserKey.getById, ""+4, User.class);
		return Result.success(user);
	}
}
