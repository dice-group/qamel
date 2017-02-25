#!/bin/bash

wget http://www-nlp.stanford.edu/data/wordvecs/glove.6B.zip -O src/main/resources/glove.6B.zip
unzip src/main/resources/glove.6B.zip -d src/main/resources/glove.6B
