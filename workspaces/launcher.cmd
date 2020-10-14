@echo off

echo %COMPUTERNAME%

echo "start-hpcs"
REM PAUSE "???"

C:\workspaces\start-hpcs

echo "start-server"
PAUSE "???"

C:\workspaces\start-server

echo "start-uim"
PAUSE "???"

C:\workspaces\start-uim

:EXIT
echo "EXIT"
exit
