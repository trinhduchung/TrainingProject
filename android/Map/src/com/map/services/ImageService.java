package com.map.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ImageService {
	
	String extStorageDirectory;
	Bitmap bm;
	String image_URL;
	public ImageService(){

	}
	public void SaveImageFromUrlToSDCard(String url, String name)
	{
		
		   image_URL = url;
		   extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		   BitmapFactory.Options bmOptions;
		   bmOptions = new BitmapFactory.Options();
		   bmOptions.inSampleSize = 1;
		   bm = LoadImage(image_URL, bmOptions);
		   OutputStream outStream = null;
		   File file = new File(extStorageDirectory, name+".PNG");
		   try {
			   outStream = new FileOutputStream(file);
			   bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			   outStream.flush();
			   outStream.close();
		   } catch (FileNotFoundException e) {
		    // TODO Auto-generated catch block
			   e.printStackTrace();

		   } catch (IOException e) {
		    // TODO Auto-generated catch block
			   e.printStackTrace();
		   }
	}
	public Bitmap getImageFromSDCard(String imageInSD)
	{
		
		Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
	    return bitmap;
	}
	 private Bitmap LoadImage(String URL, BitmapFactory.Options options)
	 {      
		 Bitmap bitmap = null;
		 InputStream in = null;      
	      try {
	          in = OpenHttpConnection(URL);
	          bitmap = BitmapFactory.decodeStream(in, null, options);
	          in.close();
	      } catch (IOException e1) {
	      }
	      return bitmap;              
	 }
	 private InputStream OpenHttpConnection(String strURL) throws IOException
	 {
		 InputStream inputStream = null;
		 URL url = new URL(strURL);
		 URLConnection conn = url.openConnection();

		 try
		 {
			 HttpURLConnection httpConn = (HttpURLConnection)conn;
			 httpConn.setRequestMethod("GET");
			 httpConn.connect();

			 if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) 
			 {
				 inputStream = httpConn.getInputStream();
			 }
		 }
		 catch (Exception ex)
		 {
			 
		 }
		 return inputStream;
	}
}
