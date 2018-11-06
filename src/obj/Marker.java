package obj;

import app.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;
import utils.Log;
import utils.Map;

public class Marker extends MapObject {
	
	private float latitude, longitude;
	
	public Marker(float latitude, float longitude) {
		super("pin.jpg");
		this.latitude = latitude;
		this.longitude = longitude;
		this.initialize();
	}
	
	public Marker(double latitude, double longitude) {
		super("pin.jpg");
		this.latitude = (float)latitude;
		this.longitude = (float)longitude;
		this.initialize();
	}

	@Override
	protected void initialize() {
		this.createCentrePoint();
		this.createNode();
	}

	@Override
	protected void createCentrePoint() {
		int[] pixelPositions = Map.getPixelPositions(this.latitude, this.longitude, Main.centerTiles);
		this.centrePoint = new Point(pixelPositions[0], pixelPositions[1], this.latitude, this.longitude);
	}
	
	@Override
	protected void createNode() {
		this.node = new Rectangle(
			this.centrePoint.getX(),
			this.centrePoint.getY(),
			getImage().getWidth(),
			getImage().getHeight()
		);
	}
	
	@Override
	public void update() {
		this.createCentrePoint();
		this.createNode();
	}
	
	@Override
	public void handleClickEvent() {
		Log.debug("Clicking marker: " + this.toString());
	}
	
}
