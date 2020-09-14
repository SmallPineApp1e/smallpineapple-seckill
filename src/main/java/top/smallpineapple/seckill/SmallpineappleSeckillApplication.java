package top.smallpineapple.seckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("top.smallpineapple.seckill.dao")
public class SmallpineappleSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmallpineappleSeckillApplication.class, args);
    }

}
