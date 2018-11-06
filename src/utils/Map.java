package utils;


import app.Main;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import obj.Point;

public class Map {

	public static float[] getTiles(float lat, float lon, int zoom) {
		float lon_rad = (float) Math.toRadians(lon);
		float lat_rad = (float) Math.toRadians(lat);
		float n = (float) Math.pow(2.0, zoom);
		float tileX = ((lon + 180) / 360) * n;
		float tileY = (float) ((1 - (Math.log(Math.tan(lat_rad) + 1.0/Math.cos(lat_rad)) / Math.PI)) * n / 2.0);
		
		return new float[] {tileX, tileY};
	}
	
	public static int[] getPixelPositions(float latitude, float longitude, float[] centerTiles) {
		float[] objectTiles = Map.getTiles(latitude, longitude, Main.zoom);
		float deltaX = objectTiles[0] - centerTiles[0];
		float deltaY = objectTiles[1] - centerTiles[1];
		float[] fromCenter = new float[] {deltaX * 256, deltaY * 256};
		float[] mapCenter = new float[] {Main.MAP_WIDTH/2, Main.MAP_HEIGHT/2};
		
		return new int[] {(int)(mapCenter[0] + fromCenter[0]), (int)(mapCenter[1] + fromCenter[1])};
	}
	
	public static boolean intersection(float[] bounds1, float[] bounds2) {
		if (bounds2[2] > bounds1[0] || bounds1[2] > bounds2[0])
			return false;
		
		if (bounds2[1] < bounds1[3] && bounds1[1] < bounds2[3])
			return true;
		
		return false;
	}
	
	public static boolean contains(float[] bounds1, float[] bounds2) {
		Point topLeft = new Point(bounds2[0], bounds2[1]);
		Point bottomRight = new Point(bounds2[2], bounds2[3]);
		
		if (contains(bounds1, topLeft) && contains(bounds1, bottomRight))
			return true;
		
		return false;
	}
	
	public static boolean contains(float[] bounds, Point point) {
		float lat = point.getLatitude();
		float lng = point.getLongitude();
		
		if ((lat >= bounds[2] && lat <= bounds[0]) && (lng >= bounds[1] && lng <= bounds[3]))
			return true;
		
		return false;
	}
}
