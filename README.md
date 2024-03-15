# 资料文库

## 需求分析

* 用户部分

1. 注册/登录/注销 ： 分布式登录
2. 用户管理（管理员/普通用户）
3. 点赞资料、收藏资料/取消点赞、取消收藏
4. 增加、获取浏览记录

* 文件资料部分

1. 文件上传

2. 给文件增加标签

3. 文件删除

4. 文件查询

   1. 通过文件名模糊查询
   2. 通过文件ID查询
   3. 通过文件标签查询

   



## 技术栈

1. Java编程语言
2. SpringBoot框架
3. SpringMVC + Mybatis/MybatisPlus
4. Junit单元测试库
5. Mysql数据库、Redis数据库
6. Swagger + Knife4j接口文档



## 数据库建表

1. 用户表user

> ```sql
> create table user
> (
>     userName     varchar(256)                       null comment '用户名称',
>     id           bigint auto_increment comment 'id(主键)'
>         primary key,
>     userAccount  varchar(256)                       null comment '账号',
>     gender       tinyint                            null comment '性别',
>     userPassword varchar(512)                       not null comment '密码',
>     email        varchar(512)                       null comment '邮箱',
>     createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
>     isDelete     tinyint  default 0                 not null comment '是否删除',
>     userRole     int      default 0                 not null comment '用户身份 0 - 普通用户; 1 - 管理员',
>     userTags     varchar(1024)                      null comment '标签 json 列表'
> )
>     comment '用户';
> 
> ```

2. 资料表document

> ```sql
> create table document
> (
>     documentName varchar(256)                       not null comment '资料名称',
>     documentId   bigint auto_increment comment '资料id(主键)'
>         primary key,
>     category     varchar(256)                       not null comment '资料类型',
>     uploadUserId bigint                             not null comment '上传用户ID',
>     uploadTime   datetime default CURRENT_TIMESTAMP null comment '上传时间',
>     isDelete     tinyint  default 0                 not null comment '是否删除',
>     ducomentUrl  varchar(256)                       not null comment '文档URL',
>     tags         varchar(1024)                      null comment '文档标签（JSON列表）',
>     likes        bigint   default 0                 not null comment '点赞数',
>     browser      bigint   default 0                 null comment '浏览记录',
>     constraint document_user_id_fk
>         foreign key (uploadUserId) references user (id)
> );
> 
> create index idx_category
>     on document (category);
> 
> 
> ```

3. 用户收藏表

> ```sql
> create table userCollect
> (
>     id           bigint auto_increment comment '自增主键'
>         primary key,
>     userId       bigint       null comment '点赞用户id',
>     documentId   bigint       null comment '被点赞文档id',
>     documentUrl  varchar(256) not null comment '文档URL',
>     documentName varchar(256) null comment '文档名称'
> )
>     comment '用户点赞表';
> 
> alter table userCollect add index idx_dUrl_dNa_uId(userId,documentName,documentUrl);
> 
> ```
>
> 

## 用户基本服务接口开发

1. 注册
2. 登录
3. 注销

### Cookie和Session

> 1. cookie是存储再浏览器端的一小段文本。
> 2. seesion是存储在服务器端的一组数据
>
> - session存储于服务器，可以理解为一个状态列表，拥有一个唯一识别符号sessionId，通常存放于cookie中。服务器收到cookie后解析出sessionId，再去session列表中查找，才能找到相应session。依赖cookie
> - cookie类似一个令牌，装有sessionId，存储在客户端，浏览器通常会自动添加。

> 1. 服务器接收到请求，开启一个Session会话，同时生成一个sessionid，并告诉客户端存储一个Cookie
> 2. 客户端收到响应后，就存储该Cookie信息，在下次访问服务器时在请求头中携带该Cookie。



### 实现单点登录

> Token + Cookie + Redis

> 单点登录是什么？

​	单点登录（Single Sign On，SSO），为一个系统开发登录功能，通常的流程是：

* 登录：登录成功后，在服务器端存储一个SessionId，并通知客户端保存一个Cookie，下次访问时，可以由请求中携带的Cookie和服务器存储的SessionID判断用户身份。
* 注销：将服务器端存储的SessionId删除。

​	这种流程对于只有一个系统是有效的。但是对于多系统就不行了，因为服务器端的SessionID只会存储在本地，而其他服务器没有SessionID信息，这样就会导致用户登录一个系统后，进入另一个关联系统就需要再次登录；

​	对此我们可以用一个Redis数据库来存储用户登录信息，这样每个系统就都可以根据Cookie取Redis中验证用户登录状态了。

​	使用Token代替SessionId

### Q&A



## 整合Redis

1. 添加依赖

```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
  </dependency>
```

2. 配置redis连接信息

```yaml
#  Redis
  redis:
    host: 
    port: 6379
    password: 
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        max-wait: -1ms
        min-idle: 0
    timeout: 3000
    database: 0
```

3. 配置RedisTemplate，自定义序列化器，否则会存入乱码

```java
/**
 * Redis配置类
 *
 * @author gaffey
 *
 */
@Configuration
public class RedisConfig {

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return template;
  }
}
```

## 踩坑

> 如果不自定义序列化器，存入Redis的可能是乱码；

## 通过Springboot定时任务来进行缓存预热

1. 给Springboot打上注解 `@EnableScheduling`
2. 通过`cron`表达式设置任务执行时间 `@Scheduled(cron = "0 0 0 * * *")  // 秒 时 分 * * *`
3. 缓存设计：使用redis的list数据类型，存入文档对象。
4. 利用分布式锁实现仅执行一次



## 分布式锁

实现方式

1. Redis原生实现：setnx命令 + lua脚本

   > setnx命令：设置一个键，不存在设设置，存在则失败；通过设置该键来模拟抢锁场景。
   >
   > 锁过期？：为了防止线程挂掉而锁一直存在，要设置过期时间；而设置过期时间就要考虑锁提前释放的问题。
   >
   > 解锁：只能自己来解锁，其他线程不能解锁，所以要对锁的拥有者有个判断。
   >
   > 并发问题？：虽然redis执行单条命令是原子性的，但是不能保证多条命令同时执行。
   >
   > lua脚本能让redis原子地执行多条命令。

2. 利用Redisson实现

> Redisson是一个在Redis的基础上实现的Java驻内存数据网格，实际上就是一个java的Redis客户端。[详解Redisson - 知乎 (zhihu.com)](https://zhuanlan.zhihu.com/p/678998862)
>
> [redisson/redisson-spring-boot-starter at master · redisson/redisson · GitHub](https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter) Springboot整合Redisson
>
> 看门狗机制：[Redisson 分布式锁的watch dog自动续期机制_redisson续期-CSDN博客](https://blog.csdn.net/qq_26222859/article/details/79645203)
>
> 



## 定时更新文档的点赞和浏览量

1. 同样使用定时任务

==Tip==

> 最刚开始没有想到并发问题，直接通过几条简单的Redis操作命令来更新Redis和数据库中的数据。
>
> 但其实是有问题的，执行流程：A读取了Redis数据，A删除Redis数据；但是因为Redis只能原子执行一条命令，所以完全可能是：A读取数据，B更新数据，A删除数据；那么B更新的数据就不会更新进数据库了；

2. 使用lua脚本来更新Redis缓存数据 [Lua 教程 | 菜鸟教程 (runoob.com)](https://www.runoob.com/lua/lua-tutorial.html)

```java
// [Lua 教程 | 菜鸟教程 (runoob.com)](https://www.runoob.com/lua/lua-tutorial.html)
private final String scriptnew = "local readcount = redis.call('hget',KEYS[1],ARGV[1])\n"+
    "local likecount = redis.call('hget',KEYS[1],ARGV[2])\n" +
    "redis.call('del',KEYS[1])\n"+
    "return likecount .. ':' .. readcount";
```

## 踩坑记录  耗时一个晚上 :cry:

> ​	当连上Redis后，一直拿不到返回值，我还一直以为自己的lua脚本写的有问题，因为上服务器查看redis也没有写上数据。一直改了两个小时，后面才发现原来自己将点赞浏览的数据存入redis3号库了，没有切库；切库过后看到了数据，实际上是一直能写上数据的。
>
> ​	那又是什么问题（一直返回null），我一开始以为是lua脚本返回值的问题，还去想办法让lua返回字符串；
>
> ​	后来看到一篇技术博客，才知道原来是因为lua脚本传过来对数据又序列化一次（自己定义的RedisTemplate已经序列化过了），导致拿不到值的问题。
>
> ​	解决方案：使用没有定义过序列化器的 StringRedisTemplate来操作Redis。



## 文档基本服务接口开发

1. 上传文档：Ftp与服务器通信
2. 删除文档
3. 查询文档
   1. 根据名称模糊查询
   2. 根据Tags模糊查询
   3. 根据文档Id（主键）查询



## Linux服务器搭建FTP服务

> 利用`vsftpd`搭建FTP服务





## 自定义返回封装类和全局异常处理器

```java
/**
 * 通用返回类
 *
 * @author gaffey
 */
@Data
public class BaseResponse<T> implements Serializable {
	private int code;
	private T data;
	private String message;
	private String description;

	public BaseResponse(int code,T data,String message,String description){
		this.code = code;
		this.data = data;
		this.message = message;
		this.description = description;
	}

	public BaseResponse(int code,T data,String message){
		this(code,data,message,"没有具体描述");
	}

	public BaseResponse(int code,String message,String description){
		this(code,null,message,description);
	}

	public BaseResponse(BusinessErrors businessErrors){
		this.code = businessErrors.getCode();
		this.message = businessErrors.getMessage();
		this.description = businessErrors.getDescription();
	}

	public BaseResponse(BusinessErrors businessErrors,String description){
		this.code = businessErrors.getCode();
		this.message = businessErrors.getMessage();
		this.description = description;
	}


}

```

```java
/**
 *自定义业务异常类
 *
 * @author gaffey
 */
@Data
public class BusinessException extends RuntimeException{
	/**
	 * 异常描述
	 */
	private String description;

	/**
	 * 异常码
	 */
	private int code;

	public BusinessException(BusinessErrors businessErrors,String description){
		super(businessErrors.getMessage());
		this.description = description;
		this.code = businessErrors.getCode();
	}
	public BusinessException(BusinessErrors businessErrors){
		super(businessErrors.getMessage());
		this.code = businessErrors.getCode();
		this.description = businessErrors.getDescription();
	}
}

///

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public BaseResponse BusinessExceptionHandler(BusinessException businessException){
		log.error("BusinessException ---- "+businessException.getMessage(),businessException);
		return ResultUtils.error(businessException.getCode(), businessException.getMessage(), businessException.getDescription());
	}

	@ExceptionHandler(RuntimeException.class)
	public BaseResponse RuntimeExceptionHandler(RuntimeException e){
		log.error("RuntimeException ---- "+e.getMessage(),e);
		return ResultUtils.error(BusinessErrors.SYSTEM_ERROR);
	}
}

```













## 导入批量测试数据

> 并发批量地导入数据

```java
//自定义线程池
	private ExecutorService executorService =
			new ThreadPoolExecutor(
        			20,
					100,
					10000,
					TimeUnit.MINUTES,
					new ArrayBlockingQueue<>(1000));
```

```java
/**
	 * 并发批量插入数据
	 */
@Test
public void doConcurrencyInsertDoc() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    // 分十组
    int batchSize = 10000;
    int j = 0;
    List<CompletableFuture<Void>> futureList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        List<Document> documentList = new ArrayList<>();
        while (true) {
            j++;
            Long l = new Random().nextLong(5000);
            Document document = new Document();
            document.setDocumentName("1-批量插入测试文档");
            document.setCategory("图书");
            document.setUploadUserId(1L);
            document.setDucomentUrl("http://document.com");
            document.setTags("[Tags]");
            document.setLikes(l);
            document.setBrowser(l);
            documentList.add(document);
            if (j % batchSize == 0) {
                break;
            }
        }
        // 异步执行
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("threadName: " + Thread.currentThread().getName());
            documentService.saveBatch(documentList, batchSize);
        }, executorService);
        futureList.add(future);
    }
    CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
    stopWatch.stop();
    System.out.println(stopWatch.getTotalTimeMillis());
}


@Test
public void insertCollect(){
    Random random = new Random(500);
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    // 分十组
    int batchSize = 1000;
    int j = 0;
    List<CompletableFuture<Void>> futureList = new ArrayList<>();
    Long uid = 1L;
    Long did = random.nextLong(500);
    for (int i = 0; i < 100; i++) {
        uid++;
        List<Usercollect> collectList = new ArrayList<>();
        while (true) {
            j++;
            Usercollect usercollect = new Usercollect();
            //				usercollect.setId(0L);
            usercollect.setUserId(uid);
            usercollect.setDocumentId(did);
            usercollect.setDocumentUrl("sdaw"+j);
            usercollect.setDocumentName("name"+j+i);
            collectList.add(usercollect);
            if (j % batchSize == 0) {
                break;
            }
        }
        // 异步执行
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("threadName: " + Thread.currentThread().getName());
            usercollectService.saveBatch(collectList, batchSize);
        }, executorService);
        futureList.add(future);
    }
    CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
    stopWatch.stop();
    System.out.println(stopWatch.getTotalTimeMillis());
}
```





