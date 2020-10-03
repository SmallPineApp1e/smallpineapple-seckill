package top.smallpineapple.seckill;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.smallpineapple.seckill.common.redisKey.UserKey;
import top.smallpineapple.seckill.domain.User;
import top.smallpineapple.seckill.service.UserService;
import top.smallpineapple.seckill.util.RedisUtil;

@SpringBootTest
@Slf4j
class SmallpineappleSeckillApplicationTests {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private UserService userService;

    @Test
    public void testGetUserById() {
        User user = userService.getById(1);
        log.info("user:{}", user.toString());
    }

    @Test
    public void testSetKey() {

        User user = new User();
        user.setId(10);
        user.setName("张三");

        boolean b = redisUtil.set(UserKey.USER_ID_KEY.getPrefix() + "" + user.getId(), user);
        log.info("b:{}", b);
    }

    @Test
    public void testGetKey() {
        String realKey = UserKey.USER_ID_KEY.getPrefix() + "" + 10;
        log.info(realKey + ":{}", redisUtil.get(realKey));
    }

}
