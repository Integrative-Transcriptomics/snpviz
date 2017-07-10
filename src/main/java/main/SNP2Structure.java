package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import datastructures.MappingFile;
import datastructures.VCFEntry;
import datastructures.VCFParser;

public class SNP2Structure {
	
	public static String CLASS_NAME = "title";
	public static String VERSION = "x.x";
	private static final String DESC = "Tool for the identification of structures based on a SNP";
	
	private MappingFile mappingFile;
//	private VCFParser vcfInput;


	public SNP2Structure(String mappingFile, String gtfFile, String vcfFile, String reference, String outFile) {
		// TODO Auto-generated constructor stub
		long start = System.currentTimeMillis();
		this.mappingFile=new MappingFile(mappingFile);
		long map = System.currentTimeMillis();
		System.out.println("reading the mapping file took "+(map-start)/1000+"s");
		
//		this.vcfInput = new VCFParser(vcfFile);
//		long vcf = System.currentTimeMillis();
//		System.out.println("reading the vcfFile took "+(vcf-map)/1000+"s");
		
		SparkDoVCF sp = new SparkDoVCF(vcfFile, gtfFile, this.mappingFile, reference);
//		System.exit(1);
		
//		new ExonMapping(gtfFile, this.vcfInput, this.mappingFile, reference);
		long exon = System.currentTimeMillis();
		System.out.println("generating the annotations took "+(exon-map)/1000+"s");
		
		writeNewVCFFile(outFile, sp.getVCFEntries());
		long write = System.currentTimeMillis();
		System.out.println("writing the output took "+(write-exon)/1000+"s");
		
		long end = System.currentTimeMillis();
		System.out.println("Program finished in "+(end-start)/1000+"s");
	}

	private void writeNewVCFFile(String outFile, List<VCFEntry> write) {
		//TODO
		StringBuffer toWrite = new StringBuffer();
//		for(String key: this.vcfInput.getKeys()){
			for(VCFEntry vcfEntry: write){
				toWrite.append(vcfEntry);
				toWrite.append("\n");
			}
//		}
		writeToFile(toWrite.toString(), new File(outFile));
	}
	
	private void writeToFile(String s, File f){
		try {
			PrintWriter outMerged = new PrintWriter(new BufferedWriter(new FileWriter(f, false)));
			outMerged.println(s);
			outMerged.flush();
			outMerged.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings({ "static-access", "deprecation" })
	public static void main(String[] args) {
		loadMetadata();
		Options helpOptions = new Options();
		helpOptions.addOption("h", "help", false, "show this help page");
		
		Options options = new Options();
		options.addOption("h", "help", false, "show this help page");
		options.addOption("o", "output", true, "output vcf file [<INPUT>_modified.vcf]");
		options.addOption(OptionBuilder.withLongOpt("idmap")
				.withArgName("idmap")
				.withDescription("the id mapping file")
				.isRequired()
				.hasArg()
				.create("m"));
		options.addOption(OptionBuilder.withLongOpt("gtf")
				.withArgName("gtf")
				.withDescription("the gtf file")
				.isRequired()
				.hasArg()
				.create("g"));
		options.addOption(OptionBuilder.withLongOpt("vcf")
				.withArgName("vcf")
				.withDescription("the vcf file")
				.isRequired()
				.hasArg()
				.create("v"));
		options.addOption(OptionBuilder.withLongOpt("ref")
				.withArgName("ref")
				.withDescription("the fasta reference file")
				.isRequired()
				.hasArg()
				.create("r"));

		
		HelpFormatter helpformatter = new HelpFormatter();
		
		
		CommandLineParser parser = new BasicParser();
		
		String mappingFile = "";
		String gtfFile = "";
		String vcfFile = "";
		String referenceFile = "";
		String outFile = "";
		
		try {
			CommandLine cmd = parser.parse(helpOptions, args);
			if (cmd.hasOption('h')) {
				helpformatter.printHelp(CLASS_NAME, options);
				System.exit(1);
			}
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
		}
		
		try {
			CommandLine cmd = parser.parse(options, args);
			mappingFile = cmd.getOptionValue("m");
			gtfFile = cmd.getOptionValue("g");
			vcfFile = cmd.getOptionValue("v");
			referenceFile = cmd.getOptionValue("r");
			outFile = vcfFile+"_modified.vcf";
			if(cmd.hasOption("o")){
				outFile = cmd.getOptionValue("o");
			}
		} catch (ParseException e) {
			helpformatter.printHelp(CLASS_NAME, options);
			System.err.println(e.getMessage());
			System.exit(1);

		}
		new SNP2Structure(mappingFile, gtfFile, vcfFile, referenceFile, outFile);
		
		System.out.println("finished");
	}
	
	private static void loadMetadata(){
		Properties properties = new Properties();
		try {
			//load version
			InputStream in = SNP2Structure.class.getResourceAsStream("/version.properties");
			properties.load(in);
			SNP2Structure.VERSION = properties.getProperty("version");
			in.close();
			// load title
			in = SNP2Structure.class.getResourceAsStream("/title.properties");
			properties.load(in);
			SNP2Structure.CLASS_NAME = properties.getProperty("title");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
