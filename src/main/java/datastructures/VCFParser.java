package datastructures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VCFParser implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8582533513893379828L;
	Map<String, Set<VCFEntry>> chromosomes;

	public VCFParser(String file){
		parse(file);
	}
	
	private void parse(String file) {
		this.chromosomes = new HashMap<String, Set<VCFEntry>>();
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
				Set<VCFEntry> vcfEntries = new HashSet<VCFEntry>();
				if(this.chromosomes.containsKey(chr)){
					vcfEntries = this.chromosomes.get(chr);
				}
				vcfEntries.add(vcfEntry);
				this.chromosomes.put(chr, vcfEntries);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Set<VCFEntry> getEntries(String chr){
		if(this.chromosomes.containsKey(chr)){
			return this.chromosomes.get(chr);
		}else{
			return new HashSet<VCFEntry>();
		}
	}
	
	public Set<String> getKeys(){
		return this.chromosomes.keySet();
	}
}
