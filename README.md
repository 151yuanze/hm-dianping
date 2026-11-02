# 黑马点评（hm-dianping）

一个基于 Spring Boot 的本地生活类点评项目练习，包含店铺浏览、优惠券秒杀、探店笔记、点赞与关注等完整业务流程，后端 + 前端静态页面均可本地运行。

## 技术栈

- Java 8、Spring Boot 2.3.12
- MySQL、MyBatis-Plus
- Redis（Lettuce、Redisson）、RabbitMQ
- JWT、Hutool
- Nginx（托管前端页面并反向代理 `/api`）

## 主要功能

- 短信验证码登录、Token 刷新与登录拦截
- 店铺分类查询、按距离查询附近店铺
- Redis 缓存：缓存预热、空值缓存、随机 TTL、逻辑过期
- 优惠券秒杀：Lua 脚本 + 乐观锁 + 分布式锁（一人一单）
- RabbitMQ 异步下单
- 探店笔记发布、点赞排行榜（ZSet）
- 关注 / 取关 / 共同关注（Set）

## 目录结构

```
hmdianping/
├── dianping-master/                  # 完整实现版本（包名 com.hmdp）
├── hmdp-main/
│   ├── hmdp-main/
│   │   ├── hm-dianping/              # 课程初始骨架（部分功能为 TODO）
│   │   ├── NoteFromVisit/            # 完整实现版本（包名 com.sengang）
│   │   └── nginx-1.18.0/             # 前端页面 + Nginx 配置
│   └── README.md
└── .gitignore
```

> 推荐运行 `hmdp-main/hmdp-main/NoteFromVisit`，功能最完整。

## 环境要求

- JDK 8
- Maven 3.6+
- MySQL 5.7 / 8.0
- Redis 5.0+
- RabbitMQ（`NoteFromVisit` 可选，`dianping-master` 需要）
- 已包含 Windows 版 Nginx 1.18.0，无需另行安装

## 快速开始（以 NoteFromVisit 为例）

### 1. 创建数据库并导入数据

```sql
CREATE DATABASE hmdp DEFAULT CHARACTER SET utf8mb4;
```

```bash
mysql -uroot -p hmdp < hmdp-main/hmdp-main/NoteFromVisit/src/main/resources/db/hmdp.sql
```

也可以用 Navicat 等工具先建库 `hmdp`，再运行该 SQL 文件。

### 2. 启动 Redis

本地启动即可。若 Redis 设置了密码，请在第 3 步同步修改配置。

### 3. 修改配置

为避免泄露，仓库中的密码都已替换为占位符，启动前必须改成你自己的环境：

| 文件 | 需要修改的内容 |
| --- | --- |
| `NoteFromVisit/src/main/resources/application.yaml` | Redis 地址、RabbitMQ 地址、`your_password`、`your_jwt_secret` |
| `NoteFromVisit/src/main/resources/application-local.yaml` | 数据库地址与密码 |
| `dianping-master/src/main/resources/application.yaml` | 数据库密码 `your_password` |

- 数据库连接使用 `${hm.db.host}` / `${hm.db.pw}`，通过 `application-local.yaml` 等 profile 提供。
- JWT 密钥请改成任意随机字符串，不要使用示例值。

### 4. 启动后端

方式一（命令行）：

```bash
cd hmdp-main/hmdp-main/NoteFromVisit
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

方式二（IDEA）：运行 `com.sengang.Main`，并在启动配置的 Active profiles 中填入 `local`。

后端默认端口 `8081`。

### 5. 启动前端

进入 Nginx 目录，双击 `nginx.exe` 或执行：

```bash
cd hmdp-main/hmdp-main/nginx-1.18.0
./nginx.exe
```

浏览器访问 <http://localhost:8080>。Nginx 会把 `/api` 请求代理到 `127.0.0.1:8081`。

### 6. 登录

输入任意合法手机号获取验证码。验证码由后端随机生成：

- 控制台日志中会打印：`发送短信验证码，验证码为:xxxxxx`
- 或直接查询 Redis：`redis-cli get "login:code:你的手机号"`

## 运行 dianping-master

```bash
cd dianping-master
mvn spring-boot:run
```

- 主类：`com.hmdp.HmDianPingApplication`
- 数据库名默认为 `dianping`，需要先建库同名的数据库（或修改 `application.yaml` 中的库名）
- 需要本地 RabbitMQ（默认 `guest/guest`）
- 前端仍可使用上面的 Nginx，访问 8080 端口

## 常见问题

**提示数据库连接失败**
检查 MySQL 是否启动、库名是否为 `hmdp`/`dianping`、账号密码是否与配置文件一致。

**验证码收不到**
本项目未接入真实短信服务，验证码只写入 Redis 并打印在控制台，按上面第 6 步获取即可。

**Nginx 启动报错缺少 logs 目录**
`logs/` 目录需存在，仓库中已保留 `.gitkeep` 占位文件。

**`hm-dianping` 部分功能无法使用**
该目录是课程初始骨架，`UserController` 等处仍是 TODO，完整功能请运行 `NoteFromVisit` 或 `dianping-master`。

## 说明

- 仓库已忽略 `.idea/`、`target/`、日志和本地数据库配置，避免提交个人环境信息。
- SQL 中的用户数据为课程演示数据，与真实人物无关。
