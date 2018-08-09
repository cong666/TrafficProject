package com.cc.traffic.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cc.traffic.domain.MiaoshaOrder;
import com.cc.traffic.domain.MiaoshaUser;
import com.cc.traffic.domain.OrderInfo;
import com.cc.traffic.rabbitmq.MQSender;
import com.cc.traffic.rabbitmq.MiaoshaMessage;
import com.cc.traffic.redis.GoodsKey;
import com.cc.traffic.redis.RedisService;
import com.cc.traffic.result.CodeMsg;
import com.cc.traffic.result.Result;
import com.cc.traffic.service.GoodService;
import com.cc.traffic.service.MiaoShaUserService;
import com.cc.traffic.service.MiaoshaService;
import com.cc.traffic.service.OrderService;
import com.cc.traffic.vo.GoodsVo;


@Controller
@RequestMapping("/miaosha")
public class MiaoshaController  implements InitializingBean {
	
	private final Logger LOGGER = Logger.getLogger(MiaoshaController.class.getName());

	@Autowired
	MiaoShaUserService userService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	GoodService goodsService;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	MiaoshaService miaoshaService;

	@Autowired
	MQSender sender;
	
	/*
	 * 5000 threads
	 * qps : 186.9
	 * */
    @RequestMapping(value="/do_miaosha", method=RequestMethod.POST)
    @ResponseBody
    public Result<Integer> list(Model model,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId) {
    	LOGGER.info("do_miaosha is called");
    	model.addAttribute("user", user);
    	if(user == null) {
    		LOGGER.info("----user is null");
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	/*
    	//直接通过redis来预减库存 redis是单线程的
    	if(redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId) < 0) {
    		return Result.error(CodeMsg.MIAO_SHA_OVER);
    	}
    	*/
        /*//直接通过redis来预减库存 redis是单线程的 和上面的代码无区别*/
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, ""+goodsId);//10
    	if(stock < 0) {
    		return Result.error(CodeMsg.MIAO_SHA_OVER);
    	}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
    	}
    	//入队
    	MiaoshaMessage mm = new MiaoshaMessage();
    	mm.setUser(user);
    	mm.setGoodsId(goodsId);
    	sender.sendMiaoshaMessage(mm);
    	return Result.success(0);//排队中
    	/*//判断库存
		GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
		int stock = goods.getStockCount();
		if (stock <= 0) {
			LOGGER.info("----stock is not enough");
			return Result.error(CodeMsg.MIAO_SHA_OVER);
		}
    	//判断是否已经秒杀到了
    	MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
    	if(order != null) {
    		LOGGER.info("----order is already exist");
    		return Result.error(CodeMsg.REPEATE_MIAOSHA);
    	}
    	//减库存 下订单 写入秒杀订单
    	OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
    	LOGGER.info("----miaosha successfully");
    	return Result.success(orderInfo);*/
    }

	public void afterPropertiesSet() throws Exception {
		//add goods stock into the redis after the controller is initialized
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		if(goodsList == null) {
			return;
		}
		for(GoodsVo goods : goodsList) {
			redisService.set(GoodsKey.getMiaoshaGoodsStock, ""+goods.getId(), goods.getStockCount());
		}
	}
	@RequestMapping(value="/result", method=RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model,MiaoshaUser user,
    		@RequestParam("goodsId")long goodsId) {
    	model.addAttribute("user", user);
    	if(user == null) {
    		return Result.error(CodeMsg.SESSION_ERROR);
    	}
    	long result  =miaoshaService.getMiaoshaResult(user.getId(), goodsId);
    	return Result.success(result);
    }
}
