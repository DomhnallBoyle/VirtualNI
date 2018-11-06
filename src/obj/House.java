package obj;

import java.util.ArrayList;

import org.json.JSONObject;

import app.Main;
import csv.Household;
import utils.Dialog;
import utils.Log;


/**
 * @author makerspacestaff
 *
 * Represents a House on the Map
 */
public class House extends Group {
	
	private String houseNumber, road, hamlet, town, city, locality, county, country, state, postcode;
	private String displayName;
	private String oacSuperGroup, oacGroup, oacSubGroup;
	private Household household;
	
	private JSONObject address;
	private ArrayList<Person> people;
	
	public House(String imageFilePath, ArrayList<Marker> markers, JSONObject nomData, String oacSuperGroup, String oacGroup, String oacSubGroup) {
		super(imageFilePath, markers);
		this.displayName = nomData.optString("display_name");
		this.address = nomData.getJSONObject("address");
		this.houseNumber = address.optString("house_number");
		this.road = address.optString("road");
		this.hamlet = address.optString("hamlet");
		this.town = address.optString("town");
		this.city = address.optString("city");
		this.locality = address.optString("locality");
		this.county = address.optString("county");
		this.country = address.optString("country");
		this.state = address.optString("state");
		this.postcode = address.optString("postcode");
		this.oacSuperGroup = oacSuperGroup;
		this.oacGroup = oacGroup;
		this.oacSubGroup = oacSubGroup;
	}
	
	public House(String imageFilePath, ArrayList<Marker> markers) {
		super(imageFilePath, markers);
	}
 
	@Override
	public void handleClickEvent() {
		if (this.household != null)
			Dialog.info("House Information", this.displayName, String.format("Adults: %d\nChildren: %d\n", this.household.getNumAdult(), this.household.getNumChild()));
		else
			Dialog.info("House Information", this.displayName, "No household object.");
	}

	public String getOac() {
		return this.oacSubGroup;
	}

	public void setHousehold(Household household) {
		this.household = household;
	}
	
}
