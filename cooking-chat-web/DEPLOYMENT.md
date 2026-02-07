# 部署指南

## 开发环境部署

### 前置要求
- Node.js 16+
- npm 或 yarn
- 后端服务已启动（默认端口8080）

### 步骤

1. **安装依赖**
```bash
npm install
```

2. **启动开发服务器**
```bash
npm run dev
```

3. **访问应用**
```
http://localhost:3000
```

## 生产环境部署

### 方式一：使用Nginx

1. **构建项目**
```bash
npm run build
```

2. **配置Nginx**

创建配置文件 `/etc/nginx/sites-available/cooking-chat`：

```nginx
server {
    listen 80;
    server_name your-domain.com;
    
    # 前端静态文件
    root /var/www/cooking-chat-web/dist;
    index index.html;
    
    # SPA路由支持
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # SSE支持
        proxy_buffering off;
        proxy_cache off;
        proxy_set_header Connection '';
        proxy_http_version 1.1;
        chunked_transfer_encoding off;
    }
    
    # 静态资源缓存
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

3. **启用配置**
```bash
sudo ln -s /etc/nginx/sites-available/cooking-chat /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

### 方式二：使用Docker

1. **创建Dockerfile**

```dockerfile
# 构建阶段
FROM node:18-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# 运行阶段
FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

2. **创建nginx.conf**

```nginx
server {
    listen 80;
    server_name localhost;
    
    root /usr/share/nginx/html;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_buffering off;
        proxy_cache off;
    }
}
```

3. **构建和运行**
```bash
docker build -t cooking-chat-web .
docker run -d -p 3000:80 --name cooking-chat cooking-chat-web
```

### 方式三：使用Vercel/Netlify

1. **配置vercel.json**

```json
{
  "rewrites": [
    {
      "source": "/api/:path*",
      "destination": "http://your-backend-url.com/api/:path*"
    },
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

2. **部署**
```bash
npm install -g vercel
vercel --prod
```

## 环境变量配置

创建 `.env.production` 文件：

```env
VITE_API_BASE_URL=https://api.your-domain.com
```

在代码中使用：
```javascript
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';
```

## 性能优化

### 1. 代码分割
已通过Vite自动实现

### 2. 资源压缩
```bash
npm install -D vite-plugin-compression
```

更新 `vite.config.js`：
```javascript
import compression from 'vite-plugin-compression'

export default {
  plugins: [
    react(),
    compression()
  ]
}
```

### 3. CDN加速
将静态资源上传到CDN，更新 `vite.config.js`：
```javascript
export default {
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['react', 'react-dom']
        }
      }
    }
  }
}
```

## 监控和日志

### 1. 错误监控
集成Sentry：
```bash
npm install @sentry/react
```

```javascript
import * as Sentry from "@sentry/react";

Sentry.init({
  dsn: "your-sentry-dsn",
  environment: "production"
});
```

### 2. 访问日志
Nginx日志位置：
- 访问日志：`/var/log/nginx/access.log`
- 错误日志：`/var/log/nginx/error.log`

## 安全配置

### 1. HTTPS配置
使用Let's Encrypt：
```bash
sudo certbot --nginx -d your-domain.com
```

### 2. 安全头
在Nginx配置中添加：
```nginx
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "no-referrer-when-downgrade" always;
```

## 故障排查

### 1. 白屏问题
- 检查浏览器控制台错误
- 检查静态资源路径
- 检查Nginx配置

### 2. API请求失败
- 检查后端服务状态
- 检查Nginx代理配置
- 检查CORS设置

### 3. SSE连接失败
- 检查Nginx缓冲配置
- 检查防火墙设置
- 检查超时配置

## 回滚策略

1. **保留旧版本**
```bash
cp -r dist dist.backup.$(date +%Y%m%d)
```

2. **快速回滚**
```bash
rm -rf dist
mv dist.backup.20240207 dist
sudo systemctl reload nginx
```

## 更新流程

1. 拉取最新代码
2. 安装依赖
3. 运行测试
4. 构建生产版本
5. 备份当前版本
6. 部署新版本
7. 验证功能
8. 监控错误

```bash
git pull
npm install
npm run build
sudo cp -r /var/www/cooking-chat-web/dist /var/www/cooking-chat-web/dist.backup
sudo rm -rf /var/www/cooking-chat-web/dist
sudo cp -r dist /var/www/cooking-chat-web/
sudo systemctl reload nginx
```

## 联系支持

如遇到部署问题，请联系技术支持团队。
