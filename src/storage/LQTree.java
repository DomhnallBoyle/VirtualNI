package storage;

import java.util.ArrayList;
import java.util.HashMap;

import obj.Point;
import utils.Map;

/**
 * @author makerspacestaff
 * 
 * Loose Quad Tree Data Structure
 */
public class LQTree {
	
	private final String ROOT_DIRECTORY = "C:\\Users\\makerspacestaff\\PycharmProjects\\OpenStreetMap\\root_copy";
	
	public final String ROOT_START = ROOT_DIRECTORY.substring(ROOT_DIRECTORY.lastIndexOf("\\")+1);
	// Google API zoom levels go from 0 - 18 
	// Maps a path length to the appropriate zoom level
	// Anything not within the hashmap is given the highest zoom level
	public static final HashMap<Integer, Integer> zoomLevels = new HashMap<Integer, Integer>() {{
		put(1, 8); put(2, 9); put(3, 9); put(4, 10); put(5, 10); put(6, 11); put(7, 11); put(8, 12); put(9, 12);
		put(10, 13); put(11, 13); put(12, 14); put(13, 14); put(14, 15); put(15, 15); put(16, 16); put(17, 16); put(18, 17);
		put(19, 17); 
	}};
	
	private TreeNode root;
	private int count;
	private ArrayList<TreeNode> queriedNodes;
	
	public LQTree() {
		this.root = new TreeNode(this.ROOT_DIRECTORY);
		this.queriedNodes = new ArrayList<TreeNode>();
		
//		preprocessing(this.root);
	}
	
	/**
	 * Recursively pre-processes every node in the tree. 
	 * 
	 * @param currentNode - current node of the tree to be pre-processed
	 */
	private void preprocessing(TreeNode currentNode) {
		if (currentNode != null) {
			currentNode.loadSegment();
			preprocessing(currentNode.getNW());
			preprocessing(currentNode.getNE());
			preprocessing(currentNode.getSW());
			preprocessing(currentNode.getSE());
		}
	}
	
	public int getCount() {
		return this.count;
	}
	
	public TreeNode getRoot() {
		return this.root;
	}
	
	/**
	 * Query the LQTree to return a list of nodes based on the bounds of the map
	 * and the zoom level
	 * 
	 * @param node - starting node
	 * @param bounds - bounds of the map
	 * @param zoom - zoom level of the map
	 * @return the list of queried nodes
	 */
	public ArrayList<TreeNode> query(TreeNode node, float[] bounds, int zoom) {
		this.queriedNodes = new ArrayList<TreeNode>();
		if (zoom < 14)
			this.findByBoundsAndZoom(node, bounds, zoom);
		else
			this.findByBounds(node, bounds);
		
		return this.queriedNodes;
	}
	
	/**
	 * Recursively finds the leaf node of the tree that contains a particular position 
	 * on the map
	 * 
	 * @param node - current node
	 * @param position - position of the point to lookup
	 * @return the queried tree node
	 */
	public TreeNode find(TreeNode node, Point position) {
		TreeNode currentNode = node;
		
		if (currentNode == null)
			return null;
		
		if (!Map.contains(currentNode.getSegmentInfo().getPosition(), position))
			return null;
		
		if (currentNode.isLeaf())
			return currentNode;
		
		TreeNode result = find(currentNode.getNW(), position);
		if (result != null)
			return result;
		
		result = find(currentNode.getNE(), position);
		if (result != null)
			return result;
		
		result = find(currentNode.getSW(), position);
		if (result != null)
			return result;
		
		result = find(currentNode.getSE(), position);
		if (result != null)
			return result;
		
		return currentNode;
	}
	
	/**
	 * Find all nodes within the bounds of the map and the map zoom
	 * 
	 * @param node - current node to check
	 * @param bounds - bounds of the map
	 * @param zoom - zoom level of the map
	 */
	public void findByBoundsAndZoom(TreeNode node, float[] bounds, int zoom) {
		TreeNode currentNode = node;
		
		if (currentNode == null)
			return;
		
		if (!Map.intersection(currentNode.getSegmentInfo().getPosition(), bounds))
			return;
		
		if (currentNode.getZoomLevel() == zoom) {
			queriedNodes.add(currentNode);
			int nextZoomLevel = 0;
			for (TreeNode child_node: currentNode.getChildNodes()) {
				if (child_node != null) {
					nextZoomLevel = child_node.getZoomLevel();
					break;
				}
			}
			if (nextZoomLevel != zoom)
				return;
		}

		findByBoundsAndZoom(currentNode.getNW(), bounds, zoom);
		findByBoundsAndZoom(currentNode.getNE(), bounds, zoom);
		findByBoundsAndZoom(currentNode.getSW(), bounds, zoom);
		findByBoundsAndZoom(currentNode.getSE(), bounds, zoom);
	}
	
	/**
	 * Find all nodes within the bounds of the map
	 * 
	 * @param node - current node
	 * @param bounds - bounds of the map
	 */
	public void findByBounds(TreeNode node, float[] bounds) {
		TreeNode currentNode = node;
		
		if (currentNode == null)
			return;
		
		if (!Map.intersection(currentNode.getSegmentInfo().getPosition(), bounds))
			return;
		
		if (currentNode.getZoomLevel() > 14)
			queriedNodes.add(currentNode);
		
		findByBounds(currentNode.getNW(), bounds);
		findByBounds(currentNode.getNE(), bounds);
		findByBounds(currentNode.getSW(), bounds);
		findByBounds(currentNode.getSE(), bounds);
	}

	/**
	 * Gets the tree node based on a directory name e.g. root/nw/se/sw/ne/nw
	 * 
	 * @param node - current node
	 * @param directory - directory to search for
	 * @param level - current directory level 
	 * @return - the result of the search
	 */
	public TreeNode getTreeNode(TreeNode node, String directory, int level) {
		TreeNode currentNode = node;
		
		if (currentNode.getTreeDirectory().equalsIgnoreCase(directory))
			return currentNode;
		else {
			String nextChild = directory.split("/")[level+1];
			TreeNode childNode = currentNode.getChildNode(nextChild);
			if (childNode != null)
				return getTreeNode(childNode, directory, level+1);
			else
				return null;
		}
	}
	
}
