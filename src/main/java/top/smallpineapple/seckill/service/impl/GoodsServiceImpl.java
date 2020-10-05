package top.smallpineapple.seckill.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.smallpineapple.seckill.dao.GoodsDao;
import top.smallpineapple.seckill.service.GoodsService;
import top.smallpineapple.seckill.vo.GoodsVo;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Override
    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }
}
