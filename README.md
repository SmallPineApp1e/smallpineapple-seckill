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

引入依赖

```xml
<!-- JSR 303 参数校验-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

常用注解解析：

| 注解                   | 解析                                      |
| ---------------------- | ----------------------------------------- |
| @NotNull               | 参数不能为 NULL，如果是空字符串“”也会通过 |
| @Length(min=)          | 最小长度是 min···                         |
| @Email                 | 匹配邮箱的正则表达式                      |
| @NotBlank              | 不能为 NULL，且不能为空字符串“”           |
| @Range(min = , max = ) | 大小范围是[min, max]                      |

我们还需要自己实现一个手机号码的**注解校验器**。`@IsMobile` + `IsMobileValidator`实现自定义校验注解。

我们发现参数传递到服务器端后有异常时，可以定义**全局异常统一处理**，这样就不需要在每个方法内部自己处理异常，让代码更加简洁。

使用 `@ControllerAdvice` 定义全局异常处理类，`@ExceptionHanlder` 标注在方法上，表明该方法捕获哪一类异常，这样在业务逻辑中抛出的异常经过全局异常处理类进行处理，就不需要在业务代码中单独处理了！

而我们在项目场景开发时，遇到特殊的业务异常，通常是抛出自己的异常，而不是作为返回值返回给前端的，所以，在发生各种各样的业务异常时，我们可以为这些异常统一归为一个自定义异常`GlobleException`，然后交给全局异常处理类进行捕获并处理，由异常处理类返回对应的错误信息给前端，这样就不需要在每个业务异常里面单独处理，只需要抛出异常即可！

```java
/**
 * 全局统一异常处理
 *
 * @author zengzhijie
 * @since 2020/10/4 15:46
 * @version 1.0
 */
@RestControllerAdvice
public class GlobleExceptionHandler {

    @ExceptionHandler(value = BindException.class)
    public Result<String> bindExceptionHandler(HttpServletRequest request, BindException e) {

        ObjectError error = e.getAllErrors().get(0);
        String msg = error.getDefaultMessage();
        return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));

    }

    @ExceptionHandler(value = GlobleException.class)
    public Result<String> globleExceptionHandler(HttpServletRequest request, GlobleException e) {
        return Result.error(e.getCm());
    }

}
/**
 * 业务产生的自定义异常
 *
 * @author zengzhijie
 * @since 2020/10/4 16:04
 * @version 1.0
 */
@Getter
public class GlobleException extends RuntimeException {

    private CodeMsg cm;

    public GlobleException(CodeMsg cm) {

        super(cm.toString());
        this.cm = cm;

    }
}
```

### 分布式 Session（重要！）

真正的项目在上线时，肯定不会只用单机部署的形式，因为单机应用的抗压能力实在太弱了，我们通常会部署应用到多台服务器上，根据不同的情况将请求分发到多个服务器上，减轻一台服务器的负担，此时就会出现问题了：

用户之前登录请求分发到服务器A，A 记录了用户的 Session，但是服务器 B 不知道用户登录了，如果用户在秒杀时请求的服务器 B，那就会出现**未登录**的谬论，此时，我们需要将 **Session 在各个服务器间共享**，于是出现了分布式 Session。

解决方案有多种：

- Session 自动同步（应用不多，性能瓶颈）
- 一台存储 Session 的服务器，所有请求查询 Session 时都来该服务器查询
- JWT 存入 Redis 种，类似于方案 2

**解决思路：**

在用户登录后，生成唯一的 UUID，作为 Key 存放在 Redis 中，Value 存放用户数据，同时设置过期时间，并把生成的随机串写回到 Cookie 当中，这样用户无论在哪台服务器上请求访问，都会带上 Cookie 中的 token 值，服务器去 Redis 中查找，就避免了多台服务器之间 Session 不同步的问题！

**优化：**

如果用户在这段时间内一直在访问，那么过期时间应该也延后一些，避免用户一直在访问，突然被告知未登录的乱象！

## 秒杀功能（重点！）

### 数据库设计

![](http://cdn.smallpineapple.top/20201005195005.png)



### 功能实现

> 基本的页面和查询显示功能就不在这里展出了，大家跟着视频做也能够实现，这篇笔记主要针对该项目最重要的技术点进行阐述和讲解。

1、只单纯地使用数据 SQL 语句 + 事务进行秒杀

```sql
UPDATE miaosha_goods SET stock_count = stock_count - 1 WHERE id = #{goodsId}
```

出现的问题：

- 超卖：可能出现商品超卖，导致
- 并发量低：

## 页面优化技术

通过各种手段的缓存，减少对数据库的访问，提高并发量

页面静态化处理，前后端分离

### 页面缓存

 取缓存，渲染模板，返回客户端

客户端在访问页面时，不希望每次都需要客户端去渲染页面模板，这样会导致并发量下降，如果一个用户下载 1KB 的数据，那么10万个用户需要下载 1KB 的数据，就会非常慢。

我们可以将第一次渲染好的页面以 HTML 形式保存在 Redis 中，每次客户端请求访问页面时，都去判断 Redis 是否存在该页面，如果存在则直接返回所有的 HTML，不需要重新渲染。

页面存在 Redis 中的时间不能过长，因为商品信息有可能会随时更新，通常采用 1 分钟的过期时间保存在 Redis。

```java
@GetMapping("/to_list")
@ResponseBody
public String toGoodList(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
    // 先去缓存中查找已经之前渲染好的 HTML 页面
    String html = (String) redisUtil.get(GoodsKey.getGoodsList.getPrefix());
    if (!StringUtils.isEmpty(html)) {
        return html;
    }
    // 手动渲染
    List<GoodsVo> goodsVos = goodsService.listGoodsVo();
    model.addAttribute("goodsList", goodsVos);
    model.addAttribute("user", user);
    WebContext context = new WebContext(request, response, request.getServletContext(),
                                        request.getLocale(), model.asMap());
    html = thymeleafViewResolver.getTemplateEngine().process("goods_list", context);
    if (!StringUtils.isEmpty(html)) {
        // 将初次渲染好的 HTML 页面保存到 Redis 中
        redisUtil.set(GoodsKey.getGoodsList.getPrefix(), html, GoodsKey.getGoodsList.expireSeconds());
    }
    return html;
}
```

### 对象缓存



### 页面静态化



### 前后端分离



### 

## 常见错误

### 导入 JS 包却找不到

视频中引入的方式如下图所示，但是这种方式引入，在浏览器上会找不到资源，即使资源存在。

![](http://cdn.smallpineapple.top/20201004145246.png)

![](http://cdn.smallpineapple.top/20201004145331.png)

我们只需要把`/static`去掉即可。

### 使用 JSR 303 校验注解后不生效

需要在 Controller 层接收参数的参数前添加注解 `@Valid`，否则注解不会生效。

## 常见知识点

### 防 SQL 注入

MyBatis 中编写 SQL 语句时 #{} 和 ${} 都可以放入值，但是 \${} 有可能会导致 SQL 注入风险，原因在于它直接将 SQL 语句和参数值拼接起来，如果使用类似于`OR 1 = 1 -` 类型的参数，就会导致所有参数都成立！而 #{} 会将 SQL 进行预编译，生成一条 SQL 语句，并在参数位置加上双引号，如果想进行 SQL 注入，那么 SQL 语句会报错，不会成功编译 SQL 语句。

### 自定义参数解析

在分布式 Session 中，我们一开始需要在参数中传入 `cookieToken` 和 `paramToken` 两个参数值，其它需要登录校验的方法也需要传入这两个参数值，导致代码非常**冗余**。

我们希望在传入控制层的参数中直接得到一个 MiaoshaUser 对象，而不希望自己再去 Redis 中查询，这就是一种优化手段。

我们自己实现一个**参数解析器**

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UserArgumentResolverHandler userArgumentResolverHandler;

    // SpringMVC 会回调该方法，然后往控制层中对应的方法参数赋值
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolverHandler);
    }

}
```

```java
@Component
public class UserArgumentResolverHandler implements HandlerMethodArgumentResolver {

    @Autowired
    private MiaoshaUserService miaoshaUserService;

    /**
     * 定义解析的参数类型
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz = methodParameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    /**
     * 往该参数赋值
     * @param methodParameter
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        String paramToken = request.getParameter(MiaoshaUserServiceImpl.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserServiceImpl.COOKIE_NAME_TOKEN);
        if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(cookieToken) ? paramToken : cookieToken ;
        return miaoshaUserService.getByToken(response, token);
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(cookieNameToken)) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
```

实现完上面这段代码后，我们的 Controller 就变得非常清爽了

**之前的版本：**

```java
@GetMapping("/to_list")
public String toGoodList(HttpServletResponse response,
                         Model model,
                         @CookieValue(value = MiaoshaUserServiceImpl.COOKIE_NAME_TOKEN, required = false) String cookieToken,
                         @RequestParam(value = MiaoshaUserServiceImpl.COOKIE_NAME_TOKEN, required = false) String paramToken) {
    if (StringUtils.isEmpty(cookieToken) && StringUtils.isEmpty(paramToken)) {
        return "login";
    }
    String token = StringUtils.isEmpty(cookieToken) ? paramToken : cookieToken ;
    MiaoshaUser user = miaoshaUserService.getByToken(response, token);
    LOGGER.info("user:{}", user);
    model.addAttribute("user", user);
    return "goods_list";
}
```

**优化后的版本：**

```java
@GetMapping("/to_list")
public String toGoodList(Model model, MiaoshaUser miaoshaUser) {
    LOGGER.info("miaoshaUser:{}", miaoshaUser);
    model.addAttribute("user", miaoshaUser);
    return "goods_list";
}
```

### 数据库主键自增策略

我们在企业级一般不会用自增主键，因为数据很容易就被别人全部遍历起来了，我们一般都使用 `snowflake` 算法

### 如何在做插入数据时把插入后数据的主键id返回

```java
@SelectKey(keyColumn = "id", keyProperty = "id", resultType = Long.class, before = false, statement = "SELECT last_insert_id()")
Long insert(OrderInfo orderInfo);
```



### 如何保证 Redis 和 MySQL 的一致性

我们无法严格保证两个数据库的数据一致性，因为我们无论先操作哪一个数据库，系统在某一段时间内都会处于不稳定的**中间态**。所以我们只能说**尽最大努力保证数据一致性**。

