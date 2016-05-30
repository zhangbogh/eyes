#!/usr/bin/env bash
JAVA_HOME=$JAVA_HOME_1_6
export JAVA_HOME

ant

tar -zcvf ./kaimu.tar.gz ./kaimu

mkdir output
cp ./kaimu.tar.gz output