#!/bin/bash
while getopts i:f:n: flag
do
    case "${flag}" in
        i) initialDirectory=${OPTARG};;
		#addons vpk archive directory
        f) finalDirectory=${OPTARG};;
		#left4manager directory
		n) fileCode=${OPTARG};;
		#name of the vpk
    esac
done
filePath="$initialDirectory"\\"$fileCode".vpk;
cp "$filePath" "$finalDirectory"\\temp;
filePath="$initialDirectory"\\"$fileCode".jpg;
cp "$filePath" "$finalDirectory"\\temp;
#copy the vpk archive and the thumbnail img to \left4manager\temp
cd "$finalDirectory"\\vpk;
#go to the vpk files in left4manager folder
filePath="$finalDirectory"\\temp\\"$fileCode".vpk;
#complete path of the copied vpk archive
#./vpk "$filePath";

./vpk x "$filePath" addoninfo.txt;
#execute vpk command (./ required to force the use of the relative path and not the enviromental one)

cd "$finalDirectory"\\vpk;
filePath="$finalDirectory"\\temp\\"$fileCode";

if [ -f addoninfo.txt ]; then
	mv addoninfo.txt "$filePath".txt;
	#rename and move file
else 
    echo "file does not exist."
fi

rm "$filePath".vpk;
#delete vpk archive


#./vpk x "$filePath" addoninfo.txt;
#extract only addoninfo.txt
#./vpk "$filePath";
#extract all