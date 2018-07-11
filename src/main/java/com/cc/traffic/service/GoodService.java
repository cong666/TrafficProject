package com.cc.traffic.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cc.traffic.dao.GoodsDao;
import com.cc.traffic.domain.MiaoshaGoods;
import com.cc.traffic.vo.GoodsVo;

@Service
public class GoodService {
	@Autowired
	private GoodsDao goodDao;
	
	public List<GoodsVo> listGoodsVo(){
		return goodDao.listGoodsVo();
	}
	
	public GoodsVo getGoodsVoByGoodsId(long goodsId) {
		return goodDao.getGoodsVoByGoodsId(goodsId);
	}
	
	public int reduceStock(GoodsVo good) {
		MiaoshaGoods mg = new MiaoshaGoods();
		mg.setGoodsId(good.getId());
		return goodDao.reduceStock(mg);
	}
}
