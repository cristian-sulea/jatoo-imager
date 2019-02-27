@echo off

REG ADD HKEY_CURRENT_USER\Software\Classes\JaTooImager /v "" /d "JaToo Imager File" /f
REG ADD HKEY_CURRENT_USER\Software\Classes\JaTooImager\DefaultIcon /v "" /d "%CD%\icon.ico" /f
REG ADD HKEY_CURRENT_USER\Software\Classes\JaTooImager\shell\open\command /v "" /d "\"%CD%\JaToo Imager.bat\" \"%%1\"" /f

REG ADD HKEY_CURRENT_USER\Software\Classes\.jatoo-image /v "" /d "JaTooImager" /f
REG ADD HKEY_CURRENT_USER\Software\Classes\.jpeg /v "" /d "JaTooImager" /f
