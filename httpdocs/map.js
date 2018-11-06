var mouse_position = null;
var rectangle = null;

function load_map() {
	// use these settings
	// zoom = 8
	// lat, long = 54.72459746020048, -6.306859029296902
	
	var lat = 54.58132624942525;
	var lng = -5.930539304318472;
	var zoom = 15;
	
	var location = new google.maps.LatLng(lat, lng);
	
	settings = {
		zoom: zoom,
		center: location,
		mapTypeId: google.maps.MapTypeId.ROADMAP,
		mapTypeControl: false,
		navigationControl: false,
		streetViewControl: false,
		backgroundColor: "#666970"
	};
	
	document.geocoder = new google.maps.Geocoder();
	document.map = new google.maps.Map(document.getElementById("map_canvas"), settings);
	
	document.map.addListener('mousemove', function(e) {
		mouse_position = e.latLng;
	});
};

function setMapType(type) {
	if (type == 0) {
		document.map.setMapTypeId(google.maps.MapTypeId.ROADMAP);
	}
	else if (type == 1) {
		document.map.setMapTypeId(google.maps.MapTypeId.SATELLITE);
	}
	else if (type == 2) {
		document.map.setMapTypeId(google.maps.MapTypeId.HYBRID);
	}
	else {
		document.map.setMapTypeId(google.maps.MapTypeId.TERRAIN);
	}	
}

function getMapPosition() {
	var currentBounds = document.map.getBounds();
    if (currentBounds != null) {
	  	var north = currentBounds.getNorthEast().lat();   
	    var east = currentBounds.getNorthEast().lng();
	    var south = currentBounds.getSouthWest().lat();   
	    var west = currentBounds.getSouthWest().lng();
	  	var location = {
	  		"minLat": north,
	  		"minLng": west,
	  		"maxLat": south,
	  		"maxLng": east
	  	};
	  	return JSON.stringify(location);
  	}
  	else {
  		return "";
  	}
}

function getCenterPosition() {
	return JSON.stringify(document.map.getCenter());
}
  
function getZoom() {
  	return document.map.getZoom();
}

function setCenterPosition(lat, lng) {
	document.map.setCenter(new google.maps.LatLng(lat, lng));
}

function setZoom(zoom) {
	document.map.setZoom(zoom);
}

function getMousePosition() {
	if (mouse_position != null) {
		return JSON.stringify(mouse_position);
	}
}

function addInfoWindow(content, latitude, longitude) {
	var infoWindow = new google.maps.InfoWindow({
		content: content
	});
	infoWindow.setPosition(new google.maps.LatLng(latitude, longitude));
	infoWindow.open(document.map);
}

function loadGeoJSON(geojson) {
	var geojson = json.parse(geojson);
	document.map.data.addGeoJson(geojson);
}

function drawArea(north, south, east, west) {
	// remove previous rectangle
	if (rectangle) 
		rectangle.setMap(null);

	var bounds = {
		north: north,
		south: south,
		east: east,
		west: west
	};
	
	rectangle = new google.maps.Rectangle({
		map: document.map,
		bounds: bounds,
		editable: false
	});
}
	