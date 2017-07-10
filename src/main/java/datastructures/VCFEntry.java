package datastructures;

import java.io.Serializable;
import java.util.Set;

public class VCFEntry implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3668441597020331519L;
	private Integer position;
	private String refBase;
	private String altBase;
	private Set<String> pdbIds;
	private String reference;
	
	public VCFEntry(String reference, Integer position, String refBase, String altBase, Set<String> pdbIds) {
		this.reference = reference;
		this.position = position;
		this.refBase = refBase;
		this.altBase = altBase;
		this.pdbIds = pdbIds;
	}
	public Set<String> getPdbIds() {
		return pdbIds;
	}
	public void setPdbIds(Set<String> pdbIds) {
		this.pdbIds = pdbIds;
	}
	public void addPdbIds(Set<String> pdbIds){
		this.pdbIds.addAll(pdbIds);
	}
	public Integer getPosition() {
		return position;
	}
	public String getRefBase() {
		return refBase;
	}
	public String getAltBase() {
		return altBase;
	}
	public String getReference(){
		return this.reference;
	}
	
	public String toString(){
		String pdbIDs = "";
		for(String s: this.pdbIds){
			if(s.length()>0){
				pdbIDs+=s+",";
			}
		}
		if(pdbIDs.length()>0){
			pdbIDs = "PDB="+pdbIDs.substring(0, pdbIDs.length()-1);
		}
		return this.reference+"\t"+this.position+"\t.\t"+this.refBase+"\t"+this.altBase+"\t.\t.\t"+pdbIDs;
	}

}
