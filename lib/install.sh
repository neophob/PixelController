mvn install:install-file -Dfile=./blinkenlights.jar -DgroupId=neophob.com -DartifactId=processing-blinkenlight -Dversion=0.5 -Dpackaging=jar
mvn install:install-file -Dfile=./neorainbowduino.jar -DgroupId=neophob.com -DartifactId=neorainbowduino -Dversion=0.82 -Dpackaging=jar

mvn install:install-file -Dfile=./core.jar -DgroupId=processing.org -DartifactId=core -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./ecj.jar -DgroupId=processing.org -DartifactId=ecj -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./net.jar -DgroupId=processing.org -DartifactId=net -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./pde.jar -DgroupId=processing.org -DartifactId=pde -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./jna.jar -DgroupId=processing.org -DartifactId=jna -Dversion=1.0 -Dpackaging=jar

mvn install:install-file -Dfile=./serial.jar -DgroupId=processing.org -DartifactId=serial -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./RXTXcomm.jar -DgroupId=qbang.org -DartifactId=rxtx -Dversion=2.2pre5 -Dpackaging=jar

mvn install:install-file -Dfile=./minim.jar -DgroupId=compartmental.net -DartifactId=minim -Dversion=2.0.2 -Dpackaging=jar
