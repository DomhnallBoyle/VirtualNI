package obj;

import java.util.ArrayList;

import javafx.geometry.BoundingBox;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;
import utils.Log;

public class Area extends Group {

//	protected ArrayList<Area> subAreas;
	
	public Area(String imageFilePath, ArrayList<Marker> markers) {
		super(imageFilePath, markers);
//		this.subAreas = null;
	}
	
	@Override
	public void render(GraphicsContext gc) {
		double[] xPoints = new double[this.markers.size()];
		double[] yPoints = new double[this.markers.size()];
		
		for (int i=0; i<this.markers.size(); i++) {
			xPoints[i] = this.markers.get(i).getCentrePoint().getX();
			yPoints[i] = this.markers.get(i).getCentrePoint().getY();
		}

		gc.strokePolygon(xPoints, yPoints, this.markers.size());
	}
	
	@Override
	public void handleClickEvent() {
		Log.debug("Clicking Area: " + this.toString());
	}
	
}
