package obj;

import java.io.Serializable;

public class Point implements Serializable {

	private int x, y;
	private float latitude, longitude;
	
	public Point() {
		this.x = 0;
		this.y = 0;
		this.latitude = 0f;
		this.longitude = 0f;
	}
	
	public Point(float latitude, float longitude) {
		this.x = 0;
		this.y = 0;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public Point(int x, int y) {
		this.x = x;
		this.y = y;
		this.latitude = 0f;
		this.longitude = 0f;
	}
	
	public Point(int x, int y, double latitude, double longitude) {
		this.x = x;
		this.y = y;
		this.latitude = (float)latitude;
		this.longitude = (float)longitude;
	}
	
	public Point(int x, int y, float latitude, float longitude) {
		this.x = x;
		this.y = y;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
}
