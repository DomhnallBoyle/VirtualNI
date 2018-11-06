package osm;

import java.util.ArrayList;


/**
 * @author makerspacestaff
 *
 * Represents a Node in OpenStreeMap
 */
public class Node extends OSMElement {

	private ArrayList<Tag> tags;
	private float latitude, longitude;

	public Node(float latitude, float longitude) {
		super("");
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Node(String osmId, float latitude, float longitude) {
		super(osmId);
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	public ArrayList<Tag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}
	
}
