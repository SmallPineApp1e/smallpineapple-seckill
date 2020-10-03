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

1、替换DBCP和C3P0。Druid提供了一个高效、功能强大、可扩展性好的数据库连接池。

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
1. 启动类上需要加上`@MapperScan`注解，否则无法自动扫描到 Mapper 类
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