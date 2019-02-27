@echo off

CHCP 65001

if not exist %UserProfile%\.jatoo\imager\args\NUL mkdir %UserProfile%\.jatoo\imager\args

for /f "delims=/ tokens=1-3" %%a in ("%DATE:~4%") do (
	for /f "delims=:. tokens=1-4" %%m in ("%TIME: =0%") do (
		set FILENAME=%%c-%%b-%%a-%%m%%n%%o%%p-%RANDOM%
	)
)
	
echo %~1 > %UserProfile%\.jatoo\imager\args\%FILENAME%

del %UserProfile%\.jatoo\imager\lock.file
if not exist %UserProfile%\.jatoo\imager\lock.file (
	start javaw -cp %~dp0\lib\* jatoo.imager.JaTooImager %~1
)
