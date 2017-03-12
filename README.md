[![Build Status](https://travis-ci.org/dnltsk/yet-another-cru-ts-transformer.svg?branch=master)](https://travis-ci.org/dnltsk/yet-another-cru-ts-transformer) [![codebeat badge](https://codebeat.co/badges/8186fa48-f6cf-42b2-a7f6-61b4a0fc6df5)](https://codebeat.co/projects/github-com-dnltsk-yet-another-cru-ts-transformer-master) [![codecov](https://codecov.io/gh/dnltsk/yet-another-cru-ts-transformer/branch/master/graph/badge.svg)](https://codecov.io/gh/dnltsk/yet-another-cru-ts-transformer)

# Yet Another CRU TS Transformer
Command Line Interface (CLI) to loads a Climatic Research Unit Timeseries (CRU TS) file into a database table.

## requirements
* Java 8
* Maven 3.3.x
* SQLite (to work with the resulting database)

## build
* `mvn clean test package`

## run
* `java -jar target/yet-another-cru-ts-transformer.jar sample-cru-ts-file.pre`
  * creates SQLite db in file `cru.ts.sqlite`
* `sqlite3 -header -column cru-ts.sqlite 'select * from CRU_TS_TABLE LIMIT 10'`
  * to check the resulting database


## references

* CRU TS 2.1 Standard<br>
https://crudata.uea.ac.uk/~timm/grid/CRU_TS_2_1.html