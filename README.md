# SNPViz - Visualization of SNPs in proteins


## Dependencies:

- jdk8+
- gradle

## needed additional information:

### ID mapping of different gene IDs:

ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/idmapping_selected.tab.gz

For the example, the smaller file will suffice:

ftp://ftp.uniprot.org/pub/databases/uniprot/current_release/knowledgebase/idmapping/idmapping_selected.tab.example

### genome annotation of the human genome:

On the following Website you need to select the desired human release ans select "Track" to "TransMapEnsembl"!!! In order to download the file, you need to give a filename in the "output file" field

http://genome.ucsc.edu/cgi-bin/hgTables

## Compilation

To compile the program type

`gradle build shadowJar`
