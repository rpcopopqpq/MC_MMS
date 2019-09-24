#!/bin/sh

javac  -d . -cp ./lib/* ./java/MNSDummyForGeocastingTest.java
chmod 755 MNSDummyForGeocastingTest.class
java -cp .:./lib/* MNSDummyForGeocastingTest $1



