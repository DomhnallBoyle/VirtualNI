package web;

import java.io.InputStream;

public interface InputStreamCallback {

	public void in(WebClientRequest r,Object metadata,InputStream s);

}
