package com.cc.traffic.controller;

import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import com.cc.traffic.domain.MiaoshaUser;
import com.cc.traffic.redis.GoodsKey;
import com.cc.traffic.redis.RedisService;
import com.cc.traffic.result.Result;
import com.cc.traffic.service.GoodService;
import com.cc.traffic.service.MiaoShaUserService;
import com.cc.traffic.vo.GoodsDetailVo;
import com.cc.traffic.vo.GoodsVo;


@Controller
@RequestMapping("/goods")
public class GoodsController {
	private final Logger LOGGER = Logger.getLogger(GoodsController.class.getName());

	@Autowired
	MiaoShaUserService userService;
	@Autowired
	GoodService goodService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;
	
	@Autowired
	ApplicationContext applicationContext;
	
    @RequestMapping(value="/to_list" , produces="text/html")
    @ResponseBody
    public String list(HttpServletRequest request, HttpServletResponse response,Model model,MiaoshaUser user) {
    	model.addAttribute("user", user);
    	//取缓存
    	String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
    	if(!StringUtils.isEmpty(html)) {
    		LOGGER.info("the page is found from redis cache!");
    		return html;
    	}
    	List<GoodsVo> goodsList = goodService.listGoodsVo();
    	model.addAttribute("goodsList", goodsList);
       //return "goods_list";
    	WebContext ctx = new WebContext(request,response,
    			request.getServletContext(),request.getLocale(), model.asMap());
    	//手动渲染
    	html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
    	if(!StringUtils.isEmpty(html)) {
    		redisService.set(GoodsKey.getGoodsList, "", html);
    	}
    	return html;
    }
    @RequestMapping(value="/to_detail2/{goodsId}",produces="text/html")
    @ResponseBody
    public String detail2(HttpServletRequest request, HttpServletResponse response, Model model,MiaoshaUser user,
    		@PathVariable("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	
    	//取缓存
    	String html = redisService.get(GoodsKey.getGoodsDetail, ""+goodsId, String.class);
    	if(!StringUtils.isEmpty(html)) {
    		return html;
    	}
    	//手动渲染
    	GoodsVo goods = goodService.getGoodsVoByGoodsId(goodsId);
    	model.addAttribute("goods", goods);
    	
    	long startAt = goods.getStartDate().getTime();
    	long endAt = goods.getEndDate().getTime();
    	long now = System.currentTimeMillis();
    	
    	int miaoshaStatus = 0;
    	int remainSeconds = 0;
    	if(now < startAt ) {//秒杀还没开始，倒计时
    		miaoshaStatus = 0;
    		remainSeconds = (int)((startAt - now )/1000);
    	}else  if(now > endAt){//秒杀已经结束
    		miaoshaStatus = 2;
    		remainSeconds = -1;
    	}else {//秒杀进行中
    		miaoshaStatus = 1;
    		remainSeconds = 0;
    	}
    	model.addAttribute("miaoshaStatus", miaoshaStatus);
    	model.addAttribute("remainSeconds", remainSeconds);
//        return "goods_detail";
    	
    	WebContext ctx = new WebContext(request,response,
    			request.getServletContext(),request.getLocale(), model.asMap());
    	html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", ctx);
    	if(!StringUtils.isEmpty(html)) {
    		redisService.set(GoodsKey.getGoodsDetail, ""+goodsId, html);
    	}
    	return html;
    }
    @RequestMapping("/to_detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> detail(Model model,MiaoshaUser user,
    		@PathVariable("goodsId")long goodsId) {
    	
    	GoodsDetailVo vo = new GoodsDetailVo();
    	model.addAttribute("user", user);
    	vo.setUser(user);
    	GoodsVo goods = goodService.getGoodsVoByGoodsId(goodsId);
    	model.addAttribute("goods", goods);
    	vo.setGoods(goods);
    	
    	long startAt = goods.getStartDate().getTime();
    	long endAt = goods.getEndDate().getTime();
    	long now = System.currentTimeMillis();
    	
    	int miaoshaStatus = 0;
    	int remainSeconds = 0;
    	if(now < startAt ) {//秒杀还没开始，倒计时
    		miaoshaStatus = 0;
    		remainSeconds = (int)((startAt - now )/1000);
    	}else  if(now > endAt){//秒杀已经结束
    		miaoshaStatus = 2;
    		remainSeconds = -1;
    	}else {//秒杀进行中
    		miaoshaStatus = 1;
    		remainSeconds = 0;
    	}
    	vo.setMiaoshaStatus(miaoshaStatus);
    	vo.setRemainSeconds(remainSeconds);
        return Result.success(vo);
    }
}
