# change the required files based on your files
idMappingFile=~/Downloads/idmapping_selected.tab.example
genomeAnnotation=~/Downloads/hg19.gtf.gz
referenceGenome=~/Downloads/GRCh37.primary_assembly.genome.fa.gz

# the example input, change if you want to use a custom file
inputVCF=example.vcf

java -jar ../build/libs/snpviz-0.0.1-all.jar -m $idMappingFile -g $genomeAnnotation -v $inputVCF -r $referenceGenome
