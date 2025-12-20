@echo off
setlocal
:: 设置控制台编码为UTF-8(解决IDEA控制台乱码问题)
chcp 65001

:: 获取项目根目录(上跳4级)
pushd "%~dp0..\..\..\.."
set PROJECT_ROOT=%CD%
popd

:: 设置classpath
set APP_CLASSPATH=%PROJECT_ROOT%\target\classes

:: 循环执行10次
for /L %%n in (1, 1, 10) do (
    echo --------------------------------------------------
    echo 第 %%n 次运行:
    java -Dfile.encoding=UTF-8 -cp "%APP_CLASSPATH%" com.ltx.exercise.TicketWindow
    echo.
)

endlocal