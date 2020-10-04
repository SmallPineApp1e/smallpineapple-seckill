package top.smallpineapple.seckill.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.smallpineapple.seckill.domain.MiaoshaUser;

/**
 * 秒杀用户持久层
 *
 * @author zengzhijie
 * @since 2020/10/4 15:05
 * @version 1.0
 */
@Mapper
public interface MiaoshaUserDao {

    @Select("SELECT * FROM miaosha_user WHERE id = #{id}")
    MiaoshaUser getById(@Param("id") long id);

}
