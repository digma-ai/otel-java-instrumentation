#!/bin/bash -e

version=$1

if [ -z "$version" ];
then
  echo "Version is empty, please call update_version.sh <wanted version>"
  exit 2
fi

sed -Ei "s/val theVersion = \"[^\"]*\"/val theVersion = \"$version\"/" version.gradle.kts
