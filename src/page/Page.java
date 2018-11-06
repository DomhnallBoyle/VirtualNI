package page;

import web.WebRequest;
import web.WebResponse;

public abstract class Page {

	protected String url, html, body;
	
	public Page(String url) {
		this.url = url;
	}
	
	public String getURL() {
		return this.url;
	}
	
	protected void assembleHTML() {
		this.html = "<!DOCTYPE html><html><body>" + this.body + "</body></html>";
	}
	
	public abstract WebResponse handleRequest(WebRequest request);
	
}
