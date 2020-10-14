@echo off

echo %COMPUTERNAME%

START "start-svn-update-all" /SEPARATE /HIGH "C:\workspaces\bin\svn-update-all.cmd"
