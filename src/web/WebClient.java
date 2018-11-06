package web;


import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class WebClient
{
	public String root = "http://localhost";
	public String rootSSL = "https://localhost";
	//the web requests to be processed by the client
	ArrayList<ArrayList<String> > queue = new ArrayList<ArrayList<String> >();

	public WebClient()
	{
	}
	
	public WebClient(String root)
	{
		this.root = root;
	}
	
	public static void setupSSL()
	{
//    	System.setProperty("javax.net.ssl.trustStore", f.getAbsolutePath());
//		keytool -import -alias gridserver -file gridserver.crt -storepass $PASS -keystore gridserver.keystore
//		
//		-Djavax.net.ssl.keyStoreType=pkcs12
//				-Djavax.net.ssl.trustStoreType=jks
//				-Djavax.net.ssl.keyStore=clientcertificate.p12
//				-Djavax.net.ssl.trustStore=gridserver.keystore
//				-Djavax.net.debug=ssl # very verbose debug
//				-Djavax.net.ssl.keyStorePassword=$PASS
//				-Djavax.net.ssl.trustStorePassword=$PASS		
		disableCertificateValidation();
	}
	public static void disableCertificateValidation() 
	{
	    // Create a trust manager that does not validate certificate chains
	    TrustManager[] trustAllCerts = new TrustManager[] 
	    { 
	      new TrustAllManager() 
	    };

	    // Ignore differences between given hostname and certificate hostname
	    HostnameVerifier hv = new TrustAllHostnameVerifier();
	    
	    // Install the all-trusting trust manager
	    try 
	    {
	      SSLContext sc = SSLContext.getInstance("SSL");
	      sc.init(null, trustAllCerts, new SecureRandom());
	      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	      HttpsURLConnection.setDefaultHostnameVerifier(hv);
	    } catch (Exception e) {}
	}

	public synchronized void getQueue(ArrayList<ArrayList<String> > q)
	{
	    q.addAll(queue);
	    queue.clear();
	}
	public synchronized void queue(ArrayList<String> e)
	{
		queue.add(e);
	}

	public void post(String path,HashMap<String,String> parameters)
	{
		String paramstring = "";
		Iterator<Map.Entry<String, String> > it = parameters.entrySet().iterator();
		boolean first = true;
	    while (it.hasNext()) {
	    	Map.Entry<String, String> pair = it.next();
			if(!first)
				paramstring += "&";
			paramstring += WebSecurity.encodeUri(pair.getKey())+"="+WebSecurity.encodeUri(pair.getValue());			
	    	first = false;
	    }

		new WebClientRequest(this,root+path,paramstring);
	}
	
	public String objAsString(String p, Object o)
	{
		if(o instanceof String)
		{
			return (String)o;
		}
//		else
//		if(o instanceof GeneralMatrixFloat)
//		{
//			return PropertyMatrixFloat.toString(p, (GeneralMatrixFloat)o);
//		}
//		else
//		if(o instanceof GeneralMatrixString)
//		{
//			return PropertyArrayString.toString(p, (GeneralMatrixString)o);
//		}
//		else
//		if(o instanceof Property)
//		{
//			String out = "";
//			GeneralMatrixString lines = new GeneralMatrixString(1);
//			((Property)o).SaveVerbose(lines);
//			for(int i=0;i<lines.height;i++)
//				out += lines.value[i]+"\n";
//			return out;
//		}
		return "null";
	}
	
	public void post(String path,String p0,File f)
	{
		new WebClientRequest(this,root+path,p0,f);
	}

	public void post(String path,String p0,Object o0,File f)
	{
		String res = "";
		res += WebSecurity.encodeUri(p0)+"="+WebSecurity.encodeUri(objAsString(p0,o0));
		new WebClientRequest(this,root+path,res,f);
	}

	public void post(String path,String p0,Object o0)
	{
		String res = "";
		res += WebSecurity.encodeUri(p0)+"="+WebSecurity.encodeUri(objAsString(p0,o0));
		new WebClientRequest(this,root+path,res);
	}
	
	public void post(String path,String p0,Object o0,String p1,Object o1,String p2,Object o2)
	{
		String res = "";
		res += WebSecurity.encodeUri(p0)+"="+WebSecurity.encodeUri(objAsString(p0,o0));
		res += "&";
		res += WebSecurity.encodeUri(p1)+"="+WebSecurity.encodeUri(objAsString(p1,o1));
		res += "&";
		res += WebSecurity.encodeUri(p2)+"="+WebSecurity.encodeUri(objAsString(p2,o2));
		new WebClientRequest(this,root+path,res);
	}
	
	public void post(String path)
	{
		new WebClientRequest(this,root+path,"");
	}
	
	public void postSSL(String path)
	{
		new WebClientRequest(this,rootSSL+path,"",true);
	}
	
//	public void run()
//	{
//		while(true)
//		{
//			//String urlParameters = "param1=a&param2=b&param3=c";
//			URL url = new URL("http://example.com/index.php");
//			URLConnection conn = url.openConnection();
//
//			conn.setDoOutput(true);
//
//			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
//
//			writer.write(urlParameters);
//			writer.flush();
//
//			String line;
//			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//
//			while ((line = reader.readLine()) != null) {
//			    System.out.println(line);
//			}
//			writer.close();
//			reader.close();  
//
////			String urlParameters = "param1=a&param2=b&param3=c";
////			String request = "http://example.com/index.php";
////			URL url = new URL(request); 
////			HttpURLConnection connection = (HttpURLConnection) url.openConnection();           
////			connection.setDoOutput(true);
////			connection.setDoInput(true);
////			connection.setInstanceFollowRedirects(false); 
////			connection.setRequestMethod("POST"); 
////			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); 
////			connection.setRequestProperty("charset", "utf-8");
////			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
////			connection.setUseCaches (false);
////	
////			DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
////			wr.writeBytes(urlParameters);
////			wr.flush();
////			wr.close();
////			connection.disconnect();
//		}
//	}
}