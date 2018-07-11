package com.cc.traffic.service;

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

	public static final String COOKI_NAME_TOKEN = "token";
	@Autowired
	private MiaoshaUserDao userDao;
	@Autowired
	private RedisService redisService;

	public MiaoshaUser getById(long id) {
		return userDao.getById(id);
	}
	
	public MiaoshaUser getByToken(HttpServletResponse response, String token) {
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		MiaoshaUser user = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
		//延长有效期
		if(user != null) {
			addCookie(response, token, user);
		}
		return user;
	}

	public boolean login(HttpServletResponse response, LoginVo loginVo) {
		if (loginVo == null) {
			throw new GlobalException(CodeMsg.SERVER_ERROR);
		}
		String mobile = loginVo.getMobile();
		String formPass = loginVo.getPassword();
		// 判断手机号是否存在
		MiaoshaUser user = getById(Long.parseLong(mobile));
		if (user == null) {
			throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
		}
		// 验证密码
		String dbPass = user.getPassword();
		String saltDB = user.getSalt();
		String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
		if (!calcPass.equals(dbPass)) {
			throw new GlobalException(CodeMsg.PASSWORD_ERROR);
		}
		// 生成cookie
		String token = UUIDUtil.uuid();
		addCookie(response, token, user);
		return true;
	}

	private void addCookie(HttpServletResponse response, String token, MiaoshaUser user) {
		redisService.set(MiaoshaUserKey.token, token, user);
		Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
		cookie.setMaxAge(MiaoshaUserKey.token.getExpireSeconds());
		cookie.setPath("/");
		response.addCookie(cookie);

	}

}
