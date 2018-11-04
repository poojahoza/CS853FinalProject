#!/bin/bash
#@Author- Amith RC


function usage()
{
	echo "Argument1=Absolute path to the Paragraph CBOR file"
	echo "Argument2=Absolute path to the Outline CBOR file"
	echo "Argument3 =Absolute path to the article Qrel file"	
	echo ""
	exit
}

function call_maven()
{
echo "Executing the Maven for the dependency"
mvn clean compile
mvn package
sleep 2
}

function change_path_target()
{
	pwdCurrent=$(pwd)
	pwdCurrent=$pwdCurrent/target/CS853project-1.0-SNAPSHOT-jar-with-dependencies.jar
	java -jar $pwdCurrent $1 $2 $3
}

if [ $# -ne 3 ]
then 
	usage
else
	echo " "
	echo "************CS853-Team3**************************"
	echo "Paragraph File passed=$1"
	echo "Outline File passed=$2"
	echo "Qrel FIle passed=$3"
	echo " "
	call_maven
	change_path_target $1 $2 $3
fi
