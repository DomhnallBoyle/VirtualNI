/* 
Copyright Paul James Mutton, 2001-2004, http://www.jibble.org/

This file is part of SimpleFTP.

This software is dual-licensed, allowing you to choose between the GNU
General Public License (GPL) and the www.jibble.org Commercial License.
Since the GPL may be too restrictive for use in a proprietary application,
a commercial license is also provided. Full license information can be
found at http://www.jibble.org/licenses/

$Author: pjm2 $
$Id: SimpleFTP.java,v 1.2 2004/05/29 19:27:37 pjm2 Exp $

*/

package web;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.*;

import web.GeneralMatrixString;


/**
 * SimpleFTP is a simple package that implements a Java FTP client.
 * With SimpleFTP, you can connect to an FTP server and upload multiple files.
 *  <p>
 * Copyright Paul Mutton,
 *           <a href="http://www.jibble.org/">http://www.jibble.org/</a>
 * 
 */
public class SimpleFTP {

	String host = null;
	
	public static void main(String[] list) throws Exception
	{
		String test = "drwxr-xr-x   1 root users         4096 Nov 15  2014 test";
		String tentry = getSize(test);
		System.out.println(tentry);
//		SimpleFTP ftp = new SimpleFTP();
//		ftp.connect("localhost", 8081);
//		ftp.cwd("/Users/johnbustard/Documents/ftp");
//		ftp.disconnect();
	}
    
    /**
     * Create an instance of SimpleFTP.
     */
    public SimpleFTP() {
        
    }
    
    
    /**
     * Connects to the default port of an FTP server and logs in as
     * anonymous/anonymous.
     */
    public synchronized void connect(String host) throws IOException {
        connect(host, 21);
    }
    
    
    /**
     * Connects to an FTP server and logs in as anonymous/anonymous.
     */
    public synchronized void connect(String host, int port) throws IOException {
        connect(host, port, "anonymous", "anonymous");
    }
    
    
    /**
     * Connects to an FTP server and logs in with the supplied username
     * and password.
     */
    public synchronized void connect(String host, int port, String user, String pass) throws IOException {
        if (socket != null) {
            throw new IOException("SimpleFTP is already connected. Disconnect first.");
        }
        this.host = host;
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        

        String response = readLine();
        if (!response.startsWith("220 ")) {
            throw new IOException("SimpleFTP received an unknown response when connecting to the FTP server: " + response);
        }
        
        sendLine("USER " + user);
        
        response = readLine();
        if (!response.startsWith("331 ")) {
            throw new IOException("SimpleFTP received an unknown response after sending the user: " + response);
        }
        
        sendLine("PASS " + pass);
        
        response = readLine();
        if (!response.startsWith("230 ")) {
            throw new IOException("SimpleFTP was unable to log in with the supplied password: " + response);
        }

    }

    public synchronized void setupDataConnection() throws IOException
    {
        sendLine("PASV");
        String response = readLine();
        if (!response.startsWith("227 ")) {
            throw new IOException("SimpleFTP could not request passive mode: " + response);
        }

        String[] vals = response.split(",");
        String p1 = vals[4];
        String p2 = vals[5].substring(0,vals[5].length()-1);
        int ip1 = Integer.parseInt(p1);
        int ip2 = Integer.parseInt(p2);
        dport = ip1*256+ip2;

        dsocket = new Socket(host, dport);
          dreader = new BufferedReader(new InputStreamReader(dsocket.getInputStream()));    	
          dwriter = new BufferedOutputStream(dsocket.getOutputStream());
    }
    
    /**
     * Disconnects from the FTP server.
     */
    public synchronized void disconnect() throws IOException {
        try {
            sendLine("QUIT");
        }
        finally {
            socket = null;
        }
    }
    
    
    /**
     * Returns the working directory of the FTP server it is connected to.
     */
    public synchronized String pwd() throws IOException {
        sendLine("PWD");
        String dir = null;
        String response = readLine();
        if (response.startsWith("257 ")) {
            int firstQuote = response.indexOf('\"');
            int secondQuote = response.indexOf('\"', firstQuote + 1);
            if (secondQuote > 0) {
                dir = response.substring(firstQuote + 1, secondQuote);
            }
        }
        return dir;
    }

    public static String getEntry(String listentry)
    {
    	int ind = listentry.indexOf(':');
    	if(ind==-1)
    	{
    		ind = 0;
    		int numsegsrem = 8;
    		while(numsegsrem>0)
    		{
    			char c = listentry.charAt(ind);
    			while(c!=' ')
    			{
    				c = listentry.charAt(ind);
    				ind++;
    			}
    			while(c==' ')
    			{
    				c = listentry.charAt(ind);
    				ind++;
    			}
    			numsegsrem--;
    		}
    		return listentry.substring(ind-1);
    	}
    	else
    	{
        	return listentry.substring(ind+4);    		
    	}
    }

    public static String getSize(String listentry)
    {
    		int ind = 0;
    		int numsegsrem = 4;
    		while(numsegsrem>0)
    		{
    			char c = listentry.charAt(ind);
    			while(c!=' ')
    			{
    				c = listentry.charAt(ind);
    				ind++;
    			}
    			while(c==' ')
    			{
    				c = listentry.charAt(ind);
    				ind++;
    			}
    			numsegsrem--;
    		}

    		int ind1 = ind;
    		char c = 't';
			while(c!=' ')
			{
				c = listentry.charAt(ind1);
				ind1++;
			}

    		return listentry.substring(ind-1,ind1-1);
    }

    public synchronized boolean list(GeneralMatrixString v) throws IOException 
    {    
        setupDataConnection();

        sendLine("LIST");
    	String response = readLine();
    	
		if(response.startsWith("211 "))
		{
		}
		else
		if(response.startsWith("150 "))
		{
		}
		else
		if(response.startsWith("451 "))
		{
			return false;
		}
		else
		{
			return false;
		}

		while(true)
    	{
    		response = dreadLine();
    		if(response==null)
    		{	
    			dreader.close();
    			dwriter.close();
    			dsocket.close();
    			
    	        response = readLine();
    	        boolean iscomplete = response.startsWith("226 ");
    			return iscomplete;
    		}
    		v.push_back(response);
    	}
    }

    public synchronized boolean list(String entry, GeneralMatrixString v) throws IOException 
    {    
        setupDataConnection();

        sendLine("LIST "+entry);
    	String response = readLine();
    	
		if(response.startsWith("211 "))
		{
		}
		else
		if(response.startsWith("150 "))
		{
		}
		else
		if(response.startsWith("451 "))
		{
			return false;
		}
		else
		{
			return false;
		}

		while(true)
    	{
    		response = dreadLine();
    		if(response==null)
    		{	
    			dreader.close();
    			dwriter.close();
    			dsocket.close();
    			
    	        response = readLine();
    	        boolean iscomplete = response.startsWith("226 ");
    			return iscomplete;
    		}
    		v.push_back(response);
    	}
    }

    public synchronized byte[] md5OfFile(String p,byte[] header,long timeoutMillis) throws Exception
    {
    	MessageDigest sha1 = MessageDigest.getInstance("SHA1");

    	//System.out.println("sha1 size="+readsize);
        setupDataConnection();

        sendLine("RETR "+p);
    	String response = readLine();

    	String md5tempfile = "temp.md5";
    	final Path destination = Paths.get(md5tempfile);
    	try (
    	    final InputStream in = dsocket.getInputStream();
    	) 
    	{
    	    Files.copy(in, destination,StandardCopyOption.REPLACE_EXISTING);
    	}

    	/*
    	try (
    	    final InputStream fis = dsocket.getInputStream();
    	) 
    	{
            byte[] dataBytes = new byte[16*1024];
        	
            int read = 0;

            long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;
        	//long readsize = f.length();
        	//System.out.println("sha1 size="+readsize);
        	boolean timeout = true;
        	//if(readsize!=0)
        	{
        		boolean readheader = false;
        		while (System.currentTimeMillis() < maxTimeMillis) 
        		{
    		         int readLength = dataBytes.length;
    		         //int readLength = java.lang.Math.min(fis.available(),dataBytes.length);
    		         if(readLength>0)
    		         {
        		         // can alternatively use bufferedReader, guarded by isReady():
        		         int readResult = fis.read(dataBytes, 0, readLength);
        		         if(!readheader)
        		         {
        		        	 System.arraycopy(dataBytes, 0, header, 0, 11);
	        		         readheader = true;
        		         }
        		         if(readResult == -1) 
        		         {
        		        	 timeout = false;
        		        	 break;
        		         }
        		         if(readResult>0)
        		        	 sha1.update(dataBytes, 0, readResult);
    		         }
    		         else
    		         {
        		         try
        		         {
        		        	 Thread.sleep(100);
        		         }
        		         catch(Exception e)
        		         {
        		        	 System.out.println(e.toString());
        		         }
    		         }
        		}		        		   
        	}
        	*/
		dreader.close();
		dwriter.close();
		dsocket.close();

        response = readLine();
        boolean iscomplete = response.startsWith("226 ");
    		//return iscomplete;

    	FileInputStream fis = new FileInputStream(md5tempfile);
        byte[] dataBytes = new byte[16*1024];

        int read = 0;

        long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;
        File f = new File(md5tempfile);
        long readsize = f.length();
    	System.out.println("sha1 size="+readsize);
    	boolean timeout = true;
    	if(readsize!=0)
    	{
    		boolean readheader = false;
    		while (System.currentTimeMillis() < maxTimeMillis) 
    		{
		         int readLength = dataBytes.length;
		         //int readLength = java.lang.Math.min(fis.available(),dataBytes.length);
		         if(readLength>0)
		         {
    		         // can alternatively use bufferedReader, guarded by isReady():
    		         int readResult = fis.read(dataBytes, 0, readLength);
    		         if(!readheader)
    		         {
    		        	 System.arraycopy(dataBytes, 0, header, 0, 11);
        		         readheader = true;
    		         }
    		         if(readResult == -1) 
    		         {
    		        	 timeout = false;
    		        	 break;
    		         }
    		         if(readResult>0)
    		        	 sha1.update(dataBytes, 0, readResult);
		         }
		         else
		         {
    		         try
    		         {
    		        	 Thread.sleep(100);
    		         }
    		         catch(Exception e)
    		         {
    		        	 System.out.println(e.toString());
    		         }
		         }
    		}		        		   
    	}

    	fis.close();
    	
		if((!timeout)&&(iscomplete))
    	{
            byte[] mdbytes = sha1.digest();
    		return mdbytes;
    	}
    	return null;
    }

    public synchronized boolean retrToFile(String p,String f) throws IOException 
    {
        setupDataConnection();

        sendLine("RETR "+p);
    	String response = readLine();
    	
    	final Path destination = Paths.get(f);
    	try (
    	    final InputStream in = dsocket.getInputStream();
    	) 
    	{
    	    Files.copy(in, destination,StandardCopyOption.REPLACE_EXISTING);
    	}
    	
		dreader.close();
		dwriter.close();
		dsocket.close();

        response = readLine();
        boolean iscomplete = response.startsWith("226 ");
		return iscomplete;
    }
    /**
     * Changes the working directory (like cd). Returns true if successful.
     */   
    public synchronized boolean cwd(String dir) throws IOException {
        sendLine("CWD " + dir);
        String response = readLine();
        return (response.startsWith("250 "));
    }
    
    
    /**
     * Sends a file to be stored on the FTP server.
     * Returns true if the file transfer was successful.
     * The file is sent in passive mode to avoid NAT or firewall problems
     * at the client end.
     */
    public synchronized boolean stor(File file) throws IOException {
        if (file.isDirectory()) {
            throw new IOException("SimpleFTP cannot upload a directory.");
        }
        
        String filename = file.getName();

        return stor(new FileInputStream(file), filename);
    }
    
    
    /**
     * Sends a file to be stored on the FTP server.
     * Returns true if the file transfer was successful.
     * The file is sent in passive mode to avoid NAT or firewall problems
     * at the client end.
     */
    public synchronized boolean stor(InputStream inputStream, String filename) throws IOException {

        BufferedInputStream input = new BufferedInputStream(inputStream);
        
//        sendLine("PASV");
//        String response = readLine();
//        if (!response.startsWith("227 ")) {
//            throw new IOException("SimpleFTP could not request passive mode: " + response);
//        }
//        
//        String ip = null;
//        int port = -1;
//        int opening = response.indexOf('(');
//        int closing = response.indexOf(')', opening + 1);
//        if (closing > 0) {
//            String dataLink = response.substring(opening + 1, closing);
//            StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
//            try {
//                ip = tokenizer.nextToken() + "." + tokenizer.nextToken() + "." + tokenizer.nextToken() + "." + tokenizer.nextToken();
//                port = Integer.parseInt(tokenizer.nextToken()) * 256 + Integer.parseInt(tokenizer.nextToken());
//            }
//            catch (Exception e) {
//                throw new IOException("SimpleFTP received bad data link information: " + response);
//            }
//        }
        
        setupDataConnection();
        
        sendLine("STOR " + filename);
        
//        Socket dataSocket = new Socket(ip, port);
        
        String response = readLine();
        System.out.println(""+response);
//        if (!response.startsWith("150 ")) {
//            throw new IOException("SimpleFTP was not allowed to send the file: " + response);
//        }
        
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
        while ((bytesRead = input.read(buffer)) != -1) {
            dwriter.write(buffer, 0, bytesRead);
        }
        dwriter.flush();
        dwriter.close();
		dreader.close();
		dsocket.close();
        input.close();
        
        response = readLine();
        boolean iscomplete = response.startsWith("226 ");
        return iscomplete;
    }


    /**
     * Enter binary mode for sending binary files.
     */
    public synchronized boolean bin() throws IOException {
        sendLine("TYPE I");
        String response = readLine();
        return (response.startsWith("200 "));
    }
    
    
    /**
     * Enter ASCII mode for sending text files. This is usually the default
     * mode. Make sure you use binary mode if you are sending images or
     * other binary data, as ASCII mode is likely to corrupt them.
     */
    public synchronized boolean ascii() throws IOException {
        sendLine("TYPE A");
        String response = readLine();
        return (response.startsWith("200 "));
    }
    
    
    /**
     * Sends a raw command to the FTP server.
     */
    private void sendLine(String line) throws IOException {
        if (socket == null) {
            throw new IOException("SimpleFTP is not connected.");
        }
        try {
            writer.write(line + "\r\n");
            writer.flush();
            if (DEBUG) {
                System.out.println("> " + line);
            }
        }
        catch (IOException e) {
            socket = null;
            throw e;
        }
    }
    
    private String readLine() throws IOException {
        String line = reader.readLine();
        if(line.length()==0)
        	line = reader.readLine();
        
        if (DEBUG) {
            System.out.println("< " + line);
        }
        return line;
    }
    private String dreadLine() throws IOException {
        String line = dreader.readLine();
        if(line==null)
        	return line;
        if(line.length()==0)
        	line = dreader.readLine();
        
        if (DEBUG) {
            System.out.println("< " + line);
        }
        return line;
    }
    
    private Socket socket = null;
    private BufferedReader reader = null;
    private Socket dsocket = null;
    private BufferedReader dreader = null;
    private BufferedWriter writer = null;
    BufferedOutputStream dwriter = null;
    
    int dport = -1;
    
    private static boolean DEBUG = false;
    
    
}