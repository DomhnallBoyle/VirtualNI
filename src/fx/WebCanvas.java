package fx;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import utils.Log;


/**
 * @author makerspacestaff
 * 
 * Web Canvas to show the Map from the Google Maps API
 */
public class WebCanvas {

	private WebEngine webEngine;
	private WebView webView;
	private boolean engineReady;
	
	public WebCanvas() {
		this.webView = new WebView();
		this.webEngine = this.webView.getEngine();
		this.engineReady = false;
		setup();
	}
	
	/**
	 * Sets up the web engine, leapcontrol and loads the webpage
	 */
	private void setup() {
		this.webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				Log.debug("WebEngine State - " + newState);
				if(newState == Worker.State.SUCCEEDED) {
					//leap = new LeapControl();
					engineReady = true;
				}
			}
		});		
		this.webEngine.setJavaScriptEnabled(true);
		String homepage = System.getProperty("user.dir") + String.join(File.separator, new String[] {
			"", "httpdocs", "map.html"
		});
		File f = new File(homepage);
		this.webEngine.load(f.toURI().toString());
	}
	
	public WebEngine getWebEngine() {
		return this.webEngine;
	}
	
	public WebView getWebView() {
		return this.webView;
	}
	
	public boolean getEngineReady() {
		return this.engineReady;
	}
	
	/**
	 * Run a Javascript command available in the JS file
	 * 
	 * @param command - function to run
	 * @return object returned from function call
	 */
	public Object runJS(String command) {
		Object obj = this.webEngine.executeScript(command);
		if (!obj.equals("undefined"))
			return obj;
		else
			return null;
	}
}
