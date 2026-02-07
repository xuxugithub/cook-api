@echo off
chcp 65001 >nul
echo ======================================
echo   AI对话系统 - 前端启动脚本
echo ======================================
echo.

REM 检查Node.js是否安装
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ 错误：未检测到Node.js，请先安装Node.js
    pause
    exit /b 1
)

echo ✓ Node.js已安装
node -v
npm -v
echo.

REM 检查node_modules是否存在
if not exist "node_modules" (
    echo 📦 首次运行，正在安装依赖...
    call npm install
    if %errorlevel% neq 0 (
        echo ❌ 依赖安装失败
        pause
        exit /b 1
    )
    echo ✓ 依赖安装完成
    echo.
)

REM 检查后端是否启动
echo 🔍 检查后端服务...
curl -s http://localhost:8080/api/auth/login >nul 2>nul
if %errorlevel% equ 0 (
    echo ✓ 后端服务正常运行
) else (
    echo ⚠️  警告：后端服务未启动或无法访问
    echo    请确保后端服务运行在 http://localhost:8080
)
echo.

REM 启动开发服务器
echo 🚀 启动开发服务器...
echo    访问地址: http://localhost:3000
echo.
echo 按 Ctrl+C 停止服务器
echo ======================================
echo.

npm run dev
