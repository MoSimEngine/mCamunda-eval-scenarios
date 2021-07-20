#!/bin/sh
#1 = where to search
#2 = before or after
#3 = scenario number

mkdir -p "scenario$3/before"
mkdir -p "scenario$3/after"
mkdir -p "scenario$3/lang-apply"

cat "input/scenario$3_$2_input.txt" | sed 's@.*/@@' > input.txt
while IFS= read -r line
do
    find $1 -name "$line" -print0 | while read -d $'\0' file
        do
            #echo "$file"
            cp "$file" "scenario$3/$2"
        done
done < input.txt
rm input.txt
