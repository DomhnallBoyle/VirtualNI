package page;

import app.Main;
import web.WebRequest;
import web.WebResponse;

public class Homepage extends Page {

	public Homepage(String url) {
		super(url);
	}

	@Override
	public WebResponse handleRequest(WebRequest request) {
		this.body = "<a href=\"treenode?directory=" + Main.tree.ROOT_START + "\"><h1>Virtual Browser</h1></a>";
		this.assembleHTML();
		
		return new WebResponse(WebResponse.HTTP_OK, WebResponse.MIME_HTML, html);
	}
	
}
