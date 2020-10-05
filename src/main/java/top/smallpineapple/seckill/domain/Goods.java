package top.smallpineapple.seckill.domain;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品
 *
 * @author zengzhijie
 * @since 2020/10/5 19:59
 * @version 1.0
 */
@Data
public class Goods {

    private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private String goodsDetail;
    private BigDecimal goodsPrice;
    private Integer goodsStock;

}
