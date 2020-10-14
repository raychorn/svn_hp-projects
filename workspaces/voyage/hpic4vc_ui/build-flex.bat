@echo off
REM --- Windows script starting an Ant build of the current flex project
REM --- You can copy this file and build-flex.xml in your project directory
REM --- (if Ant runs out of memory try defining ANT_OPTS=-Xmx512M)

@IF not defined ANT_HOME (
   @echo You must set the env variable ANT_HOME to your Apache Ant folder
   goto end
)
@IF not defined FLEX_HOME (
   @echo You must set the env variable FLEX_HOME to your Flex SDK folder,
   @echo for instance: set FLEX_HOME=C:\Program Files\Adobe\Adobe Flash Builder 4 Plug-in\sdks\4.1.0
   goto end
)
@IF not defined VSPHERE_SDK_HOME (
   @echo You must set the env variable VSPHERE_SDK_HOME to your vSphere Web Client SDK folder
   goto end
)
@IF not exist "%VSPHERE_SDK_HOME%\libs\vsphere-client-lib.swc" (
   @echo VSPHERE_SDK_HOME is not set to a valid vSphere Web Client SDK folder
   @echo %VSPHERE_SDK_HOME%\libs\vsphere-client-lib.swc is missing
   goto end
)

@call "%ANT_HOME%\bin\ant" -f build-flex.xml

@echo.
@echo War file was created in %~dp0war\target
:end
