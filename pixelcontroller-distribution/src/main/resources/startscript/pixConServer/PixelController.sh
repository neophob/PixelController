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


CURRENT=$(dirname "$0")/../
cd "$CURRENT"

# do not start jmx server on RPI
export JAVA_OPT="-Xmx512m -Djava.util.logging.config.file=./sys/logging.properties -Djava.library.path=./lib -Djava.security.policy=./sys/client.policy" 
java $JAVA_OPT -classpath .:./lib/* -XX:ErrorFile=./log/hs_err_pid%p.log com.neophob.sematrix.cli.PixConDaemon "$@"

