package fx;

import java.util.ArrayList;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import obj.MapObject;


/**
 * @author makerspacestaff
 *
 * Canvas to draw the map objects onto.
 * This is overlayed onto the web canvas map.
 */
public class DrawCanvas extends Canvas {
	
	private GraphicsContext gc;
	private ArrayList<MapObject> mapObjects;
	
	public DrawCanvas(int width, int height) {
		super(width, height);
		gc = getGraphicsContext2D();
		this.mapObjects = new ArrayList<MapObject>();
	}
	
	public void update() {
		gc.clearRect(0, 0, this.getWidth(), this.getHeight());
		for (MapObject mapObject: mapObjects) {
			mapObject.update();
		}
	}
	
	public void render() {
		for (MapObject mapObject: this.mapObjects) {
			mapObject.render(gc);
		}
	}
	
	public ArrayList<MapObject> getMapObjects() {
		return this.mapObjects;
	}
	
	public void setMapObjects(ArrayList<MapObject> mapObjects) {
		this.mapObjects = mapObjects;
	}
	
}
