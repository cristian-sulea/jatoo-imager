@echo off

if "%1" == "" goto launch

for /f "delims=/ tokens=1-3" %%a in ("%DATE:~4%") do (
	for /f "delims=:. tokens=1-4" %%m in ("%TIME: =0%") do (
		set PARAMS_FILE_NAME=%%c-%%b-%%a-%%m%%n%%o%%p
	)
)

set PARAMS_FOLDER=%USERPROFILE%\.jatoo-app-imager\args
set PARAMS_FILE=%PARAMS_FOLDER%\%PARAMS_FILE_NAME%.%RANDOM%
set PARAMS_FILE_TMP=%PARAMS_FILE%.tmp

if not exist %PARAMS_FOLDER% mkdir %PARAMS_FOLDER%

copy /y NUL %PARAMS_FILE_TMP% >NUL

for %%a in (%*) do (
	echo %%a>>%PARAMS_FILE_TMP%
)

move /Y %PARAMS_FILE_TMP% %PARAMS_FILE%>NUL

goto end

:launch

echo launch
goto end

:end