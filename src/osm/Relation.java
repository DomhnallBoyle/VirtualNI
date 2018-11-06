package osm;

import java.util.ArrayList;

public class Relation extends OSMElement {

	private ArrayList<Way> ways;
	
	public Relation(String osmId) {
		super(osmId);
	}
	
	public ArrayList<Way> getWays() {
		return this.ways;
	}
	
	public void setWays(ArrayList<Way> ways) {
		this.ways = ways;
	}
}
