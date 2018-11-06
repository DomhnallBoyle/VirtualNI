package obj;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import obj.MapObject;

public class Way extends MapObject {

	protected ArrayList<Marker> markers;
	
	public Way(String imageFilePath, ArrayList<Marker> markers) {
		super(imageFilePath);
		this.markers = markers;
		this.getCentrePoint();
	}
	
	public ArrayList<Marker> getPoints() {
		return this.markers;
	}
	
	public void setPoints(ArrayList<Marker> markers) {
		this.markers = markers;
	}
	
	public Point getCentrePoint() {
		if (this.centrePoint == null) {
			Path2D path = new Path2D.Double();
	
			path.moveTo(this.markers.get(0).getCentrePoint().getLatitude(), this.markers.get(0).getCentrePoint().getLongitude());
			for(int i = 1; i < this.markers.size(); i++) {
			   path.lineTo(this.markers.get(i).getCentrePoint().getLatitude(), this.markers.get(i).getCentrePoint().getLongitude());
			}
			path.closePath();
			Rectangle2D bounds = path.getBounds2D();
			
			this.centrePoint = new Point((float)bounds.getX(), (float)bounds.getY());
		}
		return this.centrePoint;
	}
 
//	@Override
//	public void update(int zoom, float[] centerTiles) {
//		for (Marker marker: this.markers) {
//			marker.update(zoom, centerTiles);
//		}
//	}

}
