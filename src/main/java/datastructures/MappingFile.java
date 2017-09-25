package datastructures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import utilities.ID;
import utilities.Utilities;

public class MappingFile implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1284219139313387374L;
	
	private Map<String, Set<String>> ens2pdb = new HashMap<String, Set<String>>();
	
	private ID fromID = ID.Ensembl;
	private ID pdb = ID.PDB;
	
	public MappingFile(String file, ID fromID){
		this.fromID = fromID;
		parse(file);
	}

	private void parse(String file) {
		this.ens2pdb = new HashMap<String, Set<String>>();
		try {
			BufferedReader br = Utilities.getReader(file);
			String currLine = "";
			while((currLine = br.readLine()) != null){
				String[] splitted = currLine.split("\t",-1);
				if(splitted.length<=19){
					System.out.println(splitted.length+"\t"+currLine);
				}
				for(String currFromId: splitted[this.fromID.getColumn()].split(";")){
					currFromId = currFromId.trim();
					Set<String> pdbIDs = new HashSet<String>();
					if(this.ens2pdb.containsKey(currFromId)){
						pdbIDs = this.ens2pdb.get(currFromId);
					}
					for(String pdb: splitted[this.pdb.getColumn()].split(";")){
						pdbIDs.add(pdb.trim());
					}
					this.ens2pdb.put(currFromId, pdbIDs);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized Set<String> getIDs(String ensID){
		if(this.ens2pdb.containsKey(ensID)){
			return this.ens2pdb.get(ensID);
		}else{
			return new HashSet<String>();
		}
	}

}
