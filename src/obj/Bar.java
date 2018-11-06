package obj;

import java.util.ArrayList;

import utils.Log;

public class Bar extends Group {
	
	public Bar(String imageFilePath, ArrayList<Marker> markers) {
		super(imageFilePath, markers);
	}
 
	@Override
	public void handleClickEvent() {
		Log.debug("Clicking Health: " + this.toString());
	}
}
