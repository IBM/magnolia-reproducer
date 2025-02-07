@echo off
rem Check for the existence of $JDKPath (which might be a variable that was substituted by an external script, i.e installer)
if exist "$JDKPath" set JAVA_HOME=$JDKPath

rem Magnolia needs extra memory
set CATALINA_OPTS=%CATALINA_OPTS% -Xms64M -Xmx2048M -Djava.awt.headless=true

set CURDIR=%~dp0
if exist "%CURDIR%\magnolia_banner.txt" type %CURDIR%\magnolia_banner.txt
