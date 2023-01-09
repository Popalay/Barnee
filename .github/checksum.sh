#!/bin/bash
#
# Copyright (c) 2023 Denys Nykyforov
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.
#

RESULT_FILE=$1

if [ -f $RESULT_FILE ]; then
  rm $RESULT_FILE
fi
touch $RESULT_FILE

checksum_file() {
  echo $(openssl md5 $1 | awk '{print $2}')
}

FILES=()
while read -r -d ''; do
	FILES+=("$REPLY")
done < <(find . -type f \( -name "build.gradle*" -o -name "*versions.toml" -o -name "gradle-wrapper.properties" \) -print0)

# Loop through files and append MD5 to result file
for FILE in ${FILES[@]}; do
	echo $(checksum_file $FILE) >> $RESULT_FILE
done
# Now sort the file so that it is
sort $RESULT_FILE -o $RESULT_FILE
