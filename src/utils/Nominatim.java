package utils;

import java.util.List;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import osm.OSMElement;

public class Nominatim {

	private static final String NOMINATIM_URL = "http://192.168.1.109/nominatim";
	
	public static HttpResponse<JsonNode> lookup(List<OSMElement> elements) {
		if (elements.size() == 0)
			return null;
		
		String osmIds = "";
		for (OSMElement element: elements) {
			osmIds += element.getClass().getSimpleName().toUpperCase().charAt(0) + element.getOsmId() + ",";
		}
		osmIds = osmIds.substring(0, osmIds.length()-1);

		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.post(NOMINATIM_URL + "/lookup")
				.queryString("format", "json")
				.queryString("osm_ids", osmIds).asJson();
			
			return jsonResponse;
		} 
		catch (UnirestException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
