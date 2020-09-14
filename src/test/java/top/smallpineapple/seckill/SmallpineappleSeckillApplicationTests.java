package top.smallpineapple.seckill;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.smallpineapple.seckill.domain.User;
import top.smallpineapple.seckill.service.UserService;

@SpringBootTest
@Slf4j
class SmallpineappleSeckillApplicationTests {



    @Autowired
    private UserService userService;

    @Test
    public void testGetUserById() {
        User user = userService.getById(1);
        log.info("user:{}", user.toString());
    }

}
