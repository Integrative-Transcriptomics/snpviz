package main;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import datastructures.ExonMapping;
import datastructures.MappingFile;
import datastructures.VCFEntry;

public class SparkDoVCF implements Serializable {
	
	private static final long serialVersionUID = 5749468430075726305L;
	private List<VCFEntry> vcfEntries = new LinkedList<VCFEntry>();
	
	@SuppressWarnings("resource")
	public SparkDoVCF(String file, String gtfFile, MappingFile mappingFile, String reference, Boolean keepRefInMemory){
		SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(SparkDoVCF.class.getSimpleName());
		ExonMapping em = new ExonMapping(gtfFile, mappingFile, reference, keepRefInMemory);
		this.vcfEntries = new JavaSparkContext(conf).textFile(new File(file).getAbsolutePath())
				.filter(s -> !s.startsWith("#"))
				.map(s -> s.split("\t"))
				.map(f -> new VCFEntry(f[0], Integer.parseInt(f[1]), f[3], f[4], new HashSet<String>()))
				.map(f -> em.analyzeEntry(f))
				.filter(new Function<VCFEntry, Boolean>(){
					private static final long serialVersionUID = 753333851848380013L;
					@Override
					public Boolean call(VCFEntry v1) throws Exception {
						return v1.getPDBIds().size()>0;
					}})
				.collect();
//		@SuppressWarnings("resource")
//		JavaSparkContext sc = new JavaSparkContext(conf);
//		JavaRDD<String> rdd = sc.textFile(new File(file).getAbsolutePath());
//		rdd = rdd.filter(s -> !s.startsWith("#"));
//		JavaRDD<String[]> splittedRdd = rdd.map(s -> s.split("\t"));
//		JavaRDD<VCFEntry> vcfRdd = splittedRdd.map(f -> new VCFEntry(f[0], Integer.parseInt(f[1]), f[3], f[4], new HashSet<String>()));
//		JavaRDD<VCFEntry> updatedVcfRdd = vcfRdd.map(f -> new ExonMapping(gtfFile, mappingFile, reference).analyzeEntry(f));
//		JavaRDD<VCFEntry> filteredFinal = updatedVcfRdd.filter(new Function<VCFEntry, Boolean>(){
//			private static final long serialVersionUID = 753333851848380013L;
//			@Override
//			public Boolean call(VCFEntry v1) throws Exception {
//				return v1.getPDBIds().size()>0;
//			}});
//		this.vcfEntries = filteredFinal.collect();
		System.out.println("finished");
		
	}
	
	public List<VCFEntry> getVCFEntries(){
		return this.vcfEntries;
	}
	
}
