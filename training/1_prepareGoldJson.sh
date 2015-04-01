#!/bin/bash -e

# Convert from the *.frame.elements and *.tokenized files to the Json format

source "/home/p262594/Apps/semafor/bin/config.sh"

# preprocessing
fefile=cv.test.sentences.frame.elements
tokenizedfile=cv.test.sentences.tokenized
processedfile=cv.test.sentences.all.lemma.tags
#cat ${fefile} | awk '{print "0""\t"$0}' > cv.test0.sentences.frame.elements
end=`wc -l ${tokenizedfile}`
end=`expr ${end% *}`
echo ${end}
#conversion
java -classpath ${CLASSPATH} -Xms1000m -Xmx1000m \
edu.cmu.cs.lti.ark.fn.evaluation.PrepareFullAnnotationXML \
testFEPredictionsFile:${fefile} \
startIndex:0 \
endIndex:${end} \
testParseFile:${processedfile} \
testTokenizedFile:${tokenizedfile} \
outputFile:cv.test.sentences.recreated.xml


