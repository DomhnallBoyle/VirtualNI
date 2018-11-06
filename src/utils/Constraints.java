package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import csv.Household;
import obj.House;
import obj.MapObject;
import obj.SegmentInfo;
import osm.OSMElement;
import osm.Tag;
import osm.Way;
import storage.TreeNode;

public class Constraints {
	
	private static Type type = new TypeToken<HashMap<String, HashMap<String, ArrayList<String>>>>(){}.getType();
	public static HashMap<String, HashMap<String, ArrayList<String>>> tags;
	static {
		try {
			tags = new Gson().fromJson(new BufferedReader(new FileReader(System.getProperty("user.dir") + "/json/tags.json")), type);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Filter the ways to the different map objects. This is used to initialize the Map
	 * 
	 * @param segmentInfo - segment info of the particular Tree Node
	 * @param ways - ways within that particular Tree Node
	 * @return the filtered Map objects
	 */
	public static ArrayList<MapObject> getFilteredWays(TreeNode node) {
		ArrayList<MapObject> mapObjects = new ArrayList<MapObject>(); 
		
		mapObjects.addAll(getHouses(node));
		// add more here e.g. getHospitals()

		return mapObjects;
	}
	
	private static ArrayList<House> getHouses(TreeNode node) {
		// method to get houses using some constraints
		ArrayList<House> houses = new ArrayList<House>();
		
		// we know this way is definitely a house if it's tagged
		// adding some weaker constraints
		// e.g. 
		// no. of nodes >=3 and <= 5
		// query nominatim for address and check if it has a house number
		// ignore ways at particular zoom levels (too big)
		
		int partitionSize = 50;
		List<List<OSMElement>> partitions = new ArrayList<>();
		for (int i = 0; i < node.getWays().size(); i += partitionSize) {
			partitions.add(node.getWays().subList(i, Math.min(i + partitionSize, node.getWays().size())));
		}
		
		HashMap<String, JSONObject> jsonObjects = new HashMap<String, JSONObject>();
		for (List<OSMElement> partitionedWays: partitions) {
			HttpResponse<JsonNode> jsonResponse = Nominatim.lookup(partitionedWays);
			
			JSONArray jsonArray = jsonResponse.getBody().getArray();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				jsonObjects.put(jsonObject.getString("osm_id"), jsonObject);
			}
		}
		
		House house = null;
		ArrayList<Tag> filteredTags = getFilteredTags(new String[] {"building=house"});
		for (OSMElement element: node.getWays()) {
			Way way = (Way) element;
			// if tagged - strongest constraint
			if (isTagged(way, filteredTags)) {
				// try to get the actual address using Nominatim
				// if it doesn't exist, use OAC to pick from random
				JSONObject nomObject = jsonObjects.get(way.getOsmId());
				if (nomObject != null) {
					house = new House("house.jpg", way.getMarkers(), nomObject, node.getSegmentInfo().getSuperGroup(), node.getSegmentInfo().getGroup(), node.getSegmentInfo().getSubGroup());
					houses.add(house);
				}
				else {
					
				}
			}
			else if (way.getNodes().size() >=3 && way.getNodes().size() <= 4) {
				
			}
		}
		
		// TODO: read the household dataset and apply a random household
		// need to link with the raw household information
		// preprocessing lookup table needed to randomly assign this information quickly based on OAC code
		// each household needs a time and money data associated with it, keep separate and link with code
		// needs to be saved to json in the appropriate tree nodes
		List<Household> households = Datasets.households.getBeans();
		Random r = new Random();
		for (House h: houses) {
			List<Household> filteredHouseholds = households.stream().filter(hse -> hse.getOac() != null &&  hse.getOac().equalsIgnoreCase(h.getOac())).collect(Collectors.toList());
			if (filteredHouseholds.size() > 0) {
				int i = r.nextInt(filteredHouseholds.size());
				Household randomHousehold = filteredHouseholds.get(i);
				h.setHousehold(randomHousehold);
				Datasets.households.remove(randomHousehold);
			}
		}
		
		return houses;
	}
	
	private static ArrayList<Tag> getFilteredTags(String[] filters) {
		// e.g. to filter all houses, use getFilteredTags(["building=house"])
		// check tags.json
		
		if (filters.length == 0)
			return null;
		
		ArrayList<Tag> filteredTags = new ArrayList<Tag>();
		for (String filter: filters) {
			String[] splitFilter = filter.split("=");
			HashMap<String, ArrayList<String>> parentTags = tags.get(splitFilter[0]);
			if (parentTags != null) {
				ArrayList<String> childTags = parentTags.get(splitFilter[1]);
				for (String childTag: childTags) {
					filteredTags.add(new Tag(splitFilter[0], childTag));
				}
			}
		}
		
		return filteredTags;
	}
	
	private static boolean isTagged(Way way, ArrayList<Tag> tags) {
		if (tags == null)
			return false;
		
		for (Tag wayTag: way.getTags()) {
			for (Tag tag: tags) {
				if (tag.getKey().equalsIgnoreCase(wayTag.getKey()) && tag.getValue().equalsIgnoreCase((wayTag.getValue()))) {
					return true;
				}
			}
		}
		return false;
	}
}
