package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.FeatureSource;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opensphere.geometry.algorithm.ConcaveHull;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;

import obj.Area;
import obj.Marker;

public class ShapeFileReader {

	public static Area readFile(String filePath) {
		File file = new File(filePath);
    	ArrayList<Marker> markers = new ArrayList<Marker>();
		
		try {
			HashMap<String, Object> map = new HashMap<>();
	        map.put("url", file.toURI().toURL());
	
	        DataStore dataStore = DataStoreFinder.getDataStore(map);
	        String typeName = dataStore.getTypeNames()[0];
	
	        SimpleFeatureSource source = dataStore.getFeatureSource(typeName);
	        Filter filter = Filter.INCLUDE;
	
	        SimpleFeatureCollection collection = source.getFeatures(filter);
	        GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
	        ArrayList<Geometry> geometries = new ArrayList<Geometry>();
	        try (SimpleFeatureIterator features = collection.features()) {
	            while (features.hasNext()) {
	                SimpleFeature feature = features.next();
	                Geometry g = (Geometry)feature.getDefaultGeometry();
	                geometries.add(g);
	            }
	        }
	        
	        GeometryCollection gc = (GeometryCollection) gf.buildGeometry(geometries);
	        ConcaveHull ch = new ConcaveHull(gc, 0.5);
	        Geometry outline = ch.getConcaveHull();
	        
            Coordinate[] coords = outline.getCoordinates();
            for (Coordinate coord: coords) {
            	markers.add(new Marker(coord.y, coord.x));
            }
	        
	        dataStore.dispose();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return new Area("pin.jpg", markers);
	}
}
