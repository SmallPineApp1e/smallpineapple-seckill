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