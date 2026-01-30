# 做菜小程序后端系统

基于 Spring Boot + MyBatis Plus + Redis + MySQL 的做菜小程序后端系统。

## 技术栈

- **JDK**: 11
- **Spring Boot**: 2.7.14
- **MyBatis Plus**: 3.5.3.1
- **MySQL**: 8.0+
- **Redis**: 用于缓存
- **Nginx**: 用于反向代理和静态资源服务

## 项目结构

```
src/main/java/com/cooking/
├── common/          # 通用类（统一响应、异常处理）
├── config/          # 配置类（Redis、MyBatis Plus）
├── controller/      # 控制器层
│   ├── admin/       # 后台管理接口
│   └── app/         # 小程序接口
├── dto/             # 数据传输对象
├── entity/          # 实体类
├── mapper/          # Mapper接口
├── service/         # 服务层
│   └── impl/        # 服务实现类
└── util/            # 工具类
```

## 数据库设计

### 主要表结构

1. **user** - 用户表
2. **user_follow** - 用户关注表
3. **category** - 菜单分类表
4. **dish** - 菜品表
5. **dish_step** - 菜品制作步骤表
6. **dish_ingredient** - 菜品食材表
7. **user_favorite** - 用户收藏表
8. **shopping_cart** - 购物车表
9. **banner** - Banner轮播图表

## 功能模块

### 后台管理系统

- 菜单分类管理（增删改查）
- 菜品管理（增删改查、上架下架）
- 菜品配置（制作步骤、食材）
- Banner轮播图管理（增删改查、启用/禁用）

### 小程序接口

- **菜单分类**: 获取分类列表
- **菜品列表**: 分页查询菜品
- **菜品搜索**: 根据菜品名称模糊搜索
- **菜品详情**: 获取菜品详细信息（包含制作步骤和食材）
- **菜品收藏**: 收藏/取消收藏、收藏列表
- **购物车**: 添加、更新、删除、清空、列表查询
- **菜品分享**: 分享功能（增加分享次数）
- **用户关注**: 关注/取消关注、关注列表、粉丝列表
- **用户主页**: 查看用户信息和收藏的菜品
- **Banner轮播图**: 获取首页轮播图列表

## 快速开始

### 1. 环境要求

- JDK 11+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+

### 2. 数据库配置

执行 `src/main/resources/db/schema.sql` 创建数据库和表结构。

### 3. 配置文件

修改 `src/main/resources/application.yml` 中的配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/cooking_db?...
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379

# 微信小程序配置
wechat:
  appid: your_appid      # 替换为你的小程序AppID
  secret: your_secret    # 替换为你的小程序AppSecret

# JWT配置
jwt:
  secret: your-secret-key  # JWT密钥，建议使用随机字符串
  expire-time: 604800000   # token过期时间（毫秒），默认7天
```

### 4. 运行项目

```bash
mvn spring-boot:run
```

或打包后运行：

```bash
mvn clean package
java -jar target/cooking-app-1.0.0.jar
```

## API接口说明

### 后台管理接口（/admin）

#### 管理员登录（无需token）
- `POST /admin/login` - 管理员登录
  - 请求参数：`{"username": "用户名", "password": "密码"}`
  - 返回：`{"token": "JWT token", "adminId": 管理员ID, "username": "用户名", "name": "姓名"}`
  - 默认账号：用户名 `admin`，密码 `123456`

#### 管理员信息（需要token）
- `GET /admin/info` - 获取当前管理员信息
- `PUT /admin/info` - 修改管理员信息
- `POST /admin/logout` - 管理员登出

#### 分类管理（需要token）

#### 分类管理（需要token）
- `GET /admin/category/page` - 分页查询分类
- `GET /admin/category/list` - 获取所有分类
- `GET /admin/category/{id}` - 查询分类详情
- `POST /admin/category` - 新增分类
- `PUT /admin/category` - 更新分类
- `DELETE /admin/category/{id}` - 删除分类

#### 菜品管理（需要token）

#### 菜品管理（需要token）
- `GET /admin/dish/page` - 分页查询菜品
- `GET /admin/dish/{id}` - 查询菜品详情
- `POST /admin/dish` - 新增菜品
- `PUT /admin/dish` - 更新菜品
- `DELETE /admin/dish/{id}` - 删除菜品
- `PUT /admin/dish/status/{id}` - 上架/下架菜品

#### Banner管理（需要token）

#### Banner管理（需要token）
- `GET /admin/banner/page` - 分页查询Banner
- `GET /admin/banner/list` - 获取所有Banner
- `GET /admin/banner/{id}` - 查询Banner详情
- `POST /admin/banner` - 新增Banner
- `PUT /admin/banner` - 更新Banner
- `DELETE /admin/banner/{id}` - 删除Banner
- `PUT /admin/banner/status/{id}` - 启用/禁用Banner

### 小程序接口（/app）

#### 用户登录（无需token）
- `POST /app/user/login` - 微信登录
  - 请求参数：`{"code": "微信code", "nickname": "昵称(可选)", "avatar": "头像(可选)"}`
  - 返回：`{"token": "JWT token", "userId": 用户ID, "nickname": "昵称", "avatar": "头像"}`

#### 用户信息（需要token）
- `GET /app/user/info` - 获取用户信息
  - 请求头：`token: JWT token`

#### 公开接口（无需token，支持可选登录）
- `GET /app/category/list` - 获取分类列表
- `GET /app/dish/page` - 分页查询菜品列表
- `GET /app/dish/{id}` - 获取菜品详情
- `GET /app/dish/search` - 搜索菜品（根据菜品名称模糊搜索）
  - 参数：`keyword` - 搜索关键词, `current` - 当前页, `size` - 每页大小
- `GET /app/banner/list` - 获取Banner轮播图列表
- `GET /app/user/profile/{userId}` - 获取用户主页信息
- `GET /app/user/profile/{userId}/favorites` - 获取用户收藏的菜品列表

#### 需要登录的接口（需要token）
- `POST /app/dish/share/{id}` - 分享菜品

- `POST /app/favorite/add` - 收藏菜品
  - 参数：`dishId` - 菜品ID
- `POST /app/favorite/remove` - 取消收藏
  - 参数：`dishId` - 菜品ID
- `GET /app/favorite/list` - 获取收藏列表
- `GET /app/favorite/check` - 判断是否已收藏
  - 参数：`dishId` - 菜品ID

- `POST /app/cart/add` - 添加购物车
  - 参数：`dishId` - 菜品ID, `quantity` - 数量（默认1）
- `PUT /app/cart/update` - 更新购物车数量
  - 参数：`dishId` - 菜品ID, `quantity` - 数量
- `DELETE /app/cart/remove` - 移除购物车
  - 参数：`dishId` - 菜品ID
- `DELETE /app/cart/clear` - 清空购物车
- `GET /app/cart/list` - 获取购物车列表

- `POST /app/follow/add` - 关注用户
  - 参数：`followUserId` - 被关注用户ID
- `POST /app/follow/remove` - 取消关注
  - 参数：`followUserId` - 被关注用户ID
- `GET /app/follow/check` - 判断是否已关注
  - 参数：`followUserId` - 被关注用户ID
- `GET /app/follow/list` - 获取关注列表（我关注的用户）
  - 参数：`userId` - 用户ID
- `GET /app/follow/fans` - 获取粉丝列表（关注我的用户）
  - 参数：`userId` - 用户ID

## 注意事项

1. 所有接口返回统一格式：`Result<T>`
2. 使用逻辑删除，不会真正删除数据
3. 菜品详情接口会自动增加浏览次数
4. 收藏和取消收藏会自动更新菜品的收藏数
5. 分享接口会自动增加分享次数
6. 小程序接口需要携带token（登录接口除外），token放在请求头 `token` 字段
7. **管理员接口需要携带管理员token（登录接口除外），token放在请求头 `token` 字段**
8. 用户ID从token中自动解析，无需在请求参数中传递
9. 微信登录需要配置小程序的AppID和AppSecret
10. 用户关注功能支持查看关注列表和粉丝列表
11. Banner轮播图由后台管理系统配置，小程序端获取启用的Banner列表
12. 菜品搜索支持根据菜品名称模糊搜索
13. **管理员密码使用BCrypt加密存储，默认管理员账号：admin/123456**

## Nginx配置示例

```nginx
server {
    listen 80;
    server_name your_domain.com;

    # 静态资源
    location /static/ {
        alias /path/to/static/files/;
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```
