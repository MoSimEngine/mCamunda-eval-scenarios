#!/bin/sh
#1 = files to find
#2 = where to search
#3 = where to copy
#4 = scenario number

mkdir -p "scenario$4/before"
mkdir -p "scenario$4/after"

cat $1 | sed 's@.*/@@' > input.txt
while IFS= read -r line
do
    find $2 -name "$line" -print0 | while read -d $'\0' file
        do
            #echo "$file"
            cp "$file" $3
        done
done < input.txt
rm input.txt
