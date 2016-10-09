nano conll-eval.pl

# Train
# Usage: crf_learn -f 3 -c 1.5 template-file train_file model_file
crf_learn template_files/template conll2002/esp.train model_file

# for lab test
crf_learn template_files/template conll2002/esp.train models/model_file

# Test
# Usage: crf_test -m model_file test_files ...
crf_test -m models/model_file conll2002/esp.testa

crf_test -m models/model_file conll2002/esp.testb

# Print test results to a file
crf_test -m models/model_file conll2002/esp.testa > predicted.txt

# evaluate result of processing conll
perl conll-eval.pl -d "\t" < predicted.txt
