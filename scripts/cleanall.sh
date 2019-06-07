#!/bin/bash

./sbt -J-Xmx2G "project accumulo-store" clean  || { exit 1; }
./sbt -J-Xmx2G "project accumulo-spark" clean  || { exit 1; }
./sbt -J-Xmx2G "project cassandra-store" clean  || { exit 1; }
./sbt -J-Xmx2G "project geomesa" clean || { exit 1; }
./sbt -J-Xmx2G "project geotools" clean || { exit 1; }
./sbt -J-Xmx2G "project geowave" clean || { exit 1; }
./sbt -J-Xmx2G "project hbase-store" clean || { exit 1; }
./sbt -J-Xmx2G "project hbase-spark" clean || { exit 1; }
./sbt -J-Xmx2G "project proj4" clean || { exit 1; }
./sbt -J-Xmx2G "project s3" clean || { exit 1; }
./sbt -J-Xmx2G "project shapefile" clean || { exit 1; }
./sbt -J-Xmx2G "project spark-store" clean  || { exit 1; }
./sbt -J-Xmx2G "project spark-spark" clean  || { exit 1; }
./sbt -J-Xmx2G "project util" clean || { exit 1; }
./sbt -J-Xmx2G "project vector" clean || { exit 1; }
./sbt -J-Xmx2G "project vectortile" clean || { exit 1; }

rm -r accumulo-store/target
rm -r accumulo-spark/target
rm -r cassandra-store/target
rm -r cassandra-spark/target
rm -r geomesa/target
rm -r geotools/target
rm -r geowave/target
rm -r hbase-store/target
rm -r hbase-spark/target
rm -r macros/target
rm -r proj4/target
rm -r raster/target
rm -r raster-testkit/target
rm -r s3-store/target
rm -r s3-spark/target
rm -r shapefile/target
rm -r spark-testkit/target
rm -r spark/target
rm -r util/target
rm -r vector-testkit/target
rm -r vector/target
rm -r vectortile/target
