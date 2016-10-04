nano conll-eval.pl

# Train
crf_learn examples/template-example conll2002/esp.train modleExample

# Test
crf_test -m modleExample conll2002/esp.testa

# 5
crf_test -m modleExample conll2002/esp.testa > predicted.txt
perl conll-eval.pl -d "\t" < predicted.txt
