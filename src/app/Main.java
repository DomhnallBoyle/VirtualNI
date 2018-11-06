package app;

import java.io.File;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.google.common.collect.Lists;
import com.google.common.math.IntMath;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fx.DrawCanvas;
import fx.WebCanvas;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import obj.Area;
import obj.MapObject;
import obj.Worker;
import page.Homepage;
import page.Page;
import storage.LQTree;
import storage.TreeNode;
import utils.Log;
import utils.Map;
import utils.ShapeFileReader;
import utils.Storage;
import utils.Web;
import web.WebInterface;
import web.WebRequest;
import web.WebResponse;


/**
 * @author makerspacestaff
 * 
 * Application starting point
 */
public class Main extends Application {

	public static final int APP_WIDTH = 1000;
	public static final int APP_HEIGHT = 800;
	public static final int MAP_WIDTH = 1000;
	public static final int MAP_HEIGHT = 675;
	public static final String root = "httpdocs";
	
	public static int zoom = 15;
	public static float latitude = 54.58132624942525f; 
	public static float longitude = -5.930539304318472f;
	public static float[] centerTiles = Map.getTiles(latitude, longitude, zoom); 
	public static WebCanvas webCanvas;
	public static LQTree tree;
	
	private static WebInterface webInterface;
	private static ArrayList<WebRequest> requestToProcess = new ArrayList<WebRequest>();
	private static ArrayList<Page> pages = new ArrayList<Page>();
	private static DrawCanvas drawCanvas;
	private static Gson gson = new Gson();
	private static boolean freeRoaming = true;
	public static ArrayList<MapObject> mapObjects;
	private static ArrayList<Area> areas;
	
	private static void walk(File dir) {
		File[] list = dir.listFiles();
		if (list == null) 
			return;
		for (File f: list) {
			try {
				UUID.fromString(f.getName().split("\\.")[0]);
				System.out.println("DELETING : " + f.getAbsolutePath());
				f.delete();
			}
			catch(Exception e) {}
			
			if (f.isDirectory())
				walk(f);
		}
	}
	
	public static void main(String[] args) {
//		// comment this if you don't want to rebuild your map objects
//		Log.debug("Removing Map Objects");
//		walk(new File("C:\\Users\\makerspacestaff\\PycharmProjects\\OpenStreetMap\\root_copy"));
		
		Log.debug("Initializing LQTree");
		tree = new LQTree();
		
		// shape file demo
		areas = new ArrayList<Area>();
		File areas_dir = new File("areas");
		if (areas_dir.exists()) {
			for (File f: areas_dir.listFiles()) {
				areas.add((Area) Storage.readFromFile(f.getAbsolutePath(), Area.class));
			}
		}
		else {
			areas_dir.mkdir();
			File shapeFileDir = new File("shape_files");
			for (File f: shapeFileDir.listFiles()) {
				if (f.getName().endsWith(".shp")) {
					Log.debug("Loading ShapeFile: " + f.getName());
					Area a = ShapeFileReader.readFile(f.getAbsolutePath());
					areas.add(a);
					Storage.writeToFile("areas/" + f.getName().split("\\.")[0] + ".obj", a);
				}
			}
		}
		
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {	
		BorderPane border = new BorderPane();
		border.setTop(setupTopPane());
		border.setBottom(setupBottomPane());
		
//		if (new File(root).mkdir())
//			Log.debug("Directory: " + root + " created");   		
//		else
//			Log.debug("Directory: " + root + " not created");
		
		webInterface = new WebInterface(4203);
		pages.add(new Homepage(""));
		pages.add(new page.TreeNode("treenode"));
		
		// timer runs update and render constantly
		new AnimationTimer()
	    {
			@Override
	        public void handle(long currentNanoTime)
	        {
				// 2 threads here?
	        	if (webCanvas.getEngineReady()) {
					requestToProcess.clear();
					webInterface.getQueue(requestToProcess);
					
					for(int i=0; i<requestToProcess.size(); i++) {
						WebRequest toProcess = requestToProcess.get(i);
						
						try {
							for (Page page: pages) {
								if (toProcess.path.equalsIgnoreCase(page.getURL())) {
									toProcess.r = page.handleRequest(toProcess);
									break;
								}
							}
							
							if(toProcess.r == null){
								String asFilepath = Web.decodeFilePath(toProcess.path);
		
								File asFile = new File(asFilepath);
								if((asFilepath!=null) && (toProcess.r==null)&&asFile.isFile()) { //&&fileStoreInterface.existsForUser(asFilepath,u))
									toProcess.r = WebResponse.serveFile(toProcess.parms, asFilepath);
									toProcess.r.addHeader("Access-Control-Allow-Origin", "*");
								}
								else if(toProcess.r == null) {
									toProcess.r = new WebResponse( WebResponse.HTTP_NOTFOUND, WebResponse.MIME_PLAINTEXT, "Error 404, file not found." );
								}
							}
						}
						catch(Exception e) {
							String url = toProcess.header.get("referer");//"https://"+externalip+":"+portSSL+"/";
							toProcess.r = new WebResponse( WebResponse.HTTP_REDIRECT, WebResponse.MIME_HTML,
									"<html><body>Redirected: <a href=\"" + url + "\">" +
											url + "</a></body></html>");
							toProcess.r.addHeader( "Location", url );
						}

						Thread t = new Thread(toProcess);
						t.setDaemon(true);
						t.start();
					}
	        		update();
	        		render();
	        	}
	        }
	    }.start();
		
	    stage.setScene(new Scene(border, APP_WIDTH, APP_HEIGHT));
	    stage.show();
	}
	
	
	/**
	 * Gets the boundaries of the Map and selects the appropriate node from the Loose Quad Tree,
	 * updating the map objects within that node.
	 */
	private static void update() {
//		Log.debug("UPDATE");
		if (freeRoaming) {
			String map_position = (String) webCanvas.runJS("getMapPosition()");
			if (map_position != null) { 
				mapObjects = new ArrayList<MapObject>();
				String center_position = (String) webCanvas.runJS("getCenterPosition()");
				int mapZoom = (Integer) webCanvas.runJS("getZoom()");
				
				HashMap<String, String> map = gson.fromJson(map_position, new TypeToken<HashMap<String, String>>(){}.getType());
				float[] mapBounds = new float[] {
					Float.parseFloat(map.get("minLat")), 
					Float.parseFloat(map.get("minLng")), 
					Float.parseFloat(map.get("maxLat")), 
					Float.parseFloat(map.get("maxLng"))
				};
				
				map = gson.fromJson(center_position, new TypeToken<HashMap<String, String>>(){}.getType());
				float centerLat = Float.parseFloat(map.get("lat"));
				float centerLon = Float.parseFloat(map.get("lng"));
				latitude = centerLat;
				longitude = centerLon;
				zoom = mapZoom;
				centerTiles = Map.getTiles(centerLat, centerLon, mapZoom);
				
				long startTime = System.nanoTime();
				ArrayList<TreeNode> treeNodes = tree.query(tree.getRoot(), mapBounds, zoom);
				Log.debug("Querying " + treeNodes.size() + " nodes took " + ((System.nanoTime() - startTime) / 1000000000) + " seconds.");

				// trying some concurrent stuff
				List<List<TreeNode>> smallerLists = Lists.partition(treeNodes, IntMath.divide(treeNodes.size(), 4, RoundingMode.CEILING));
				CyclicBarrier cb = new CyclicBarrier(5);
				for (int i=0; i<4; i++) {
					Thread t = new Thread(new Worker(cb, smallerLists.get(i)));
					t.start();
				}
				
				try {
					cb.await();
					Log.debug("Thread Main: Completed Loading " + treeNodes.size() + " nodes took " + ((System.nanoTime() - startTime) / 1000000000) + " seconds.");
				} 
				catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}

//				startTime = System.nanoTime();
//				for (TreeNode treeNode: treeNodes) {
////					webCanvas.runJS("drawArea(" + treeNode.getSegmentInfo().getPosition()[0] + "," + treeNode.getSegmentInfo().getPosition()[2] + "," + treeNode.getSegmentInfo().getPosition()[3] + "," + treeNode.getSegmentInfo().getPosition()[1] + ")");
//					mapObjects.addAll(treeNode.getMapObjects());
//				}
//				Log.debug("Loading " + treeNodes.size() + " nodes took " + ((System.nanoTime() - startTime) / 1000000000) + " seconds.");
				
				mapObjects.addAll(areas);
				
				drawCanvas.setMapObjects(mapObjects);
				drawCanvas.update();
			}
		}
	}
	
	/**
	 * Renders the updated map objects on the draw canvas 
	 */
	private static void render() {
//		Log.debug("RENDER");
		drawCanvas.render();
	}
	
	/**
	 * Creates the top pane within the JavaFX Window which contains the map.
	 * Handles the click events on each of the map objects.
	 * 
	 * @return stack pane object
	 */
	private static StackPane setupTopPane() {
		StackPane stack = new StackPane();
		
		webCanvas = new WebCanvas();
		drawCanvas = new DrawCanvas(MAP_WIDTH, MAP_HEIGHT);
		drawCanvas.setEventDispatcher(webCanvas.getWebView().getEventDispatcher());
		webCanvas.getWebView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				MouseEvent me = (MouseEvent) event;
				if (me.isStillSincePress()) {
					double x = me.getSceneX();
					double y = me.getSceneY();
					
					for (MapObject m: mapObjects) {
						if (m.getNode().contains(x, y)) {
							m.handleClickEvent();
						}
					}
				}
			}
		});
		stack.getChildren().addAll(webCanvas.getWebView(), drawCanvas);
		
		return stack;
	}
	
	/**
	 * Creates the bottom pane within the JavaFX Window which contains the controls 
	 * for the map
	 * 
	 * @return GridPane object with added elements
	 */
	private static GridPane setupBottomPane() {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(10);
		grid.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
		
		Label mapTypeLabel = new Label("Select the map type:");
		grid.add(mapTypeLabel, 0, 0);
		ToggleGroup toggleGroup = new ToggleGroup();
		String[] radioButtonOptions = new String[] {"Road Map", "Satellite Map", "Hybrid Map", "Terrain Map"};
		for (int i=0; i<radioButtonOptions.length; i++) {
			RadioButton radioButton = new RadioButton(radioButtonOptions[i]);
			radioButton.setToggleGroup(toggleGroup);
			radioButton.setUserData(i);
			if (i == 0)
				radioButton.setSelected(true);
			grid.add(radioButton, 0, i+1);
		}
		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			@Override
		    public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
	            if (toggleGroup.getSelectedToggle() != null) {
	            	webCanvas.runJS("setMapType(" + new_toggle.getUserData().toString() + ")");
	            }                
	        }
		});
		
		Label mapTimeLabel = new Label("Select the time period:");
		Label timeSliderLabel = new Label("Time: Current");
		grid.add(mapTimeLabel, 10, 0);
		Slider timeSlider = new Slider(0, 100, 10);
		timeSlider.setShowTickLabels(true);
		timeSlider.setShowTickMarks(true);
		timeSlider.setValue(100);
		grid.add(timeSlider, 10, 1, 10, 2);
		grid.add(timeSliderLabel, 10, 3);
		timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
	         @Override
	         public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
	        	 timeSliderLabel.setText("Time: " + newValue.intValue());
	         }
	      });
		
		return grid;
	}

}
