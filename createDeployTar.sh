#!/bin/bash

# Script to organize and archive opencsv files based on version.

# --- Configuration ---
PREFIX="opencsv-" # Prefix of the files to look for

cd target || exit

# --- Find Files and Extract Version ---
echo "Looking for files starting with '$PREFIX'..."

# Find the first file matching the pattern to extract the version
# Use a loop to handle potential errors and get the first match
first_file=""
for f in ${PREFIX}*; do
  # Check if it's a regular file before proceeding
  if [[ -f "$f" ]]; then
    first_file="$f"
    break # Found the first file, exit loop
  fi
done

# Check if any file was found
if [[ -z "$first_file" ]]; then
  echo "Error: No files found starting with '$PREFIX' in the current directory."
  exit 1
fi

echo "Found file: $first_file"

# Extract version number
# Remove prefix: temp=${first_file#$PREFIX} -> e.g., 5.11.jar or 5.11-sources.jar
# Remove suffix starting from the first non-digit/non-dot character after the prefix
# This handles versions like 5.11, 1.2.3 etc. and suffixes like .jar, -sources.jar
temp_version=${first_file#$PREFIX}
version=$(echo "$temp_version" | sed 's/[^0-9.].*//') # Keep only leading digits and dots

# Validate extracted version
if [[ -z "$version" ]]; then
  echo "Error: Could not extract version number from '$first_file'."
  exit 1
fi

echo "Extracted version: $version"

# --- Define Paths ---
TARGET_SUBDIR="com/opencsv/$version"
TAR_FILENAME="${PREFIX}${version}.tar"
FILE_PATTERN="${PREFIX}${version}*" # Pattern to match all files for this version

# --- Create Directory ---
echo "Creating directory: $TARGET_SUBDIR"
mkdir -p "$TARGET_SUBDIR"
if [[ $? -ne 0 ]]; then
    echo "Error: Failed to create directory '$TARGET_SUBDIR'."
    exit 1
fi

# --- Copy Files ---
echo "Copying files matching '$FILE_PATTERN' to '$TARGET_SUBDIR'..."
cp -v $FILE_PATTERN "$TARGET_SUBDIR/"
if [[ $? -ne 0 ]]; then
    echo "Error: Failed to copy files."
    # Optional: Clean up created directory on copy failure
    # rm -rf "com"
    exit 1
fi

# --- Create Tar Archive ---
echo "Creating tar archive: $TAR_FILENAME"
# Create the tar file containing the 'com' directory and its contents
tar cvf "$TAR_FILENAME" com
if [[ $? -ne 0 ]]; then
    echo "Error: Failed to create tar file '$TAR_FILENAME'."
    # Optional: Clean up created directory and copied files on tar failure
    # rm -rf "com"
    exit 1
fi

echo "-------------------------------------"
echo "Success!"
echo "  - Files copied to: $TARGET_SUBDIR"
echo "  - Archive created: $TAR_FILENAME"
echo "-------------------------------------"

# Optional: Clean up the 'com' directory after creating the tarball
# echo "Cleaning up temporary directory 'com'..."
# rm -rf com

exit 0
