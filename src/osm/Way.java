package osm;

import java.util.ArrayList;

import obj.Marker;

public class Way extends OSMElement {
	
	private ArrayList<Node> nodes;
	private ArrayList<Tag> tags;
	
	public Way(String osmId) {
		super(osmId);
		this.nodes = new ArrayList<Node>();
		this.tags = new ArrayList<Tag>();
	}
	
	public Way(String osmId, ArrayList<Node> nodes, ArrayList<Tag> tags) {
		super(osmId);
		this.nodes = nodes;
		this.tags = tags;
	}
	
	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}

	public ArrayList<Tag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<Tag> tags) {
		this.tags = tags;
	}
	
	public ArrayList<Marker> getMarkers() {
		ArrayList<Marker> markers = new ArrayList<Marker>();
		for (Node node: this.nodes) {
			markers.add(new Marker(node.getLatitude(), node.getLongitude()));
		}
		
		return markers;
	}

}
