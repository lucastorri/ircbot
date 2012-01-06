#!/bin/bash

cd `dirname "$0"`
rm -rf dist
sbt assembly
if [ $? -ne 0 ]
then
    exit 1
fi

mkdir -p dist/{bin,lib}
SOURCE_JAR=`ls target/*-assembly-*.jar`
DEST_JAR=`echo $SOURCE_JAR | sed 's/target\/\(.*\)-assembly.*/\1.jar/'`
DIST_ZIP=`echo $SOURCE_JAR | sed 's/target\/\(.*\)-assembly-\([0-9\.]*\)\.jar/\1-\2.zip/'`
cp $SOURCE_JAR dist/lib/$DEST_JAR
cp bin/* dist/bin/
zip -r dist/$DIST_ZIP dist/*