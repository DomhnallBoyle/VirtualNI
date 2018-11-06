package utils;

import java.io.File;

import app.Main;
import web.WebSecurity;

public class Web {

	public static String decodeFilePath(String uri) {
		String path = WebSecurity.decodePercent(uri);

		try
		{
			File f = new File(Main.root+"/"+path);
			String canonical = f.getCanonicalPath();
			if(canonical.startsWith(Main.root))
				return canonical;
			else
				return null;
		}
		catch(Exception e) {
			return null;
		}
	}
	
}
