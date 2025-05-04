#!/bin/bash

# Script to generate MD5 and SHA1 checksum files for all .jar files
# in the current directory.

# Counter for processed files
count=0

cd target || exit

# Iterate over all files ending with .jar in the current directory
# Using a loop that handles filenames with spaces correctly.
shopt -s nullglob # Prevent loop from running if no *.jar files exist
for jar_file in *.jar *.pom; do
  # Check if it's a regular file (not a directory or link)
  if [[ -f "$jar_file" ]]; then
    echo "Processing: $jar_file"

    # --- Generate MD5 Checksum ---
    # Calculate MD5 checksum using md5sum
    # Extract only the checksum part (first field) using awk
    # Redirect the checksum to a file named <jar_filename>.md5
    md5sum "$jar_file" | awk '{ print $1 }' > "${jar_file}.md5"
    echo "  Generated: ${jar_file}.md5"

    # --- Generate SHA1 Checksum ---
    # Calculate SHA1 checksum using sha1sum
    # Extract only the checksum part (first field) using awk
    # Redirect the checksum to a file named <jar_filename>.sha1
    sha1sum "$jar_file" | awk '{ print $1 }' > "${jar_file}.sha1"
    echo "  Generated: ${jar_file}.sha1"

    # Increment counter
    ((count++))
  fi
done

# Check if any JAR files were processed
if [[ $count -eq 0 ]]; then
  echo "No .jar files found in the current directory."
else
  echo "Finished processing $count JAR and POM file(s)."
fi

# Note:
# If 'md5sum' or 'sha1sum' are not available (e.g., on some macOS versions by default),
# you might need to install them (e.g., 'brew install coreutils' on macOS)
# or use 'openssl' instead:
#
# For MD5:
# openssl md5 "$jar_file" | awk '{ print $2 }' > "${jar_file}.md5"
#
# For SHA1:
# openssl sha1 "$jar_file" | awk '{ print $2 }' > "${jar_file}.sha1"
# (Note the use of awk '{ print $2 }' because openssl output format is different)

exit 0
