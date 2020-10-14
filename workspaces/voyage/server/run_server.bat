rem $Author: partho.bhowmick@hp.com $
rem $Date: 2013-06-18 11:12:10 -0500 (Tue, 18 Jun 2013) $
rem $HeadURL: https://svn02.atlanta.hp.com/local/ic4vc-dev/server/trunk/run_server.bat $
rem $Id: run_server.bat 5535 2013-06-18 16:12:10Z partho.bhowmick@hp.com $
rem $Rev: 5535 $

echo on
set HTTPS_PROXY=
set HTTP_PROXY=
rem cmd /K CD %USERPROFILE%\workspace\server
python src\server.py

