
This repository contains the source code of the library used as a back-end for the Watson Semantic Web Search Engine (see http://watson.kmi.open.ac.uk), based on lucene indexes created using the Watson indexer (see https://github.com/mdaquin/Watson-Service-API).

To build the library, use:

$ ant clean
$ ant build

There is a small test available, tht simply list the URIs of indexed ontologies. In order to configure the library, you need to indicate where to find the indexes. You can do it by either indicating a directory where to find them in the watson_dir.conf file, or list them in the watson.conf file (the watson_dir.conf file takes priority).

Running 

$ ant test 

should load the indexes and list the URIs of ontologies in them
