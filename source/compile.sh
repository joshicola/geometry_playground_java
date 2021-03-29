#!/bin/bash
javac GeoPlayground.java
jar cmf Manifest.txt Playground.jar *.class */*.class Messages*.properties
mv Playground.jar ../
rm *.class */*.class