#!/bin/bash

# train the frameId model

set -e # fail fast

my_dir="$(dirname ${0})"
echo my_dir=${my_dir}
source "${my_dir}/config.sh"
mkdir -p "${model_dir}"
cp "${my_dir}/../src/main/java/edu/cmu/cs/lti/ark/fn/identification/Senna.java" "${model_dir}/Senna.java"
cp "${my_dir}/config.sh" "${model_dir}/trainconfig.sh"
cp "${my_dir}/../bin/config.sh" "${model_dir}/binconfig.sh"

${my_dir}/2_createRequiredData.sh
${my_dir}/3_1_idCreateAlphabet.sh
${my_dir}/3_2_idCreateFeatureEvents.sh
# this step often throws an error at the end of LBFGS, even though it's successful
${my_dir}/3_3_idTrainBatch.sh || true
${my_dir}/3_4_idConvertAlphabetFile.sh

