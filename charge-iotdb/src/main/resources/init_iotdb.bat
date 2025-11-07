@echo off

REM IoTDB数据库初始化批处理脚本 - Windows版本
REM 充电桩监控系统专用

setlocal enabledelayedexpansion

REM 脚本版本
set VERSION=1.0.1

REM 配置信息
set IOTDB_HOST=127.0.0.1
set IOTDB_PORT=6667
set IOTDB_USERNAME=root
set IOTDB_PASSWORD=root

:DISPLAY_HEADER
cls
echo =================================================================
echo                      IoTDB数据库初始化工具                       
echo                        版本: %VERSION%
echo                      充电桩监控系统专用                         
echo =================================================================
echo.
echo 本工具用于初始化充电桩监控系统的IoTDB数据库结构
echo 包括创建时间序列、设置标签属性和初始化状态数据
echo.
echo 当前配置:
echo   - IoTDB服务器: %IOTDB_HOST%:%IOTDB_PORT%
echo   - 用户名: %IOTDB_USERNAME%
echo   - 密码: ********
echo.

:DISPLAY_MENU
echo 请选择操作:
echo 1. 使用SQL脚本初始化数据库
echo 2. 修改配置
echo 3. 查看帮助
echo 4. 退出
echo.
set /p CHOICE=请输入选项 [1-4]: 

REM 处理用户选择
if "%CHOICE%"=="1" goto EXECUTE_SQL
if "%CHOICE%"=="2" goto MODIFY_CONFIG
if "%CHOICE%"=="3" goto DISPLAY_HELP
if "%CHOICE%"=="4" goto EXIT

echo 无效的选项，请重新输入！
pause
goto DISPLAY_MENU

:EXECUTE_SQL
echo.
echo =================================================================
echo                使用SQL脚本初始化数据库                
echo =================================================================
echo.

echo 正在检查IoTDB客户端工具...

REM 检查是否存在IoTDB客户端工具
if not exist "%IOTDB_HOME%\sbin\start-cli.bat" (
    echo 错误: 未找到IoTDB客户端工具！
    echo 请设置IOTDB_HOME环境变量指向IoTDB安装目录
    echo 或者手动使用IoTDB客户端执行 iotdb-init-script.sql 文件
    pause
    goto DISPLAY_MENU
)

echo 找到IoTDB客户端工具，准备执行SQL脚本...
pause

echo 正在执行SQL脚本...
"%IOTDB_HOME%\sbin\start-cli.bat" -h %IOTDB_HOST% -p %IOTDB_PORT% -u %IOTDB_USERNAME% -pw %IOTDB_PASSWORD% -f "%~dp0\iotdb-init-script.sql"

echo.
if %errorlevel% equ 0 (
    echo SQL脚本执行成功！
) else (
    echo SQL脚本执行失败！请检查错误信息
)
pause
goto DISPLAY_MENU



:MODIFY_CONFIG
echo.
echo =================================================================
echo                修改配置                
echo =================================================================
echo.

set /p NEW_HOST=请输入IoTDB服务器地址 [默认: %IOTDB_HOST%]: 
if not "%NEW_HOST%"=="" set IOTDB_HOST=%NEW_HOST%

set /p NEW_PORT=请输入IoTDB服务器端口 [默认: %IOTDB_PORT%]: 
if not "%NEW_PORT%"=="" set IOTDB_PORT=%NEW_PORT%

set /p NEW_USER=请输入用户名 [默认: %IOTDB_USERNAME%]: 
if not "%NEW_USER%"=="" set IOTDB_USERNAME=%NEW_USER%

set /p NEW_PASS=请输入密码 [默认: 保持不变]: 
if not "%NEW_PASS%"=="" set IOTDB_PASSWORD=%NEW_PASS%

echo.
echo 配置已更新:
echo   - IoTDB服务器: %IOTDB_HOST%:%IOTDB_PORT%
echo   - 用户名: %IOTDB_USERNAME%
echo   - 密码: ********
echo.
pause
goto DISPLAY_HEADER

:DISPLAY_HELP
echo.
echo =================================================================
echo                          使用帮助                         
echo =================================================================
echo.
echo 1. 环境要求:
echo    - 已安装并运行的IoTDB服务器 1.3.2版本
    - 设置IOTDB_HOME环境变量指向IoTDB 1.3.2安装目录
    
echo 2. 使用方法:
echo    - 选择SQL脚本方式：通过IoTDB客户端执行SQL脚本
    
echo 3. 初始化内容:
echo    - 创建4个充电桩的时间序列（CP001-CP004）
    - 设置设备位置和类型标签
    - 初始化所有充电桩为空闲状态
    
echo 4. 注意事项:
echo    - 请确保IoTDB服务器正在运行
    - 请确保有足够的权限创建时间序列
    - 初始化前请备份现有数据（如果有）
echo.
pause
goto DISPLAY_MENU

:EXIT
echo.
echo =================================================================
echo                          感谢使用                         
echo =================================================================
pause
endlocal
exit /b 0