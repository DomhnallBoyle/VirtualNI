package storage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

import obj.MapObject;
import obj.Marker;
import obj.SegmentInfo;
import osm.Node;
import osm.OSMElement;
import osm.Tag;
import osm.Way;
import utils.Constraints;
import utils.Log;
import utils.Storage;

public class TreeNode {

	private final String OSM_INFO = "segment.osm";
	private final String SEGMENT_INFO = "segment_info.json";
	private final int ZOOM_START = 8;
	
	private TreeNode nw, ne, sw, se;
	private String path;
	private int zoomLevel;
	private boolean isLoaded;
	
	private ArrayList<MapObject> mapObjects;
	private ArrayList<OSMElement> ways;
	private SegmentInfo segmentInfo;
	
	public TreeNode(String path) {
		this.path = path;
		this.nw = checkTreeNode(this.path + "/nw"); 
		this.ne = checkTreeNode(this.path + "/ne");
		this.sw = checkTreeNode(this.path + "/sw");
		this.se = checkTreeNode(this.path + "/se");

		this.isLoaded = false;
		this.mapObjects = new ArrayList<MapObject>();
	}
	
	/**
	 * Checks if there exists a child node available for creation
	 * 
	 * @param child node path
	 * @return child node if exists
	 */
	private TreeNode checkTreeNode(String path) {
		File f = new File(path);
		if (f.exists() && f.isDirectory()) {
			return new TreeNode(path);
		}
		else
			return null;
	}
	
	/**
	 * The node is a leaf if all it's children are null
	 * 
	 * @return if leaf node or not
	 */
	public boolean isLeaf() {
		return (this.nw == null && this.ne == null && this.sw == null && this.se == null);
	}
	
	public String getPath() {
		return this.path;
	}
	
	/**
	 * Gets the zoom level of the node based on it's path length 
	 * in the LQTree. If the path of the node is too long and 
	 * returns a zoom level of 0, set it to the highest zoom level. 
	 * 
	 * @return zoom level 
	 */
	public int getZoomLevel() {
		if (this.zoomLevel == 0) {
			int pathLength = this.path.split("/").length;
			this.zoomLevel = LQTree.zoomLevels.get(pathLength);
			if (this.zoomLevel == 0)
				this.zoomLevel = 18;
		}
		return this.zoomLevel;
	}
	
	/**
	 * Loads the tree node objects
	 */
	public void loadSegment() {		
		// check if any objects exist already within the directory
		File dir = new File(this.path);
		for (File f: dir.listFiles()) {
			if (!f.isDirectory()) {
				try {
					UUID.fromString(f.getName().split("\\.")[0]);
//					Log.debug("Reading: " + f.getAbsolutePath());
					this.mapObjects.add((MapObject) Storage.readFromJSON(f.getAbsolutePath(), MapObject.class));
				}
				catch(Exception e) {}
			}
		}
		
		// if no objects saved within the directory, extract the ways from the OSM file
		if (this.mapObjects.size() == 0) {
			File file = new File(this.path + "/" + this.OSM_INFO);
	 
			BufferedReader br = null;
			boolean found_way = false, found_node = false;
			Way way = null;
			this.ways = new ArrayList<OSMElement>();
			
			try {
				br = new BufferedReader(new FileReader(file));
				String st;
				while ((st = br.readLine()) != null) {
					if (found_way) {
						if (found_node) {
							if (st.startsWith("</node>"))
								found_node = false;
							else if (st.startsWith("<tag")) {
								String[] split_line = st.split("\"");
								String key = split_line[1];
								String value = split_line[3];
								way.getTags().add(new Tag(key, value));
							}
						}
						else if (st.startsWith("<node")) {
							found_node = true;
							String[] split_line = st.split("\"");
							float latitude = Float.parseFloat(split_line[3]);
							float longitude = Float.parseFloat(split_line[5]);
							way.getNodes().add(new Node(latitude, longitude));
							if (st.endsWith("/>"))
								found_node = false;
						}
						else if (st.startsWith("<tag")) {
							String[] split_line = st.split("\"");
							String key = split_line[1];
							String value = split_line[3];
							way.getTags().add(new Tag(key, value));
						}
						else if (st.startsWith("</way>")) {
							ways.add(way);
							found_way = false;
							way = null;
						}
					}
					else if (st.startsWith("<way")) {
						found_way = true;
						String osmId = st.split("\"")[1];
						way = new Way(osmId);
					}
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			
			// filter the ways for initialization into houses etc
			if (this.ways.size() > 0) {
				this.mapObjects.addAll(Constraints.getFilteredWays(this));
						
				// save map objects to correct directory
				for (MapObject mapObject: this.mapObjects) {
	//				Log.debug("Writing: " + this.path + "/" + mapObject.getUUID() + ".json");
					Storage.writeToJSON(this.path + "/" + mapObject.getUUID() + ".json", mapObject);
				}
			}
		}
		
		// parse the segment info if not already done so
		if (this.segmentInfo == null)
			this.parseSegmentInfo();
		
		this.isLoaded = true;
	}
	
	public boolean isLoaded() {
		return this.isLoaded;
	}
	
	public void parseSegmentInfo() {
        this.segmentInfo = (SegmentInfo) Storage.readFromJSON(this.path + "/" + this.SEGMENT_INFO, SegmentInfo.class);
	}
	
	public SegmentInfo getSegmentInfo() {
		if (this.segmentInfo == null)
			this.parseSegmentInfo();
		
		return this.segmentInfo;
	}
	
	public TreeNode[] getChildNodes() {
		return new TreeNode[] {this.nw, this.ne, this.sw, this.se};
	}
	
	public TreeNode getNW() {
		return this.nw;
	}

	public TreeNode getNE() {
		return this.ne;
	}
	
	public TreeNode getSW() {
		return this.sw;
	}
	
	public TreeNode getSE() {
		return this.se;
	}
	
	public ArrayList<MapObject> getMapObjects() {
		if (!this.isLoaded) {
			this.mapObjects = new ArrayList<MapObject>();
			this.loadSegment();
		}
		
		return this.mapObjects;
	}
	
	public ArrayList<OSMElement> getWays() {
		return this.ways;
	}
	
	public TreeNode getChildNode(String name) {
		switch(name) {
			case "nw":
				return this.nw;
			case "ne": 
				return this.ne;
			case "sw": 
				return this.sw;
			case "se":
				return this.se;
		}
		return null;
	}
	
	public String getTreeDirectory() {
		return this.path.substring(this.path.indexOf("root"), this.path.length());
	}
}
