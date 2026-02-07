# AI对话系统快速启动指南

## 项目结构

本项目包含两个独立的部分：
- **后端**：Spring Boot应用（当前目录）
- **前端**：React应用（`cooking-chat-web/` 目录）

---

## 一、数据库准备

### 1. 执行SQL脚本
```bash
mysql -u root -p your_database < src/main/resources/db/migration/chat_tables.sql
```

或者手动执行以下SQL：

```sql
-- 1. 添加用户表字段
ALTER TABLE `user` ADD COLUMN `email` VARCHAR(100) NULL COMMENT '邮箱' AFTER `phone`;
ALTER TABLE `user` ADD COLUMN `password` VARCHAR(255) NULL COMMENT '密码（加密后）' AFTER `email`;

-- 2. 创建会话表
CREATE TABLE IF NOT EXISTS `conversation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `title` VARCHAR(100) NOT NULL DEFAULT '新对话' COMMENT '会话标题',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_update_time` (`update_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话表';

-- 3. 创建消息表
CREATE TABLE IF NOT EXISTS `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `conversation_id` BIGINT NOT NULL COMMENT '会话ID',
  `sender` VARCHAR(20) NOT NULL COMMENT '发送方：USER/AI/SYSTEM',
  `content` TEXT NOT NULL COMMENT '消息内容',
  `send_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  `message_type` VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型：TEXT/IMAGE/FILE',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_send_time` (`send_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';
```

## 二、后端启动

### 1. 确认依赖
确保pom.xml中包含以下依赖：
- Spring Boot Web
- MyBatis Plus
- JWT
- BCrypt（Spring Security）
- Validation

### 2. 启动应用
```bash
mvn spring-boot:run
```

或在IDE中直接运行主类。

### 3. 验证后端
访问：http://localhost:8080/api/auth/login
应该返回400错误（因为没有提供参数），说明接口正常。

## 三、前端启动

### 1. 安装依赖
```bash
cd cooking-chat-web
npm install
```

### 2. 配置代理
确保 `vite.config.js` 中配置了代理：

```javascript
export default {
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
}
```

### 3. 启动开发服务器
```bash
npm run dev
```

### 4. 访问应用
打开浏览器访问：http://localhost:3000

## 四、功能测试

### 1. 用户注册
- 输入姓名（不超过10位）
- 输入手机号（11位，1开头）
- 输入邮箱
- 输入密码（至少8位，不能纯数字）
- 点击注册

### 2. 用户登录
- 输入手机号
- 输入密码
- 点击登录

### 3. 创建对话
- 登录成功后，点击"+ 新对话"
- 系统会创建一个新的对话窗口

### 4. 发送消息
- 在输入框中输入消息
- 按Enter发送（Shift+Enter换行）
- AI会实时流式返回回复

### 5. 管理对话
- 点击左侧对话列表切换对话
- 点击对话右侧的"×"删除对话
- 最多可创建10个对话

## 五、API测试

### 使用Postman测试

#### 1. 注册接口
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "name": "测试用户",
  "phone": "13800138000",
  "email": "test@example.com",
  "password": "test1234"
}
```

#### 2. 登录接口
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "phone": "13800138000",
  "password": "test1234"
}
```

返回的token用于后续请求。

#### 3. 创建会话
```
POST http://localhost:8080/api/conversation/create
Content-Type: application/json
token: <从登录接口获取的token>

{
  "title": "测试对话"
}
```

#### 4. 发送消息
```
POST http://localhost:8080/api/chat/stream
Content-Type: application/json
token: <从登录接口获取的token>

{
  "userId": "1",
  "question": "你好",
  "stream": true
}
```

## 六、常见问题

### 1. 数据库连接失败
- 检查application.yml中的数据库配置
- 确认MySQL服务已启动
- 确认数据库用户名密码正确

### 2. Token验证失败
- 检查JWT配置是否正确
- 确认拦截器配置正确
- 检查token是否过期

### 3. 前端无法连接后端
- 检查后端是否启动（8080端口）
- 检查vite代理配置
- 检查CORS配置

### 4. AI响应失败
- 检查LlmService实现
- 确认大模型API配置正确
- 查看后端日志

### 5. 会话创建失败
- 检查是否已达到10个会话上限
- 检查数据库表是否创建成功
- 查看后端日志

## 七、项目结构

```
cooking-chat-web/                    # 前端项目（独立）
├── src/
│   ├── pages/
│   │   ├── Login.jsx          # 登录页面
│   │   ├── Register.jsx       # 注册页面
│   │   ├── ChatPage.jsx       # 对话页面
│   │   ├── Auth.css           # 认证页面样式
│   │   └── ChatPage.css       # 对话页面样式
│   ├── App.jsx                # 应用主组件
│   ├── main.jsx               # 应用入口
│   └── index.css              # 全局样式
├── index.html                 # HTML模板
├── vite.config.js             # Vite配置
├── package.json               # 项目配置
└── README.md                  # 项目文档

src/main/java/com/cooking/      # 后端项目
├── controller/
│   └── chat/
│       ├── AuthController.java           # 认证控制器
│       ├── ChatController.java           # 对话控制器
│       └── ConversationController.java   # 会话控制器
├── entity/
│   ├── Conversation.java                 # 会话实体
│   └── Message.java                      # 消息实体
├── mapper/
│   ├── ConversationMapper.java
│   └── MessageMapper.java
├── service/
│   ├── ConversationService.java
│   └── impl/
│       └── ConversationServiceImpl.java
└── dto/
    ├── UserLoginRequest.java
    └── UserRegisterRequest.java
```

## 八、下一步

1. **优化UI**：根据需求调整样式和布局
2. **添加功能**：
   - 会话标题自动生成（基于首条消息）
   - 消息搜索
   - 导出对话
   - 图片和文件上传
3. **性能优化**：
   - 消息分页加载
   - 虚拟滚动
   - 缓存优化
4. **安全加固**：
   - 添加验证码
   - 限流
   - XSS防护

## 九、技术支持

如有问题，请查看：
1. 后端日志：查看Spring Boot控制台输出
2. 前端控制台：浏览器F12查看Network和Console
3. 数据库日志：检查SQL执行情况

祝使用愉快！🎉
