nano conll-eval.pl

# Train
# Usage: crf_learn -f 3 -c 1.5 template-file train_file model_file
crf_learn examples/template-example conll2002/esp.train model_file

# Test
# Usage: crf_test -m model_file test_files ...
crf_test -m modleExample conll2002/esp.testa

# 5
crf_test -m modleExample conll2002/esp.testa > predicted.txt
perl conll-eval.pl -d "\t" < predicted.txt
