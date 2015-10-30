package com.wofu.fenxiao.service.impl;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.wofu.fenxiao.domain.DistributeGoods;
import com.wofu.fenxiao.mapping.DistributeGoodsMapper;
import com.wofu.fenxiao.pulgins.PageView;
import com.wofu.fenxiao.service.DistributeGoodsService;
@Service("DistributeGoodsService")//springmvc注解，这里会自动生成这个类的对象，由spring管理


public class DistributeGoodsServiceImpl  implements DistributeGoodsService{
	@Autowired
	private DistributeGoodsMapper distributeGoodsMapper;

	@Override
	public void delete(HashMap<String, Object> map) throws Exception {
		distributeGoodsMapper.delete(map);
		
	}

	@Override
	public List<HashMap> qryBrand(HashMap<String, Object> map) throws Exception {
		return distributeGoodsMapper.qryBrand(map);
	}

	@Override
	public List<HashMap> qryProductLine(HashMap<String, Object> map) throws Exception {
		return distributeGoodsMapper.qryProductLine(map);
	}

	@Override
	public List<HashMap> queryDistributeGoods(HashMap<String, Object> map) throws Exception {
		return distributeGoodsMapper.queryDistributeGoods(map);
	}

	@Override
	public List<HashMap> querySkuInventory(HashMap<String, Object> map) throws Exception {
		return distributeGoodsMapper.querySkuInventory(map);
	}

	@Override
	public PageView query(PageView pageView, DistributeGoods t)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(DistributeGoods t) throws Exception {
		distributeGoodsMapper.add(t);
		
	}

	@Override
	public void delete(int id) throws Exception {
		distributeGoodsMapper.delete(id);
		
	}

	@Override
	public DistributeGoods getById(int id) throws Exception {		
		return distributeGoodsMapper.getById(id);
	}

	@Override
	public List<DistributeGoods> queryAll(DistributeGoods t) throws Exception {
		// TODO Auto-generated method stub
		return distributeGoodsMapper.queryAll(t);
	}

	@Override
	public void update(DistributeGoods t) throws Exception {
		distributeGoodsMapper.update(t);
		
	}
	

}
