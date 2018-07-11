package com.cc.traffic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cc.traffic.result.CodeMsg;
import com.cc.traffic.result.Result;

@Controller
public class HelloController {
	@RequestMapping("/hello")
	@ResponseBody
    public Result<String> hello() {
        return Result.success("hello cc");
    }
	
	@RequestMapping("/helloError")
	@ResponseBody
    public Result<String> helloError() {
        return Result.error(CodeMsg.SERVER_ERROR);
    }
	
	/*
	 * 此处不能使用ResponseBody 因为ResponseBody返回的内容就是return的内容 配置的 thymeleaf 的视图解析器就不会起作用
	 * */
	@RequestMapping("/thymeleaf")
    public String hellothymeleaf(Model model) {
		model.addAttribute("name", "cc");
		return "hello";
    }
}
