package top.smallpineapple.seckill.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品
 *
 * @author zengzhijie
 * @since 2020/10/5 19:59
 * @version 1.0
 */
@Data
public class MiaoshaGoods {

    private Long id;
    private Long goodsId;
    private BigDecimal miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

}
