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

set JAVA_JMX_OPT=-Dcom.sun.management.jmxremote.port=1337 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=127.0.0.1 -Dcom.sun.management.jmxremote
java %JAVA_JMX_OPT% -Djava.security.policy=sys/client.policy -Djava.util.logging.config.file=sys/logging.properties -Djava.library.path=lib -Xmx512m -classpath ".;./lib/*" com.neophob.sematrix.cli.PixConDaemon %*
