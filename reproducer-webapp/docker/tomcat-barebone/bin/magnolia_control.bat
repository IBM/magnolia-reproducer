@echo off
if ""%1"" == ""start"" goto doStart
if ""%1"" == ""stop"" goto doStop
goto noCommand

:doStart
if not exist .installed if not exist ..\webapps\magnoliaPublic\WEB-INF if exist ..\webapps\magnoliaAuthor\WEB-INF goto doInstall
call startup.bat
goto end

:doStop
call shutdown.bat
goto end

:doInstall
echo First run -> create magnoliaPublic webapp from magnoliaAuthor webapp.
if exist ..\webapps\magnoliaPublic rmdir ..\webapps\magnoliaPublic /s /q
mkdir ..\webapps\magnoliaPublic
xcopy ..\webapps\magnoliaAuthor\docroot ..\webapps\magnoliaPublic\docroot /e /i /h
xcopy ..\webapps\magnoliaAuthor\modules ..\webapps\magnoliaPublic\modules /e /i /h
xcopy ..\webapps\magnoliaAuthor\META-INF ..\webapps\magnoliaPublic\META-INF /e /i /h
xcopy ..\webapps\magnoliaAuthor\WEB-INF ..\webapps\magnoliaPublic\WEB-INF /e /i /h
copy ..\webapps\magnoliaAuthor\LICENSE.txt ..\webapps\magnoliaPublic\LICENSE.txt
copy ..\webapps\magnoliaAuthor\NOTICE.txt ..\webapps\magnoliaPublic\NOTICE.txt
copy ..\webapps\magnoliaAuthor\README.txt ..\webapps\magnoliaPublic\README.txt
echo This file indicates that the public webapp was created. The file is created during first run. > .installed
goto doStart

:noCommand
echo Please provide "start" or "stop" as argument.

:end
pause
