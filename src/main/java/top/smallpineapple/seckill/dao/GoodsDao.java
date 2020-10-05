package top.smallpineapple.seckill.dao;

import org.apache.ibatis.annotations.Select;
import top.smallpineapple.seckill.vo.GoodsVo;

import java.util.List;

/**
 * 商品持久层
 *
 * @author zengzhijie
 * @since 2020/10/5 20:03
 * @version 1.0
 */
public interface GoodsDao {

    @Select("SELECT g.*, mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date FROM miaosha_goods mg LEFT OUTER JOIN goods g ON mg.goods_id = g.id")
    List<GoodsVo> listGoodsVo();

}
