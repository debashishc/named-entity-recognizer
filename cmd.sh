nano conll-eval.pl

# Train
# Usage: crf_learn -f 3 -c 1.5 template-file train_file model_file
crf_learn template_files/template conll2002/esp.train model_file

# for lab test
crf_learn template_files/template conll2002/esp.train models/model_file

# USE THIS
crf_learn -c 1.5 template_files/template conll2002/esp.train models/model_file_final

# Test
# Usage: crf_test -m model_file test_files ...
crf_test -m models/model_file conll2002/esp.testa

crf_test -m models/model_file conll2002/esp.testb

# Print test results to a file
crf_test -m models/model_file conll2002/esp.testa > predicted.txt

## USE THIS
crf_test -m models/model_file_testSet conll2002/esp.testb > testb_results/predicted.txt




crf_learn template_files/template_final test_files_final/labeled_test.txt > test_files_final/test_result.txt

# evaluate result of processing conll
perl conll-eval.pl -d "\t" < predicted.txt


## USE THIS
perl conll-eval.pl -d "\t" < testb_results/predicted.txt