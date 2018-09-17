package com.cc.traffic.service;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cc.traffic.domain.MiaoshaOrder;
import com.cc.traffic.domain.MiaoshaUser;
import com.cc.traffic.domain.OrderInfo;
import com.cc.traffic.redis.MiaoshaKey;
import com.cc.traffic.redis.RedisService;
import com.cc.traffic.vo.GoodsVo;

@Service
public class MiaoshaService {
	
	private final Logger LOGGER = Logger.getLogger(MiaoshaService.class.getName());
	
	@Autowired
	GoodService goodsService;
	
	@Autowired
	OrderService orderService;
	@Autowired
	RedisService redisService;

	@Transactional
	public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {
		//减库存 下订单 写入秒杀订单
		int result = goodsService.reduceStock(goods);
		LOGGER.info("----miaosha result is:"+result+",success?"+(result==1));
		//如果更新失败那么就抛出RuntimeException来自动回滚事务 这样就可以防止stock减库存不成功的时候不创建订单
		//减库存query update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count>0
		//因为stock减库存成不成功是数据库端修改的时候才知道 我们加了 stock_count>0的时候才减库存 所以保证了stock不会被减成负数
		//注意的是如果把库存stock存储到redis中那么此时不必要判断减库存是否成功失败 因为redis是单线程 所以在MQReceiver中已经成功判定了是否能减库存
		if(result>0) {
			//order_info maiosha_order
			return orderService.createOrder(user, goods);
		} else {
			setGoodsOver(goods.getId());
			return null;
			//throw new RuntimeException();
		}
		
	}
	
	private void setGoodsOver(Long goodsId) {
		redisService.set(MiaoshaKey.isGoodsOver, ""+goodsId, true);
	}
	
	private boolean getGoodsOver(long goodsId) {
		return redisService.exists(MiaoshaKey.isGoodsOver, ""+goodsId);
	}
	
	public long getMiaoshaResult(Long userId, long goodsId) {
		MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(userId, goodsId);
		if(order != null) {//秒杀成功
			return order.getOrderId();
		}else {
			boolean isOver = getGoodsOver(goodsId);
			if(isOver) {
				return -1;
			}else {
				return 0;
			}
		}
	}
	
}
