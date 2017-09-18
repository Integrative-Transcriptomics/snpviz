# SNPViz - Visualization of SNPs in proteins


## Dependencies:

- jdk8+
- gradle

## needed additional information:

### ID mapping of different gene IDs:

ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/idmapping_selected.tab.gz

For the example, the smaller file will suffice:

ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/idmapping_selected.tab.example

## Human reference genome

You need to donload the human reference genome that you want to use. Keep in mind that the next file has to correspond to your reference file.

### genome annotation of the human genome:

On the following Website you need to select the desired human release ans select "Track" to "TransMapEnsembl"!!! In order to download the file, you need to give a filename in the "output file" field

http://genome.ucsc.edu/cgi-bin/hgTables

## Compilation

To compile the program type

`gradle build shadowJar`

The executable jar file lies in the build/libs folder and ends with -all.jar.

## Parameters:
 - -g,--gtf <arg>      the gtf file
 - -h,--help           show this help page
 - -k,--keep           keep reference in Memory
 - -m,--idmap <arg>    the id mapping file
 - -o,--output <arg>   output vcf file [<INPUT>_modified.vcf]
 - -r,--ref <arg>      the fasta reference file
 - -s,--spark          run with apacheSpark parallelization
 - -v,--vcf <arg>      the input vcf file to analyze

 the parameters m, g, v, and r are required
 
 the id mapping file (-m) corresponds to the file downloaded from uniprot
 
 The gtf file (-g) corresponds to the file downloaded from ucsc
 
 The vcf file (-v) corresponds to the desired vcf file that you created
 
 The reference file (-r) corresponds to the genome reference you want to use (e.g. hg19).