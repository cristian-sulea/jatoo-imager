@echo off

if not exist %UserProfile%\.jatoo\.imager\images\NUL mkdir %UserProfile%\.jatoo\.imager\images

for /f "delims=/ tokens=1-3" %%a in ("%DATE:~4%") do (
	for /f "delims=:. tokens=1-4" %%m in ("%TIME: =0%") do (
		set FILENAME=%%c-%%b-%%a-%%m%%n%%o%%p-%RANDOM%
	)
)
	
echo %~1 > %UserProfile%\.jatoo\.imager\images\%FILENAME%
