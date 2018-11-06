package web;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class BroadcastRecieveThread implements Runnable
{
	public int port;
	public ArrayList<String> broadcasters = new ArrayList<String>();
	public Thread t = null;

	public BroadcastRecieveThread(int port)
	{
		this.port = port;
		t = new Thread( this );
		t.setDaemon( true );
		t.start();
	}

	public synchronized void getQueue(ArrayList<String> q)
	{
	    q.addAll(broadcasters);
	    broadcasters.clear();
	}
	public synchronized void queue(String b)
	{
		broadcasters.add(b);
	}

	public void run()
	{
		try
		{
			DatagramSocket socket = new DatagramSocket(port, InetAddress.getByName("0.0.0.0"));	
			byte[] buf = new byte[2];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			while (true) {
			      System.out.println("Waiting for data");
			      socket.receive(packet);
			      queue(packet.getAddress().toString());
			      System.out.println("Data received");
			    }
		}
		catch(IOException e)
		{
			
		}
	}
}
