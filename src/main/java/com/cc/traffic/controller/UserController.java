package com.cc.traffic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cc.traffic.domain.MiaoshaUser;
import com.cc.traffic.result.Result;

@Controller
public class UserController {
	
	@RequestMapping("/info")
	@ResponseBody
    public Result<MiaoshaUser> info(Model model,MiaoshaUser user) {
		System.out.println("info is returned:"+user);
        return Result.success(user);
    }
}
