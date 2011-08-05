#########################
# most packages are not available on maven repos. this means we need to install them on
# our local repo 

mvn install:install-file -Dfile=./blinkenlights.jar -DgroupId=neophob.com -DartifactId=processing-blinkenlight -Dversion=0.5 -Dpackaging=jar
mvn install:install-file -Dfile=./neorainbowduino.jar -DgroupId=neophob.com -DartifactId=neorainbowduino -Dversion=0.82 -Dpackaging=jar

mvn install:install-file -Dfile=./core.jar -DgroupId=processing.org -DartifactId=core -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./ecj.jar -DgroupId=processing.org -DartifactId=ecj -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./net.jar -DgroupId=processing.org -DartifactId=net -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./pde.jar -DgroupId=processing.org -DartifactId=pde -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./jna.jar -DgroupId=processing.org -DartifactId=jna -Dversion=1.0 -Dpackaging=jar

cd serial
mvn install:install-file -Dfile=./serial.jar -DgroupId=processing.org -DartifactId=serial -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=./RXTXcomm.jar -DgroupId=qbang.org -DartifactId=rxtx -Dversion=2.2pre5 -Dpackaging=jar

cd ..
cd minim
mvn install:install-file -Dfile=./minim.jar -DgroupId=compartmental.net -DartifactId=minim -Dversion=2.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=./jsminim.jar -DgroupId=compartmental.net -DartifactId=jsminim -Dversion=2.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=./minim-spi.jar -DgroupId=compartmental.net -DartifactId=minim-spi -Dversion=2.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=./tritonus_aos.jar -DgroupId=compartmental.net -DartifactId=tritonus_aos -Dversion=2.0.2 -Dpackaging=jar
mvn install:install-file -Dfile=./tritonus_share.jar -DgroupId=compartmental.net -DartifactId=tritonus_share -Dversion=2.0.2 -Dpackaging=jar

cd ..