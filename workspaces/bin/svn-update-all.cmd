@echo on

SET PROJECT_HOME=C:\workspaces(HP-SVN)\voyage-development-7.3

SET SVN_HOME=C:\Program Files\SlikSvn\bin

IF EXIST C:\workspaces\svn-update-all.txt del C:\workspaces\svn-update-all.txt

FOR %%A IN (
    "%PROJECT_HOME%/hpic4vc_provider/"
    "%PROJECT_HOME%/hpic4vc_server_provider/"
    "%PROJECT_HOME%/hpic4vc_server_ui/"
    "%PROJECT_HOME%/hpic4vc_storage_provider/"
    "%PROJECT_HOME%/hpic4vc_storage_provider_dummy/"
    "%PROJECT_HOME%/hpic4vc_storage_ui/"
    "%PROJECT_HOME%/hpic4vc_ui/"
    "%PROJECT_HOME%/server/"
    "%PROJECT_HOME%/uim/"
) DO "%SVN_HOME%\svn" update %%A 1>> "C:\workspaces\svn-update-all.txt" 2>&1

SET PROJECT_HOME=C:\workspaces

FOR %%A IN (
    "%PROJECT_HOME%/server/"
    "%PROJECT_HOME%/uim/"
) DO "%SVN_HOME%\svn" update %%A 1>> "C:\workspaces\svn-update-all.txt" 2>&1

SET PROJECT_HOME=

exit
