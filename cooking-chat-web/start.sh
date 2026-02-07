#!/bin/bash

echo "======================================"
echo "  AI对话系统 - 前端启动脚本"
echo "======================================"
echo ""

# 检查Node.js是否安装
if ! command -v node &> /dev/null; then
    echo "❌ 错误：未检测到Node.js，请先安装Node.js"
    exit 1
fi

echo "✓ Node.js版本: $(node -v)"
echo "✓ npm版本: $(npm -v)"
echo ""

# 检查node_modules是否存在
if [ ! -d "node_modules" ]; then
    echo "📦 首次运行，正在安装依赖..."
    npm install
    if [ $? -ne 0 ]; then
        echo "❌ 依赖安装失败"
        exit 1
    fi
    echo "✓ 依赖安装完成"
    echo ""
fi

# 检查后端是否启动
echo "🔍 检查后端服务..."
if curl -s http://localhost:8080/api/auth/login > /dev/null 2>&1; then
    echo "✓ 后端服务正常运行"
else
    echo "⚠️  警告：后端服务未启动或无法访问"
    echo "   请确保后端服务运行在 http://localhost:8080"
fi
echo ""

# 启动开发服务器
echo "🚀 启动开发服务器..."
echo "   访问地址: http://localhost:3000"
echo ""
echo "按 Ctrl+C 停止服务器"
echo "======================================"
echo ""

npm run dev
