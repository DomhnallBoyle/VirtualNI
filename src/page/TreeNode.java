package page;

import java.util.ArrayList;

import app.Main;
import obj.MapObject;
import obj.SegmentInfo;
import web.WebRequest;
import web.WebResponse;

public class TreeNode extends Page {

	public TreeNode(String url) {
		super(url);
	}

	@Override
	public WebResponse handleRequest(WebRequest request) {
		String directory = request.parms.get("directory");
		
		this.body = "<h1>" + directory + "</h1>";
		this.body += "<ul>";
		
		storage.TreeNode treeNode = Main.tree.getTreeNode(Main.tree.getRoot(), directory, 0);
		
		if (treeNode != null) {
			for (storage.TreeNode childNode: treeNode.getChildNodes()) {
				if (childNode != null)
					this.body += "<a href=\"treenode?directory=" + childNode.getTreeDirectory() + "\"><li>" + childNode.getPath().substring(childNode.getPath().length()-2) + "</li></a>";
			}
			this.body += "</ul>";
			
			SegmentInfo segmentInfo = treeNode.getSegmentInfo();
			this.body += "<h2>Segment Information:</h2>";
			this.body += "<h3>Region - " + segmentInfo.getRegion() + "</h3>";
			this.body += "<h3>Super Group - " + segmentInfo.getSuperGroup() + "</h3>";
			this.body += "<h3>Group - " + segmentInfo.getGroup() + "</h3>";
			this.body += "<h3>Sub Group - " + segmentInfo.getSubGroup() + "</h3>";
			
			ArrayList<MapObject> mapObjects = treeNode.getMapObjects();
			if (mapObjects.size() > 0) {
				this.body += "<h2>Map Objects:</h2>";
				this.body += "<ul>";
				for (MapObject mapObject: mapObjects) {
					this.body += "<li>" + mapObject.getClass().getSimpleName() + " - " + mapObject.getUUID() + "</li>";
				}
				this.body += "</ul>";
			}
			
			float[] position = segmentInfo.getPosition();
			Main.webCanvas.runJS("setCenterPosition(" + ((position[0] + position[2]) / 2) + "," + ((position[1] + position[3]) / 2) + ")");
			Main.webCanvas.runJS("setZoom(" + treeNode.getZoomLevel() + ")");
			
			this.assembleHTML();
			
			return new WebResponse(WebResponse.HTTP_OK, WebResponse.MIME_HTML, this.html);
		}
		else
			return new WebResponse(WebResponse.HTTP_OK, WebResponse.MIME_HTML, "Node not found.");
	}

}
