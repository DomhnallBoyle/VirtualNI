package obj;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Polygon;


 /**
 * @author makerspacestaff
 * 
 * Represents a MapObject that will be updated and displayed
 */
public abstract class MapObject implements Serializable {

	protected UUID uuid;
	protected String imageFilePath;
	protected transient Image image;
	protected Point centrePoint;
	protected transient Node node;
	protected String type = this.getClass().getName();
	
	public MapObject(String imageFilePath) {
		this.uuid = UUID.randomUUID();
		this.imageFilePath = imageFilePath;
	}
	
	public String getUUID() {
		return this.uuid.toString();
	}
	
	public String getImageFilePath() {
		return imageFilePath;
	}

	public void setImageFilePath(String imageFilePath) {
		this.imageFilePath = imageFilePath;
	}

	public Image getImage() {
		if (this.image == null)
			this.image = new Image(new File(System.getProperty("user.dir") + "/images/" + this.imageFilePath).toURI().toString());
		return this.image;
	}

	public void setImage(Image image) {
		this.image = image;
	}
	
	public Point getCentrePoint() {
		return this.centrePoint;
	}

	public void setCentrePoint(Point centrePoint) {
		this.centrePoint = centrePoint;
	}

	public Node getNode() {
		return this.node;
	}
	
	public void setBoundingBox(Node node) {
		this.node = node;
	}
	
	public void render(GraphicsContext gc) {
		gc.drawImage(getImage(), this.centrePoint.getX(), this.centrePoint.getY());
	}

	/**
	 * Initializes the map object
	 */
	protected abstract void initialize();
	
	/**
	 * Creates the centre point of the map object
	 */
	protected abstract void createCentrePoint();
	
	/**
	 * Creates the node shape around the map object
	 */
	protected abstract void createNode();
	
	/**
	 * Contains the functionality to update the map object
	 */
	public abstract void update();
	
	/**
	 * To handle the click events of each map object
	 */
	public abstract void handleClickEvent();
}
