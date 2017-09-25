/**
 * 
 */
package utilities;

/**
 * @author Alexander Seitz
 *
 */
public enum ID {
	
	UniProtKBAC{
		@Override
		public Integer getColumn() {
			return 0;
		}
	},
	UniProtKBID{
		@Override
		public Integer getColumn() {
			return 1;
		}
	},
	EntrezGene{
		@Override
		public Integer getColumn() {
			return 2;
		}
	},
	RefSeq {
		@Override
		public Integer getColumn() {
			return 3;
		}
	},
	GI {
		@Override
		public Integer getColumn() {
			return 4;
		}
	},
	PDB {
		@Override
		public Integer getColumn() {
			return 5;
		}
	},
	GO {
		@Override
		public Integer getColumn() {
			return 6;
		}
	},
	UniRef100 {
		@Override
		public Integer getColumn() {
			return 7;
		}
	},
	UniRef90 {
		@Override
		public Integer getColumn() {
			return 8;
		}
	},
	UniRef50 {
		@Override
		public Integer getColumn() {
			return 9;
		}
	},
	UniParc {
		@Override
		public Integer getColumn() {
			return 10;
		}
	},
	PIR {
		@Override
		public Integer getColumn() {
			return 11;
		}
	},
	NCBItaxon {
		@Override
		public Integer getColumn() {
			return 12;
		}
	},
	MIM {
		@Override
		public Integer getColumn() {
			return 13;
		}
	},
	UniGene {
		@Override
		public Integer getColumn() {
			return 14;
		}
	},
	PubMed {
		@Override
		public Integer getColumn() {
			return 15;
		}
	},
	EMBL {
		@Override
		public Integer getColumn() {
			return 16;
		}
	},
	EMBLCDS {
		@Override
		public Integer getColumn() {
			return 17;
		}
	},
	Ensembl {
		@Override
		public Integer getColumn() {
			return 18;
		}
	},
	EnsemblTRS {
		@Override
		public Integer getColumn() {
			return 19;
		}
	},
	EnsemblPRO {
		@Override
		public Integer getColumn() {
			return 20;
		}
	},
	PubMedAdditional {
		@Override
		public Integer getColumn() {
			return 21;
		}
	};
	
	
	/**
	 * @return the the number of the column in the mapping file
	 */
	public abstract Integer getColumn();

}
