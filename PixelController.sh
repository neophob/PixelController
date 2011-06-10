#
# Copyright (C) 2011 Michael Vogt <michu@neophob.com>
#
# This file is part of PixelController.
#
# PixelController is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# PixelController is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with PixelController.  If not, see <http://www.gnu.org/licenses/>.
#

#/bin/bash

export JAVA_OPT="-Dcom.apple.hwaccel=false -Djava.library.path=lib/serial -server"
echo Java Options: $JAVA_OPT
java $JAVA_OPT -classpath bin:lib/core.jar:lib/ecj.jar:lib/pde.jar:lib/minim/library/jl1.0.jar:lib/minim/library/jsminim.jar:lib/minim/library/minim-spi.jar:lib/minim/library/minim.jar:lib/minim/library/tritonus_aos.jar:lib/minim/library/tritonus_share.jar:lib/blinkenlights.jar:lib/serial/RXTXcomm.jar:lib/serial/serial.jar:lib/net.jar:lib/commons-lang-2.5.jar:lib/commons-collections-3.2.1.jar:lib/library/jna.jar:lib/neorainbowduino.jar com.neophob.PixelController

