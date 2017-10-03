#!/bin/bash
for i in {1..15}
do
	mvn clean test -Dtest=TimeExample1
	mvn clean test -Dtest=TimeExample2
done
