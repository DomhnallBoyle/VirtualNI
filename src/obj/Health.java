package obj;

import java.util.ArrayList;

import utils.Log;

public class Health extends Group {
	
	public Health(String imageFilePath, ArrayList<Marker> markers) {
		super(imageFilePath, markers);
	}
 
	@Override
	public void handleClickEvent() {
		Log.debug("Clicking Health: " + this.toString());
	}
}
