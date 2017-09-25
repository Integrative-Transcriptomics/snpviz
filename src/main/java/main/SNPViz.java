package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import datastructures.ExonMapping;
import datastructures.MappingFile;
import datastructures.VCFEntry;
import datastructures.VCFParser;
import utilities.ID;
import utilities.Utilities;

public class SNPViz {

	public static String CLASS_NAME = "title";
	public static String VERSION = "x.x";
	private static final String DESC = "Tool for the identification of structures based on a SNP";

	private MappingFile mappingFile;
	private VCFParser vcfInput;


	public SNPViz(String mappingFile, String gtfFile, String vcfFile, String reference, String outFile, Boolean runSpark, Boolean keepRefInMemory, ID fromID) {
		long start = System.currentTimeMillis();
		this.mappingFile=new MappingFile(mappingFile,fromID);
		long map = System.currentTimeMillis();
		System.out.println("reading the mapping file took "+(map-start)/1000+"s");

		List<VCFEntry> result = new LinkedList<VCFEntry>();
		long annotation = 0l;

		if(runSpark){
			SparkDoVCF sp = new SparkDoVCF(vcfFile, gtfFile, this.mappingFile, reference, keepRefInMemory);
			result = sp.getVCFEntries();
			annotation = System.currentTimeMillis();
		}else{
			this.vcfInput = new VCFParser(vcfFile);
			long vcf = System.currentTimeMillis();
			System.out.println("reading the vcfFile took "+(vcf-map)/1000+"s");

			ExonMapping em = new ExonMapping(gtfFile, this.mappingFile, reference, keepRefInMemory);
			long exon = System.currentTimeMillis();
			System.out.println("reading the gtf file took "+(exon-vcf)/1000+"s" + "\t");
			
			for(VCFEntry vcfEntry: this.vcfInput.getVCFEntries()){
				VCFEntry entry = em.analyzeEntry(vcfEntry);
				if(entry.getPDBIds().size() > 0){
					result.add(entry);
				}
			}
			annotation = System.currentTimeMillis();
		}
		System.out.println("generating the annotations took "+(annotation-map)/1000+"s");




		writeNewVCFFile(outFile, result);
		long write = System.currentTimeMillis();
		System.out.println("writing the output took "+(write-annotation)/1000+"s");

		long end = System.currentTimeMillis();
		System.out.println("Program finished in "+(end-start)/1000+"s");
	}

	private void writeNewVCFFile(String outFile, List<VCFEntry> write) {
		StringBuffer toWrite = new StringBuffer();
		for(VCFEntry vcfEntry: write){
			toWrite.append(vcfEntry);
			toWrite.append("\n");
		}
		Utilities.writeToFile(toWrite.toString(), new File(outFile));
	}


	@SuppressWarnings({ })
	public static void main(String[] args) {
		loadMetadata();
		Options helpOptions = new Options();
		helpOptions.addOption("h", "help", false, "show this help page");

		Options options = new Options();
		options.addOption("h", "help", false, "show this help page");
		options.addOption("o", "output", true, "output vcf file [<INPUT>_modified.vcf]");
		options.addOption("s", "spark", false, "run with apacheSpark parallelization");
		options.addOption("k", "keep", false, "keep reference in Memory");
		options.addOption("d", "id", true, "ID type for the genes ["+ID.EnsemblTRS+"]");
		options.addOption(Option.builder("m")
				.longOpt("idmap")
				.required()
				.hasArg()
				.desc("the id mapping file")
				.build());
		options.addOption(Option.builder("g")
				.longOpt("gtf")
				.required()
				.desc("the gtf file")
				.hasArg()
				.build());
		options.addOption(Option.builder("v")
				.longOpt("vcf")
				.required()
				.desc("the input vcf file to analyze")
				.hasArg()
				.build());
		options.addOption(Option.builder("r")
				.longOpt("ref")
				.required()
				.desc("the fasta reference file")
				.hasArg()
				.build());


		HelpFormatter helpformatter = new HelpFormatter();


		CommandLineParser parser = new DefaultParser();

		String mappingFile = "";
		String gtfFile = "";
		String vcfFile = "";
		String referenceFile = "";
		String outFile = "";
		Boolean runSpark = false;
		Boolean keepRefInMemory = false;
		ID fromID = ID.EnsemblTRS;

		try {
			CommandLine cmd = parser.parse(helpOptions, args);
			if (cmd.hasOption('h')) {
				helpformatter.printHelp(CLASS_NAME, options);
				System.err.println("Posible IDs:");
				System.err.println(Arrays.asList(ID.values()));
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
			if(cmd.hasOption("s")){
				runSpark = true;
			}
			if(cmd.hasOption("k")) {
				keepRefInMemory = true;
			}
			if(cmd.hasOption("d")) {
				String from = cmd.getOptionValue("d");
				try {
					fromID = ID.valueOf(from);
				}catch (Exception e) {
					helpformatter.printHelp(CLASS_NAME, options);
					System.err.println("Given ID not valied: "+from);
					System.err.println("Possible IDs: ");
					System.err.println(Arrays.asList(ID.values()));
					System.exit(1);
				}
			}
		} catch (ParseException e) {
			helpformatter.printHelp(CLASS_NAME, options);
			System.err.println(e.getMessage());
			System.err.println("Posible IDs:");
			System.err.println(Arrays.asList(ID.values()));
			System.exit(1);

		}
		new SNPViz(mappingFile, gtfFile, vcfFile, referenceFile, outFile, runSpark, keepRefInMemory, fromID);

		System.out.println("finished");
	}

	private static void loadMetadata(){
		Properties properties = new Properties();
		try {
			//load version
			InputStream in = SNPViz.class.getResourceAsStream("/version.properties");
			properties.load(in);
			SNPViz.VERSION = properties.getProperty("version");
			in.close();
			// load title
			in = SNPViz.class.getResourceAsStream("/title.properties");
			properties.load(in);
			SNPViz.CLASS_NAME = properties.getProperty("title");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
