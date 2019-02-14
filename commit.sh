#!/bin/bash

if [ $# -eq 1 ]; then
	git add .
	git commit -m $1
	git push -u origin master
else
	echo "Not commit description"
fi
