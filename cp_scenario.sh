#!/bin/sh
#1 = files to find
#2 = where to search
#3 = where to copy
#4 = scenario number

#1 = where to search
#2 = before or after
#3 = scenario number

# ./cp_scenario.sh input/scenario18_before_input.txt ../camunda-bpm-platform scenario18/before 18

# ./cp_scenario.sh ../camunda-bpm-platform after 18

mkdir -p "scenario$3/before"
mkdir -p "scenario$3/after"

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
