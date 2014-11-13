#!/bin/bash -e                                                                                       

source "$(dirname ${0})/../bin/config.sh"




# choose a name for the model to train
#model_name="mst_frame_id_20130625"
#model_name="ancestor_frame_id_20130626"
model_name="hmm_en__miter2_N64_nsent1613710_alpha1_batchsize1000_"
#model_name="hmm_en_rel_miter2_N64_nsent1613710_alpha1_batchsize1000_NMOD-PMOD-SUB"

# should set to roughly the number of cores available
num_threads=8
gc_threads=2  # garbage collection

classpath="${CLASSPATH}"
# the directory that contains framenet.frame.element.map and framenet.original.map
datadir="${SEMAFOR_HOME}/training/data"

# the directory the resulting model will end up in
model_dir="${datadir}/${model_name}"

#id_features="ancestor"
id_features="senna"

old_model_dir="${MALT_MODEL_DIR}"

fn_id_req_data_file="${model_dir}/reqData.jobj"


# paths to the gold-standard annotated sentences, and dependency-parsed version of it
training_dir="${datadir}/naacl2012_splits"
fe_file="${training_dir}/cv.train.sentences.frame.elements"
parsed_file="${training_dir}/cv.train.sentences.all.lemma.tags"
fe_file_length=`wc -l ${fe_file}`
fe_file_length=`expr ${fe_file_length% *}`

# path to store the alphabet we create:
alphabet_file="${model_dir}/alphabet.dat"

SCAN_DIR="${model_dir}/scan"

echo num_threads="${num_threads}"
echo gc_threads="${gc_threads}"
echo datadir="${datadir}"
echo id_features="${id_features}"
echo fn_id_req_data_file="${fn_id_req_data_file}"
echo training_dir="${training_dir}"
echo fe_file="${fe_file}"
echo parsed_file="${parsed_file}"
echo fe_file_length="${fe_file_length}"
echo alphabet_file="${alphabet_file}"
echo SCAN_DIR="${SCAN_DIR}"
