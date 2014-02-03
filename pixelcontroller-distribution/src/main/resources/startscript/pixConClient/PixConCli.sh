#!/bin/bash
#
# Copyright (C) 2011-2014 Michael Vogt <michu@neophob.com>
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


CURRENT=$(dirname "$0")
cd "$CURRENT"/../

java -Djava.security.policy=./sys/client.policy -classpath .:./lib/* com.neophob.sematrix.cli.PixConClient $@

if [ $# -eq 0 ]
then
 echo --------------------------------------------------------------------------
 echo Hint: This batch file can be used to control PixelController
 echo If you want to run PixelController, just doubleclick PixelController.jar!
 echo --------------------------------------------------------------------------
fi
