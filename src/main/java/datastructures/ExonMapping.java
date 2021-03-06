package datastructures;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.IllegalAlphabetException;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.nbio.structure.Chain;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureException;
import org.biojava.nbio.structure.StructureIO;

import utilities.Utilities;

public class ExonMapping implements Serializable {

	private static final long serialVersionUID = 5677894313743363829L;
//	private Map<String, Map<Integer, Set<String>>> pos2ens;
	private Map<String, Set<Triplet<Integer, Integer, String>>> combinedMap;
//	private VCFParser vcfInput;
	private MappingFile mappingFile;
	private String referenceFile;
	private VCFEntry vcfEntry;
	private Map<String, String> reference;
	private Boolean keepRefInMemory = false;
	private String fieldName;

//	public ExonMapping(String file, VCFEntry vcfEntry, MappingFile mappingFile, String referenceFile, Boolean keepRefInMemory){
//		this.mappingFile = mappingFile;
//		this.referenceFile = referenceFile;
//		this.keepRefInMemory = keepRefInMemory;
//		if(this.keepRefInMemory) {
//			parseAndKeepReference();
//		}
//		this.reference = new HashMap<String, String>();
//		parse(file, vcfEntry);
//	}

	public ExonMapping(String file, MappingFile mappingFile, String referenceFile, Boolean keepRefInMemory, String fieldName){
		this.mappingFile = mappingFile;
		this.referenceFile = referenceFile;
		this.keepRefInMemory = keepRefInMemory;
		this.fieldName = fieldName;
		if(this.keepRefInMemory) {
			parseAndKeepReference();
		}
		this.reference = new HashMap<String, String>();
		parse(file);
	}

	/**
	 * 
	 */
	private void parseAndKeepReference() {
		this.reference = new HashMap<String, String>();
		try {
			BufferedReader br = Utilities.getReader(this.referenceFile);
			String currLine = "";
			StringBuffer currSequence = new StringBuffer();
			String currHeader = "";
			while((currLine = br.readLine())!= null){
				if(currLine.startsWith(";")){
					continue;
				}
				if(currLine.startsWith(">")){
					if(currHeader.length()>0) {
						this.reference.put(currHeader, currSequence.toString());
					}
					currHeader = currLine.substring(1);
					currSequence = new StringBuffer();
				}else {
					currSequence.append(currLine.trim());
				}
			}
			this.reference.put(currHeader, currSequence.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public VCFEntry analyzeEntry(VCFEntry vcfEntry){
		Set<Triplet<Integer, Integer, String>> relevantExons = getRelevantExons(vcfEntry.getPosition(), vcfEntry.getReference());
		for(Triplet<Integer, Integer, String> currExon: relevantExons){
			Set<String> pdbIds = this.mappingFile.getPDBIDs(currExon.getThird());
			vcfEntry.addPdbIds(getPositionForIds(pdbIds, vcfEntry.getReference(), currExon.getFirst(), currExon.getSecond(), vcfEntry.getPosition(), vcfEntry.getRefBase(), vcfEntry.getAltBase()));
		}
		return vcfEntry;
	}

	/**
	 * @param position
	 * @param reference 
	 * @return
	 */
	private Set<Triplet<Integer, Integer, String>> getRelevantExons(Integer position, String reference) {
		Set<Triplet<Integer, Integer, String>> result = new HashSet<Triplet<Integer, Integer, String>>();
		if(this.combinedMap.containsKey(reference)){
			for(Triplet<Integer, Integer, String> currExon: this.combinedMap.get(reference)){
				if(currExon.getFirst() > position){
					break;
				}else if(currExon.getSecond()<position){
					continue;
				}else{
					result.add(currExon);
					System.out.println(currExon.getThird());
				}
			}
		}
		return result;
	}

//	private void parse(String file, VCFEntry vcfEntry) {
//		this.pos2ens = new HashMap<String, Map<Integer, Set<String>>>();
//		try {
//			BufferedReader br;
//			br = Utilities.getReader(file);
//			if(br == null){
//				return;
//			}
//			String currLine = "";
//			while((currLine = br.readLine()) != null){
//				if(currLine.startsWith("#")){
//					continue;
//				}
//				String[] splitted = currLine.split("\t");
//				if(!splitted[2].contains("exon")){
//					continue;
//				}
//				String ref = splitted[0];
//				Integer from = Integer.parseInt(splitted[3]);
//				Integer to = Integer.parseInt(splitted[4]);
//				if(vcfEntry.getPosition() >= from && vcfEntry.getPosition() <= to){
//					String ids = splitted[8];
//					String transcriptID = "";
//					for(String field: ids.split(";")){
//						field = field.trim();
//						if(field.contains("transcript_id")){
//							String[] splittedField = field.split(" ");
//							transcriptID = splittedField[1].trim().replace("\"", "");
//							break;
//						}
//					}
//					Set<String> pdbIds = this.mappingFile.getIDs(transcriptID);
//					vcfEntry.addPdbIds(getPositionForIds(pdbIds, ref, from, to, vcfEntry.getPosition(), vcfEntry.getRefBase(), vcfEntry.getAltBase()));
//					//						vcfEntry.addPdbIds(this.mappingFile.getIDs(transcriptID));
//				}
//				//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		this.vcfEntry = vcfEntry;
//	}

	private void parse(String file) {
		this.combinedMap = new HashMap<String, Set<Triplet<Integer, Integer, String>>>();
		BufferedReader br = Utilities.getReader(file);
		if(br != null){
			String currLine = "";
			try {
				while((currLine = br.readLine()) != null){
					if(currLine.startsWith("#")){
						continue;
					}
					String[] splitted = currLine.split("\t");
					if(!splitted[2].contains("exon")){
						continue;
					}
					String ref = splitted[0];
					Integer from = Integer.parseInt(splitted[3]);
					Integer to = Integer.parseInt(splitted[4]);
					String ids = splitted[8];
					String transcriptID = "";
					for(String field: ids.split(";")){
						field = field.trim();
						if(field.contains(fieldName)){
							String[] splittedField = field.split(" ");
							transcriptID = splittedField[1].trim().replace("\"", "");
							break;
						}
					}
					if(transcriptID.length()>0){
						Set<Triplet<Integer, Integer, String>> positions = new TreeSet<Triplet<Integer, Integer, String>>();
						if(this.combinedMap.containsKey(ref)){
							positions = this.combinedMap.get(ref);
						}
						positions.add(new Triplet<Integer, Integer, String>(from, to, transcriptID));
						this.combinedMap.put(ref, positions);
					}
				}
			} catch (NumberFormatException | IOException e) {
				e.printStackTrace();
			}
		}
	}

	private Set<String> getPositionForIds(Set<String> pdbIds, String ref, Integer from, Integer to, Integer position, String refBase, String altBase) {
		String exonSequence = getExonSequence(ref, from, to);
		Set<String> modifiedPdbIds = new HashSet<String>();
		for(String pdbId: pdbIds){
			if(pdbId.length()==0){
				continue;
			}
			try {
				String modifiedId = pdbId;
				String chainID = "";
				if(pdbId.contains(":")){
					String[] splitted = pdbId.split(":");
					modifiedId = splitted[0];
					chainID = splitted[1];
				}
				Structure struct = StructureIO.getStructure(modifiedId);
				Chain chain;
				if(chainID.length()>0){
					chain = struct.getChainByPDB(chainID);
				}else{
					chain = struct.getChain(0);
				}
				String aminoacidSequence = chain.getAtomSequence();
				Set<Triplet<String, Integer, String>> translatedSequences = translateSequence(exonSequence ,(position-from), refBase, altBase);
				for(Triplet<String, Integer, String> translatedSeq: translatedSequences){
					int pos = aminoacidSequence.indexOf(translatedSeq.getFirst());
					if(pos>=0){
						int modifiedPos = pos+translatedSeq.getSecond();
						modifiedPdbIds.add(pdbId+"_"+modifiedPos+"!"+translatedSeq.getThird());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (StructureException e) {
				e.printStackTrace();
			}
		}
		return modifiedPdbIds;
	}

	private Set<Triplet<String, Integer, String>> translateSequence(String exonSequence, int relPos, String refBase, String altBase) {
		Set<Triplet<String, Integer, String>> dnaSequences = new HashSet<Triplet<String, Integer, String>>();
		int length = exonSequence.length();
		String reverseComplementedSequence = exonSequence;
		try {
			SymbolList symL = DNATools.createDNA(exonSequence);
			symL = DNATools.reverseComplement(symL);
			reverseComplementedSequence = symL.seqString().toUpperCase();
		} catch (IllegalSymbolException | IllegalAlphabetException e) {
			e.printStackTrace();
			System.err.println(exonSequence);
		}
		for(int start=0; start<3; start++){
			if(start+3>length){
				continue;
			}
			int div=(length-start)/3;
			int end = div*3+start;
			int pos = relPos+start/3;
			int mod = (relPos-1)%3;
			String triplet = "";
			String modAA = "";
			String modRevAA = "";
			switch(mod){
			case 0: triplet = altBase+exonSequence.substring(relPos, relPos+2); break;
			case 1: triplet = exonSequence.substring(relPos-2, relPos-1)+altBase+exonSequence.substring(relPos, relPos+1); break;
			case 2: triplet = exonSequence.substring(relPos-3, relPos-1)+altBase; break;
			}
			try {
				SymbolList trip = DNATools.createDNA(triplet);
				SymbolList rev = DNATools.reverseComplement(trip);
				trip = DNATools.toRNA(trip);
				rev = DNATools.toRNA(rev);
				trip = RNATools.translate(trip);
				rev = RNATools.translate(rev);
				modAA = ((Symbol)trip.iterator().next()).getName();
				modRevAA = ((Symbol)rev.iterator().next()).getName();
			} catch (IllegalSymbolException | IllegalAlphabetException e) {
				e.printStackTrace();
			}
			dnaSequences.add(new Triplet<String, Integer, String>(exonSequence.substring(start,end), pos, modAA));
			dnaSequences.add(new Triplet<String, Integer, String>(reverseComplementedSequence.substring(start,end), pos, modRevAA));
		}
		HashSet<Triplet<String, Integer, String>> result = new HashSet<Triplet<String, Integer, String>>();
		for(Triplet<String, Integer, String> seq: dnaSequences){
			try {
				SymbolList symL = DNATools.createDNA(seq.getFirst());
				symL = DNATools.toRNA(symL);
				symL = RNATools.translate(symL);
				result.add(new Triplet<String, Integer, String>(symL.seqString(), symL.seqString().length()-seq.getSecond(), seq.getThird()));
			} catch (IllegalSymbolException | IllegalAlphabetException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private String getExonSequence(String ref, Integer from, Integer to) {
		if(this.keepRefInMemory && this.reference.containsKey(ref)) {
			return this.reference.get(ref).substring(Math.max(0, from-1), to);
		}
		StringBuffer exonSequence = new StringBuffer();
		try {
			@SuppressWarnings("resource")
//			BufferedReader br = new BufferedReader(new FileReader(new File(this.referenceFile)));
			BufferedReader br = Utilities.getReader(this.referenceFile);
			String currLine = "";
			boolean currSequence = false;
			int currStartPos = 0;
			while((currLine = br.readLine())!= null){
				if(currLine.startsWith(";")){
					continue;
				}
				if(currLine.startsWith(">")){
					if(currLine.contains(ref)){
						currSequence = true;
						currStartPos = 0;
					}
					continue;
				}
				if(currSequence){
					currStartPos += currLine.length();
					if(from <= currStartPos){
						int startPos = 0;
						if(from-1>currStartPos-currLine.length()){
							startPos = Math.max(0, currStartPos - (currStartPos-from)-1-(currStartPos-currLine.length()));
						}
						int calcEndPos = currStartPos-(currStartPos-to)-(currStartPos-currLine.length());
						int endPos = Math.min(currLine.length(), calcEndPos);
						exonSequence.append(currLine.substring(startPos, endPos));
						if(currStartPos >= to){
							break;
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exonSequence.toString();
	}

	public VCFEntry getVCFEntry(){
		return this.vcfEntry;
	}

}
