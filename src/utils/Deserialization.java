package utils;

import java.io.File;

import javafx.scene.image.Image;
import obj.MapObject;

public class Deserialization {

	public MapObject afterRead(MapObject object) {
		object.setImage(new Image(
			new File(System.getProperty("user.dir") + "/images/" + object.getImageFilePath()).toURI().toString()
		));
		
		return object;
	}
}
