# 从零开始搭建一个秒杀项目

## 包模块详解

## 引入 Thymeleaf 模板引擎

```yaml
spring: 
  thymeleaf:
    cache: false # 不允许 thymeleaf 进行缓存
    mode: HTML5  # 以 HTML5 模式匹配模板引擎
```

## 引入 MyBatis

使用 SpringBoot + MyBatis 结合可以实现灵活的 SQL 编写，而 Hibernate 与 SpringData JPA 则不能支持很好地动态 SQL，不利于建立索引和自己写 SQL

```yaml
# mybatis
mybatis:
  type-aliases-package: com.smallpineapple.seckill.domain # 配置类型别名的包
  configuration:
    map-underscore-to-camel-case: true # 将domain包以下划线的表名转换为驼峰命名
    default-fetch-size: 100 #
    default-statement-timeout: 3000 # 每条语句最大等待时间
  mapper-locations: classpath:com/smallpineapple/seckill/dao/*.xml # 扫描 mapper 文件, 用于 mapper 接口和配置文件的映射
```

### 引入 Druid 数据源

1、替换 DBCP 和 C3P0。Druid提供了一个高效、功能强大、可扩展性好的数据库连接池。

2、可以监控数据库访问性能，Druid内置提供了一个功能强大的StatFilter插件，能够详细统计SQL的执行性能，这对于线上分析数据库访问性能有帮助。

3、数据库密码加密。直接把数据库密码写在配置文件中，这是不好的行为，容易导致安全问题。DruidDruiver和DruidDataSource都支持PasswordCallback。

4、SQL执行日志，Druid提供了不同的LogFilter，能够支持Common-Logging、Log4j和JdkLog，你可以按需要选择相应的LogFilter，监控你应用的数据库访问情况。

5、扩展JDBC，如果你要对JDBC层有编程的需求，可以通过Druid提供的Filter机制，很方便编写JDBC层的扩展插件。

```yaml
spring: 
    datasource:
        druid: # 连接池通用配置
          max-active: 100
          initial-size: 10
          # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
          time-between-eviction-runs-millis: 60000
          # 配置一个连接在池中最小生存的时间，单位是毫秒
          min-evictable-idle-time-millis: 300000
          validation-query: select 'x'
          test-while-idle: true
          test-on-borrow: false
          test-on-return: false
          # 打开PSCache，并且指定每个连接上PSCache的大小
          pool-prepared-statements: true
          max-open-prepared-statements: 20
          # 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
          filters: stat, wall, log4j
          # 通过connectProperties属性来打开mergeSql功能；慢SQL记录
          connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
```

### 配置 MySQL 相关依赖及配置

```yaml
spring:
  datasource: # 数据源
    username: root
    password: root
    url: jdbc:mysql://localhost:3306/miaosha?useUnicode=true&characterEncoding=utf8&&useSSL=false&serverTimezone=GMT-8
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
```

需要注意几个地方：
1. 启动类上需要加上`@MapperScan`注解，否则**无法自动扫描到 Mapper 类**
2. Druid 监控日志如果包含 log4j 需要手动引入 log4j 的依赖，否则启动会报错
3. MySQL 8.0 需要在路径后面手动配上时区 serverTimezone=GMT-8，因为 8.0 开始 MySQL 不再是默认一个时区

## 引入 Redis

```xml
<!--jedis-->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.3.0</version>
</dependency>
<!--fastjson-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.70</version>
</dependency>
```

我们使用自己封装的 RedisTemplate，Spring Boot 默认的 RedisTemplate 采用 `<Object, Object>`  泛型指定 Key 和 Value，但在实际开发中，我们通常都是用 `<String, Object>` 类型，所以我们在使用时就要进行大量的强制转换 Object - String。

其次，我们在使用 Redis 存储对象时，需要进行序列化，在原生的 RedisTemplate 中，只制定了默认的 JDK 序列化方式，这样有可能导致编码不一致，在服务器上看到的 Key 就是一堆乱码，虽然在获取和设置时是正确的。

![image-20201003105511922](http://cdn.smallpineapple.top/20201003105538.png)

可以看到下面的源码中，默认都采用了 JDK  的序列化方式

![](http://cdn.smallpineapple.top/20201003105645.png)

所以，为了开发方便，我们自己封装一个 RedisTemplate，**这是一套固定的模板，开发时直接使用即可！**

```java
@Configuration
public class RedisConfig {

  @Bean
  @SuppressWarnings("all")
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

      RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
      template.setConnectionFactory(factory);
      Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
      ObjectMapper om = new ObjectMapper();
      om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
      om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
      jackson2JsonRedisSerializer.setObjectMapper(om);
      StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

      // key采用String的序列化方式
      template.setKeySerializer(stringRedisSerializer);
      // hash的key也采用String的序列化方式
      template.setHashKeySerializer(stringRedisSerializer);
      // value序列化方式采用jackson
      template.setValueSerializer(jackson2JsonRedisSerializer);
      // hash的value序列化方式采用jackson
      template.setHashValueSerializer(jackson2JsonRedisSerializer);
      template.afterPropertiesSet();

      return template;
  }
  
}
```

其次，我们还需要封装一个**工具类** RedisUtil，否则每个 Service 涉及到 Redis 操作时都要引入该模板，然后调用方法，就会显得**非常混乱**。篇幅原因，这里就不贴出来了，在源码中 RedisUtil 可以找到，里面包含了五种基本数据类型的几乎所有常用的 API！

### 通用缓存 Key 封装

如果没有规定 Key 的生成策略，在团队协作开发时，风险会非常大，可能会存在 Key 冲突导致数据被覆盖！所以我们必须**规定 Key 的生成策略，保证 Key 不会冲突**。

> 例如：用户缓存（UserPrefix:User1）、公司缓存（CompanyPrefix:Company1）···

通常我们都会采用接口 + 抽象类 + 实现类的方式定义策略，也称为**模板方法模式**

![](http://cdn.smallpineapple.top/20201003110734.png)

**优点**：

1. 接口定义一些契约，通常代表该接口下的所有类都具有什么样的功能
2. 抽象类实现子类共有的功能，把具体的实现交给子类的实现

在 JDK 的**集合容器**中，大量使用到了该架构形式：

1. Map 集合：Map 接口 - AbstractMap 抽象类 - HashMap 子类···
2. Set 集合：Set 接口 - AbstractSet 抽象类 - HashSet 子类···
3. List 集合：List 接口 - AbstractList 抽象类 - ArrayList 子类···

## 实现登录功能

### 数据库设计

```sql
CREATE TABLE `miaosha_user`  (
  `id` bigint(20) NOT NULL COMMENT '用户ID，手机号码',
  `nickname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'MD5(MD5(pass明文+固定salt) + salt)',
  `salt` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `head` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像，云存储的ID',
  `register_date` datetime(0) NULL DEFAULT NULL COMMENT '注册时间',
  `last_login_date` datetime(0) NULL DEFAULT NULL COMMENT '上蔟登录时间',
  `login_count` int(11) NULL DEFAULT 0 COMMENT '登录次数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;
```

### 明文密码两次 MD5 处理防止被盗

1. 用户端：PASS0 = MD5( 明文密码 + 固定 Salt )
2. 服务端：PASS1 = MD5 ( PASS0 + 随机 Salt )

由于 HTTP 明文传输的不安全性，在用户提交密码到服务器时，有可能会被其他人截获该密码，冒充用户甚至篡改信息，所以：

**用户端和服务器共同协商一个盐值（Salt），该盐值只有双方知道，第三方无法知道，在用户提交密码时，用户端会对该密码进行加盐，之后通过 MD5 散列后传递给服务器，尽管 MD5 可以碰撞出来，但由于不知道盐值，所以第三者也无法解密出用户的原始密码**。

> 但如果第三者获取到客户端的盐值并发现是如何加盐的，其实也是能够破解的，所以没有绝对安全的办法，除非使用 HTTPS

而服务器拿到加盐且散列后的密码串时，会对散列后的密码值进行**再一次的加盐（该盐值是服务器随机产生的）**，加了盐后，再做一次 MD5 散列，得到散列后的最终密码串，然后将**随机 Salt 和 最终的密码串**保存在数据库中，整个密码串看起来就是下面这个样子。

> MD5 ( MD5 (用户输入的密码 + 协商盐值) + 随机盐值 )

第二次 加盐 和 MD5 的目的：如果数据库被盗，那么第三者也无法通过彩虹表碰撞 MD5 的方式破解出密码，因为第三者不知道盐值是如何加入到原密码串的；即使破解出来，也只能得到 **MD5 (用户输入的密码 + 协商盐值)**，再碰撞一次，且不谈安全性，光是**时间成本**就让他们吃不消了。  

### JSR 303 参数校验 + 全局异常统一处理

### 分布式 Session（重要！）







## 常见错误

### 导入 JS 包却找不到

视频中引入的方式如下图所示，但是这种方式引入，在浏览器上会找不到资源，即使资源存在。

![](http://cdn.smallpineapple.top/20201004145246.png)

![](http://cdn.smallpineapple.top/20201004145331.png)

我们只需要把`/static`去掉即可。

## 常见知识点

### 防 SQL 注入

MyBatis 中编写 SQL 语句时 #{} 和 ${} 都可以放入值，但是 \${} 有可能会导致 SQL 注入风险，原因在于它直接将 SQL 语句和参数值拼接起来，如果使用类似于`OR 1 = 1 -` 类型的参数，就会导致所有参数都成立！而 #{} 会将 SQL 进行预编译，生成一条 SQL 语句，并在参数位置加上双引号，如果想进行 SQL 注入，那么 SQL 语句会报错，不会成功编译 SQL 语句。