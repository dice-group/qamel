#!/bin/bash

wget http://downloads.dbpedia.org/2016-04/core-i18n/en/labels_en.ttl.bz2 -O src/main/resources/labels_en.ttl.bz2
wget http://downloads.dbpedia.org/2016-04/core-i18n/en/redirects_en.ttl.bz2 -O src/main/resources/redirects_en.ttl.bz2

bunzip2 src/main/resources/redirects_en.ttl.bz2
bunzip2 src/main/resources/labels_en.ttl.bz2

