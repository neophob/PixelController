@REM
@REM Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
@REM
@REM This file is part of PixelController.
@REM
@REM PixelController is free software: you can redistribute it and/or modify
@REM it under the terms of the GNU General Public License as published by
@REM the Free Software Foundation, either version 3 of the License, or
@REM (at your option) any later version.
@REM
@REM PixelController is distributed in the hope that it will be useful,
@REM but WITHOUT ANY WARRANTY; without even the implied warranty of
@REM MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
@REM GNU General Public License for more details.
@REM
@REM You should have received a copy of the GNU General Public License
@REM along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
@REM

@echo off
setlocal

set BINDIR=%~dp0
cd /D "%BINDIR%"\..\

java -Djava.security.policy=./sys/client.policy -classpath ".;./lib/*" com.neophob.sematrix.cli.PixConClient %*

IF "%1"=="" GOTO SHOWHINT

endlocal
goto:eof


:SHOWHINT
echo --------------------------------------------------------------------------
echo Hint: This batch file can be used to control PixelController
echo If you want to run PixelController, just doubleclick PixelController.jar!
echo --------------------------------------------------------------------------
pause
