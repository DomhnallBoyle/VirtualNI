package obj;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import app.Main;
import javafx.scene.shape.Polygon;
import utils.Map;

public abstract class Group extends MapObject {

	protected ArrayList<Marker> markers;
	
	public Group(String imageFilePath, ArrayList<Marker> markers) {
		super(imageFilePath);
		this.markers = markers;
		this.initialize();
	}
	
	public ArrayList<Marker> getMarkers() {
		return this.markers;
	}
	
	public void setMarkers(ArrayList<Marker> markers) {
		this.markers = markers;
	}
	
	@Override
	public void initialize() {
		for (Marker m: this.markers) {
			m.createCentrePoint();
		}
		this.createCentrePoint();
		this.createNode();
	}
	
	@Override
	public void createCentrePoint() {
		Path2D path = new Path2D.Float();
		
		path.moveTo(this.markers.get(0).getCentrePoint().getLatitude(), this.markers.get(0).getCentrePoint().getLongitude());
		for(int i = 1; i < this.markers.size(); i++) {
		   path.lineTo(this.markers.get(i).getCentrePoint().getLatitude(), this.markers.get(i).getCentrePoint().getLongitude());
		}
		path.closePath();
		Rectangle2D bounds = path.getBounds2D();
		
		float latitude = (float)bounds.getCenterX();
		float longitude = (float)bounds.getCenterY();
		int[] pixelPositions = Map.getPixelPositions(latitude, longitude, Main.centerTiles);
		this.centrePoint = new Point(pixelPositions[0], pixelPositions[1], latitude, longitude);
		
//		float latitude = 0f;
//		float longitude = 0f;
//		
//		for (Marker m: this.markers) {
//			latitude += m.getCentrePoint().getLatitude();
//			longitude += m.getCentrePoint().getLongitude();
//		}
//		
//		latitude /= this.markers.size();
//		longitude /= this.markers.size();
//		
//		int[] pixelPositions = Map.getPixelPositions(latitude, longitude, Main.centerTiles);
//		this.centrePoint = new Point(pixelPositions[0], pixelPositions[1], latitude, longitude);
	}
	
	@Override
	public void createNode() {
		ArrayList<Double> points = new ArrayList<Double>();
		
		for (Marker m: this.getMarkers()) {
			points.add((double) m.getCentrePoint().getX());
			points.add((double) m.getCentrePoint().getY());
		}
		
		Polygon p = new Polygon();
		p.getPoints().addAll(points);
		
		this.node = p;
	}
	
	@Override
	public void update() {
		for (Marker m: this.markers) {
			m.update();
		}
		this.createCentrePoint();
		this.createNode();
	}
	
}
