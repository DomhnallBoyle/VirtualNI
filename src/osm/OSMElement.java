package osm;

public class OSMElement {

	private String osmId;
	
	public OSMElement(String osmId) {
		this.osmId = osmId;
	}
	
	public String getOsmId() {
		return this.osmId;
	}
	
	public void setOsmId(String osmId) {
		this.osmId = osmId;
	}
}
