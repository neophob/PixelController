@REM
@REM Copyright (C) 2011 Michael Vogt <michu@neophob.com>
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

set JAVA_OPT=-Dsun.java2d.opengl=True -Djava.library.path=lib\serial

java %JAVA_OPT% -classpath bin;lib\core.jar;lib\minim\library\jl1.0.jar;lib\minim\library\jsminim.jar;lib\minim\library\minim-spi.jar;lib\minim\library\minim.jar;lib\minim\library\tritonus_aos.jar;lib\minim\library\tritonus_share.jar;lib\blinkenlights.jar;lib\serial\RXTXcomm.jar;lib\serial\serial.jar;lib\net.jar;lib\commons-lang-2.5.jar;lib\commons-collections-3.2.1.jar;lib\neorainbowduino.jar com.neophob.PixelController