package utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javafx.scene.image.Image;
import obj.Group;
import obj.MapObject;
import obj.Marker;

public class MapObjectRuntimeAdapterFactory extends RuntimeTypeAdapterFactory<MapObject> {

	public MapObjectRuntimeAdapterFactory(Class<?> baseType, String typeFieldName) {
		super(baseType, typeFieldName, false);
	}

	@Override
	protected <T> void afterRead(T source, JsonElement tree) {
//		String imageFilePath = tree.getAsJsonObject().get("imageFilePath").getAsString();
//
//		if (source instanceof Group) {
//			for (Marker m: ((Group) source).getMarkers())
//				m.setImage(new Image(
//					new File(System.getProperty("user.dir") + "/images/" + m.getImageFilePath()).toURI().toString()
//				));
//		}
//		
//		MapObject mo = (MapObject) source;
//		mo.setImage(new Image(
//			new File(System.getProperty("user.dir") + "/images/" + imageFilePath).toURI().toString()
//		));
	}
}
