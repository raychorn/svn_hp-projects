@echo off

echo %COMPUTERNAME%

START "start-server" /SEPARATE /HIGH "C:\workspaces\bin\start-server.cmd"
