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

public class MappingFile implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1284219139313387374L;
	Map<String, Set<String>> ens2pdb = new HashMap<String, Set<String>>();
	
	public MappingFile(String file){
		parse(file);
	}

	private void parse(String file) {
		this.ens2pdb = new HashMap<String, Set<String>>();
		try {
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			String currLine = "";
			while((currLine = br.readLine()) != null){
				String[] splitted = currLine.split("\t",-1);
				if(splitted.length<=19){
					System.out.println(splitted.length+"\t"+currLine);
				}
				for(String ens: splitted[19].split(";")){
					ens = ens.trim();
					Set<String> pdbIDs = new HashSet<String>();
					if(this.ens2pdb.containsKey(ens)){
						pdbIDs = this.ens2pdb.get(ens);
					}
					for(String pdb: splitted[5].split(";")){
						pdbIDs.add(pdb.trim());
					}
					this.ens2pdb.put(ens, pdbIDs);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Set<String> getIDs(String ensID){
		if(this.ens2pdb.containsKey(ensID)){
			return this.ens2pdb.get(ensID);
		}else{
			return new HashSet<String>();
		}
	}

}
