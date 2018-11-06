package web;

import java.io.OutputStream;

import rendering.RenderBuffer;
import importexport.image.pngj.ImageInfo;
import importexport.image.pngj.ImageLine;
import importexport.image.pngj.ImageLineHelper;
import importexport.image.pngj.PngWriter;

public class PNGOutputStream implements OutputStreamCallback
{
	public RenderBuffer img;
	public ImageInfo ii;
	public ImageLine il;
	public boolean inUse = false;
	
	public synchronized void out(WebRequest wr,Object o,OutputStream s)
	{
		inUse = true;
		try
		{
			PngWriter pwrite = new PngWriter(s, ii);
			il.setRown(0);
	
			for(int j=0;j<img.height;j++)
			{
				ImageLineHelper.setPixelsRGB8(il, img.pixel, j*img.width);
				pwrite.writeRow(il);
				il.incRown();
			}		
			pwrite.end();
		}
		catch(Exception e)
		{
		}
		inUse = false;
	}
}
