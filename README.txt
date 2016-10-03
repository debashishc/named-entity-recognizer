DOCUMENT ANALYSIS COMP4650/6490
INFORMATION EXTRACTION ASSIGNMENT
Assigment: Named Entity Recognition (NER) using CRF++
________________________________________________________


FILES
-----

conll2002	directory containing data sets for training and testing

CRF++	directory containing the CRF++ tool and documentation

model	directory containing a POS-tagger model for Spanish 

eval.pl	script to evaluate the performance of CRF++ 

examples 	directory containing examples (CRF template and "Named Entity Recognition output format" )

README.txt	this help file


________________________________________________________
Spanish Named Entity Recognition (NER) using CRF++
________________________________________________________

1. Installed CRF++ following the instructions found in the CRF++/doc folder. If you want to install CRF++ in the lab computers or W$ndows, please, see instructions below.

2. Built a feature template file for the CRF++ tool. For an example see ~/examples/template-example file

3. Train NER using the training set ('esp.train') in the ~/conll2002/ folder. 
   
4. Test the classifier built in (3) using the test set ('esp.testb') in the ~/conll2002/ folder

5. Evaluate the performance of the classifier using the conll-eval.pl (you can find an example of the command to run the script in the script itself)

6. Repeat steps 2,3,4 and 5 until you have a decent performance (more than 70 f-score for each entity class, except for the MISC class). 
   Use the 'esp.testa' file as development set, thus to tune your classifier.
   You will demostrate the best performance you can get in the grading lab by runnning the conll-eval.pl script with the 'esp.testb' file 

7. Write a Java NER application that uses your best classifier from step 6. Prepare your application to be test by a new test set (row/plain text), that will be provided in the grading lab. Your application should use the code available to built and NLP pipeline that reads row/plain input text > split the sentences > tokenized the text > get the POS-tags (you can use the NLP code and the Spanish POS-tagger model in the \textit{Information Extraction - Lab & Assignment resources IE-Lab:NLPTools} posted to Wattle). 

8. Your NER application must include a NE extractor that display the recognized entities in the one NE per line, organized by named entities categories, and display the frequency of each entity found (see the example/NE-ExtractorFormat.txt file) 

9. Run your NER application with the test set provided in the grading lab. This test set will be row text. Thus, your NER application should be able to process the row text and  output the format necessary to use by CRF++ (which is the same format as the esp.testa file, but without the entity tags at the 3rd column) since in this step, we are not going to evaluate the performance of your model, but your ability to process new data with your NER model.


** Note that the training and testing can be done in the command line, it is not mandatory to have a code that run all the process together (format the input > get the features (e.g. POSTag, etc.) > test with crf++model > format results).

________________________________________________________
Installing the CRF++ tool in the lab computers
________________________________________________________

1. Go to place where you have downloaded CRF++

2. type ./configure

3. type make uninstall

4. type make

5. type cd ~

6. gedit .profile

7. in this file write

export PATH="path where you have downloaded CRF++(for example ~/Downloads/IE-Lab/CRF++):$PATH"

8. save this file and exit

9. source .profile

Now crf_learn and crf_test commands should be working from terminal.

Hope it Helps 
Swapnil

________________________________________________________
Installing the CRF++ on W$ndows
________________________________________________________
If you are using W$ndows on your laptops, you can't make (compile) CRF++. 
Please use this pre-built binaries in ~/IE-Lab/crf++_binaries-windows.zip













