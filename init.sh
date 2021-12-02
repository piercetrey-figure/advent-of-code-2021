#! /bin/bash

day=$1

if [ ${#day} -eq 1 ]; then
  day="0$day"
fi

if [ -f "src/Day$day.kt" ]; then
  echo "Day$day.kt already exists, exiting"
  exit 1
fi

echo "Creating Day$day files"

sed "s/<day>/$day/" "src/Template.kt" > "src/Day$day.kt"
touch "src/Day$day.txt"
touch "src/Day${day}_test.txt"
