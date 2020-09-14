package top.smallpineapple.seckill.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.smallpineapple.seckill.domain.User;

/**
 * User 持久层接口
 *
 * @author zengzhijie
 * @since 2020/9/14 13:35
 * @version 1.0.0
 */
@Mapper
public interface UserDao {

    @Select("SELECT id, name FROM user WHERE id = #{id}")
    public User getById(@Param("id") Integer id);

}
