package utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

import obj.House;
import obj.MapObject;
import obj.Marker;
import obj.Area;


public class Storage {

	// register any subtypes here for deserializing from JSON files
//	private static RuntimeTypeAdapterFactory<MapObject> typeFactory = RuntimeTypeAdapterFactory
//			.of(MapObject.class, "type")
//			.registerSubtype(Marker.class, Marker.class.getName())
//			.registerSubtype(House.class, House.class.getName());
	private static MapObjectRuntimeAdapterFactory typeFactory;
	static {		
		try {
			typeFactory = new MapObjectRuntimeAdapterFactory(MapObject.class, "type");
			typeFactory.registerSubtype(Marker.class, Marker.class.getName());
			typeFactory.registerSubtype(Area.class, Area.class.getName());
			typeFactory.registerSubtype(House.class, House.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Gson gson = new GsonBuilder().registerTypeAdapterFactory(typeFactory).create();
	
	public static void writeToFile(String filePath, Object obj) {
		try {
			FileOutputStream fos = new FileOutputStream(filePath);
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(obj);
		    oos.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object readFromFile(String filePath, Class<?> klass) {
		FileInputStream fin;
		ObjectInputStream ois;
		Object obj = null;
		try {
			fin = new FileInputStream(filePath);
			ois = new ObjectInputStream(fin);
			obj = ois.readObject();
		    ois.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

	    return obj;
	}
	
	public static Object readFromJSON(String filePath, Class<?> klass) {
		BufferedReader bufferedReader = null;
		Object readObject = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(filePath));
			readObject = gson.fromJson(bufferedReader, klass);
			bufferedReader.close();
			
			return readObject;
		} 
		catch (JsonParseException e) {
			Log.debug("Failed to read " + filePath);
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void writeToJSON(String filePath, Object obj) {
		Writer writer;
		try {
			writer = new FileWriter(filePath);
			gson.toJson(obj, writer);
			writer.flush();
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
