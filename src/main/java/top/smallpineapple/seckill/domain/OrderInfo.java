package top.smallpineapple.seckill.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品信息
 *
 * @author zengzhijie
 * @since 2020/10/5 20:02
 * @version 1.0
 */
@Data
public class OrderInfo {

    private Long id;
    private Long userId;
    private String userName;
    private Long goodsId;
    private Long deliveryAddrId;
    private String goodsName;
    private Integer goodsCount;
    private BigDecimal goodsPrice;
    private Integer orderChannel;
    private Integer status;
    private Date createDate;
    private Date payDate;

}
