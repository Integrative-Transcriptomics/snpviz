package datastructures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class VCFParser implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8582533513893379828L;
	Set<VCFEntry> vcfEntries;

	public VCFParser(String file){
		parse(file);
	}
	
	private void parse(String file) {
		this.vcfEntries = new HashSet<VCFEntry>();
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String currLine = "";
			while((currLine = br.readLine()) != null){
				if(currLine.startsWith("#")){
					continue;
				}
				String[] splitted = currLine.split("\t",-1);
				String chr = splitted[0];
				Integer pos = Integer.parseInt(splitted[1]);
				String refBase = splitted[3];
				String altBase = splitted[4];
				VCFEntry vcfEntry = new VCFEntry(chr, pos, refBase, altBase, new HashSet<String>());
				vcfEntries.add(vcfEntry);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Set<VCFEntry> getVCFEntries(){
		return this.vcfEntries;
	}
}
