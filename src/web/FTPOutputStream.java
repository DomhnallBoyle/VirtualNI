package web;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;

public class FTPOutputStream implements OutputStreamCallback
{	
	public void out(WebRequest r,Object metadata,OutputStream s)
	{
		PrintWriter pw = new PrintWriter( s );

		String path = (String)metadata;
		File dir = new File(path);
    	File[] children = dir.listFiles();
    	for(int fi=0;fi<children.length;fi++) 
    	{
    		String line = "";
			if(children[fi].isDirectory())
			{
				line += "drw-rw-rw-";
				line += " "+"1";
				line += " "+"user";
				line += " "+"grup";
				line += " "+Long.toString(children[fi].length(),8);
				line += " "+"Sep 9 1980";
				line += " "+children[fi].getName()+"\r\n";
			}
			else
			{
				line += "-rw-rw-rw-";
				line += " "+"1";
				line += " "+"user";
				line += " "+"grup";
				line += " "+Long.toString(children[fi].length(),8);
				line += " "+"Sep 9 1980";
				line += " "+children[fi].getName()+"\r\n";
			}
			line += "\r\n";
	    	pw.append(line);
    	}
		pw.flush();

	}

}
