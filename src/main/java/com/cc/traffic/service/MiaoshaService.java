package com.cc.traffic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cc.traffic.domain.MiaoshaUser;
import com.cc.traffic.domain.OrderInfo;
import com.cc.traffic.vo.GoodsVo;

@Service
public class MiaoshaService {
	
	@Autowired
	GoodService goodsService;
	
	@Autowired
	OrderService orderService;

	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		//减库存 下订单 写入秒杀订单
		goodsService.reduceStock(goods);
		//order_info maiosha_order
		return orderService.createOrder(user, goods);
	}
	
}
