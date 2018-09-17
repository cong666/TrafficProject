package com.cc.traffic.service;

import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cc.traffic.dao.MiaoshaUserDao;
import com.cc.traffic.domain.MiaoshaUser;
import com.cc.traffic.exception.GlobalException;
import com.cc.traffic.redis.MiaoshaUserKey;
import com.cc.traffic.redis.RedisService;
import com.cc.traffic.result.CodeMsg;
import com.cc.traffic.util.MD5Util;
import com.cc.traffic.util.UUIDUtil;
import com.cc.traffic.vo.LoginVo;

@Service
public class MiaoShaUserService {
	
	private final Logger LOGGER = Logger.getLogger(MiaoShaUserService.class.getName());

	public static final String COOKI_NAME_TOKEN = "token";
	@Autowired
	private MiaoshaUserDao userDao;
	@Autowired
	private RedisService redisService;

	public MiaoshaUser getById(long id) {
		MiaoshaUser user = redisService.get(MiaoshaUserKey.getById, id+"", MiaoshaUser.class);
		if(user==null) {
			//try to get from the db
			user = userDao.getById(id);
		} else {
			LOGGER.info("user is found from redis cache");
		}
		if(user!=null) {
			redisService.set(MiaoshaUserKey.getById, id+"", user);
		}
		return user;
	}
	
	public MiaoshaUser getByToken(HttpServletResponse response, String token) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		//change the token validation time
		if(user != null) {
			addCookie(response, token, user);
		}
		return user;
	}
	
	public boolean updatePassword(String token, long id, String formPass) {
		//get user
		MiaoshaUser user = getById(id);
		if(user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		//update bdd
		MiaoshaUser toBeUpdate = new MiaoshaUser();
		toBeUpdate.setId(id);
		toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
		userDao.update(toBeUpdate);
		//update cache
		redisService.delete(MiaoshaUserKey.getById, ""+id);
		user.setPassword(toBeUpdate.getPassword());
		redisService.set(MiaoshaUserKey.token, token, user);
		return true;
	}

	public String login(HttpServletResponse response, LoginVo loginVo) {
		if (loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		// check if phone number exist
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if (user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		// check password
		String dbPass = user.getPassword();
		String saltDB = user.getSalt();
		String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
		if (!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		// generate cookie
		String token = UUIDUtil.uuid();
		addCookie(response, token, user);
		return token;
	}

	private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
		redisService.set(MiaoshaUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
		cookie.setMaxAge(MiaoshaUserKey.token.getExpireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);

	}

}
