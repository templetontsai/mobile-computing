#!  /bin/bash

#   Script to build a cscope tags database of all of the java files that 
#   can be found within subdirectories of the current working directory.

find . -name *.java > cscope.files
cscope -b
