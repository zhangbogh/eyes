#!/bin/bash

JAVACMD="/home/work/local/software/java/jdk1.6.0_30/bin/java -Dfile.encoding=UTF-8 -Xmx2048m -jar kaimu.jar"
APPPATH="/home/work/kaimu"

cd $APPPATH

$JAVACMD $1 $2
exit $?