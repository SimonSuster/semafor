#!/bin/sh
######################## ENVIRONMENT VARIABLES ###############################
######### change the following according to your own local setup #############

# assumes this script (config.sh) lives in "${BASE_DIR}/semafor/bin/"
#export BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." > /dev/null && pwd )"
# path to the absolute path
# where you decompressed SEMAFOR.
#SEMAFOR_HOME="${BASE_DIR}/semafor"
export SEMAFOR_HOME="/home/p262594/Apps/semafor_dev"
#export CLASSPATH=".:${SEMAFOR_HOME}/target/Semafor-3.0-alpha-04.jar"
#export CLASSPATH=".:${SEMAFOR_HOME}/../Semafor_dev/out/artifacts/Semafor_jar/Semafor.jar"
export CLASSPATH=".:${SEMAFOR_HOME}/target/Semafor-3.0-alpha-04.jar"

# Change the following to the bin directory of your $JAVA_HOME
export JAVA_HOME_BIN="/usr/bin"

# Change the following to the directory where you decompressed 
# the models for SEMAFOR 2.0.
#MALT_MODEL_DIR="${BASE_DIR}/models/semafor_malt_model_20121129"

#export MALT_MODEL_DIR="${SEMAFOR_HOME}/semafor_malt_model_20121129"
#MALT_MODEL_DIR="${SEMAFOR_HOME}/training/data/hmm_en_tree_miter2_N64_nsent1613710_alpha1_batchsize1000_"
#MALT_MODEL_DIR="${SEMAFOR_HOME}/training/data/hmm_en__miter2_N64_nsent1613710_alpha1_batchsize1000_"
export MALT_MODEL_DIR="/home/p262594/Apps/semafor/training/data/retrain_simple"
#TURBO_MODEL_DIR="{BASE_DIR}/models/turbo_20130606"



######################## END ENVIRONMENT VARIABLES #########################

echo "Environment variables:"
echo "SEMAFOR_HOME=${SEMAFOR_HOME}"
echo "CLASSPATH=${CLASSPATH}"
echo "JAVA_HOME_BIN=${JAVA_HOME_BIN}"
echo "MALT_MODEL_DIR=${MALT_MODEL_DIR}"




