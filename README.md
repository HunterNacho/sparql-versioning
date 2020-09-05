# sparql-versioning
Collection of utility scripts for data processing.
Files are expected to have a filename in the format of "wikidata-YYYYMMDD-truthy-BETA.nt.gz", but the code can be adapted as needed.
Each script assumes the triples in each file are sorted beforehand.

For delta construction:

> javac DiffCalc.java

> java DiffCalc <file_1> <file_2>

For interval construction:
> javac IntervalGraphBuilder.java

> java IntervalGraphBuilder

Execute in the folder containing the dumps. Adjust the versions in the Constants.java file.
