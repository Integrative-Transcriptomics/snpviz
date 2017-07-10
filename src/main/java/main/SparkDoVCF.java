package main;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import datastructures.ExonMapping;
import datastructures.MappingFile;
import datastructures.VCFEntry;

public class SparkDoVCF {
	
	List<VCFEntry> vcfEntries = new LinkedList<VCFEntry>();
	
	public SparkDoVCF(String file, String gtfFile, MappingFile mappingFile, String reference){
		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(SparkDoVCF.class.getSimpleName());
		@SuppressWarnings("resource")
		JavaSparkContext sc = new JavaSparkContext(conf);
		JavaRDD<String> rdd = sc.textFile(new File(file).getAbsolutePath());
		rdd = rdd.filter(s -> !s.startsWith("#"));
		JavaRDD<String[]> splittedRdd = rdd.map(s -> s.split("\t"));
		JavaRDD<VCFEntry> vcfRdd = splittedRdd.map(f -> new VCFEntry(f[0], Integer.parseInt(f[1]), f[3], f[4], new HashSet<String>()));
		JavaRDD<VCFEntry> updatedVcfRdd = vcfRdd.map(f -> new ExonMapping(gtfFile, f, mappingFile, reference).getVCFEntry());
//		vcfRdd.foreach(i -> new ExonMapping(gtfFile, i, mappingFile, reference));
		this.vcfEntries = updatedVcfRdd.collect();
		System.out.println("finished");
		
	}
	
	public List<VCFEntry> getVCFEntries(){
		return this.vcfEntries;
	}
	
}
