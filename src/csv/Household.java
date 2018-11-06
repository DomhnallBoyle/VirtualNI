package csv;

import com.univocity.parsers.annotations.Parsed;


/**
 * @author makerspacestaff
 *
 * Represents an Object from the Household csv file.
 * Columns are mapped to the variables using the Univocity Parser's 
 * Parsed annotations above them. 
 */
public class Household {
	
	@Parsed(field="NumAdult")
	private int numAdult;
	
	@Parsed(field="NumChild")
	private Integer numChild;
	
	@Parsed(field="HseType", defaultNullRead="0")
	private Integer houseType;
	
	@Parsed(field="FltTyp", defaultNullRead="0")
	private Integer flatType;
	
	@Parsed(field="AccOth", defaultNullRead="0")
	private Integer otherType;
	
	@Parsed(field="OAC", defaultNullRead="")
	private String oac;
	
	public Household() {
		
	}

	public int getNumAdult() {
		return numAdult;
	}

	public void setNumAdult(int numAdult) {
		this.numAdult = numAdult;
	}

	public int getNumChild() {
		return numChild;
	}

	public void setNumChild(int numChild) {
		this.numChild = numChild;
	}

	public int getHouseType() {
		return houseType;
	}

	public void setHouseType(int houseType) {
		this.houseType = houseType;
	}

	public int getFlatType() {
		return flatType;
	}

	public void setFlatType(int flatType) {
		this.flatType = flatType;
	}

	public int getOtherType() {
		return otherType;
	}

	public void setOtherType(int otherType) {
		this.otherType = otherType;
	}

	public String getOac() {
		return oac;
	}

	public void setOac(String oac) {
		this.oac = oac;
	}
	
}
