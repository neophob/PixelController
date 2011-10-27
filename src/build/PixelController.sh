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

#!/bin/bash

#TODO add cd into current dir...

export JAVA_OPT="-Djava.util.logging.config.file=data/logging.properties -Djava.library.path=lib -Dcom.sun.management.jmxremote.port=1337 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Djava.rmi.server.hostname=127.0.0.1 -Dcom.sun.management.jmxremote -Djava.security.policy=data/client.policy" 
source ./classpath-unix.properties
echo classpath: $classpath
java $JAVA_OPT -classpath $classpath:./lib/PixelController.jar com.neophob.PixelController
