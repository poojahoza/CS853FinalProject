#!/bin/bash
#@Author- Amith RC


function usage()
{
	echo "Argument1=Absolute path to the Benchmark-y1  TEST OUTLINE CBOR file"
	echo "Argument2=Absolute path to the Benchmark-y1  TEST QREL file(AKA Ground Truth)"
	echo "Argument3=Absolute path to the Benchmark-y1  TRAIN QREL file(AKA Ground Truth"
	echo "Argument4=Absolute path to the Benchmark-y1  TRAIN OUTLINE CBOR file"
	echo ""
	exit
}

function call_maven()
{
echo "Executing the Maven for the dependency"
mvn compile
mvn package
sleep 4
}

function change_path_target()
{
	echo "************CS853-Team3***********************************"
	echo ""
	echo "TEST OUTLINE CBOR file=$1"
	echo "TEST QREL file(AKA Ground Truth)=$2"
	echo "TRAIN QREL file(AKA Ground Truth=$3"
	echo "TRAIN OUTLINE CBOR file=$4"
	echo ""
	echo "**********************************************************"
	pwdCurrent=$(pwd)
	pwdCurrent=$pwdCurrent/target/CS853project-1.0-SNAPSHOT-jar-with-dependencies.jar
	java -jar $pwdCurrent $1 $2 $3 $4
}

if [ $# -eq 0 ]
then 
	call_maven 
	change_path_target /home/team3/benchmarkY1/benchmarkY1-test/test.pages.cbor-outlines.cbor /home/team3/benchmarkY1/benchmarkY1-test/test.pages.cbor-article.qrels  /home/team3/benchmarkY1/benchmarkY1-test/train.pages.cbor-article.qrels  /home/team3/benchmarkY1/benchmarkY1-test/train.pages.cbor-outlines.cbor
elif [ $# -ne 4 ]
then 
	usage
else

	echo " "
	echo "************CS853-Team3**************************"
	echo "TEST OUTLINE CBOR file=$1"
	echo "TEST QREL file(AKA Ground Truth)=$2"
	echo "TRAIN QREL file(AKA Ground Truth=$3"
	echo "TRAIN OUTLINE CBOR file=$4"
	echo " "
	call_maven
	change_path_target $1 $2 $3 $4
fi
