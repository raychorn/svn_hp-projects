@echo off
@IF not defined ANT_HOME (
   @echo You must set the env variable ANT_HOME to your Apache Ant folder
   goto end
)
@IF not defined VSPHERE_SDK_HOME (
   @echo You must set the env variable VSPHERE_SDK_HOME to your vSphere Web Client SDK folder
   goto end
)
@IF not exist "%VSPHERE_SDK_HOME%\libs\vsphere-client-lib.jar" (
   @echo VSPHERE_SDK_HOME is not set to a valid vSphere Web Client SDK folder
   @echo %VSPHERE_SDK_HOME%\libs\vsphere-client-lib.jar is missing
   goto end
)

REM --- call the default Ant build script, i.e. build.xml in the same folder
REM --- (if Ant runs out of memory you can define ANT_OPTS=-Xmx512M)

@call "%ANT_HOME%\bin\ant" -f build-java.xml

@echo.
@echo War file was created in %~dp0target
:end
