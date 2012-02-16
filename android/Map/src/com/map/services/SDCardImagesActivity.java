package com.map.services;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.map.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * Displays images from an SD card.
 */
public class SDCardImagesActivity extends Activity {

    /**
     * Cursor used to access the results from querying for images on the SD card.
     */
    private Cursor cursor;
    /*
     * Column index for the Thumbnails Image IDs.
     */
    private int columnIndex;
    static String extStorageDirectory;
    static Bitmap bm;
    String image_URL=
    		 "http://www.fortux.com/images/icon_objectives.gif";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdcard);
        extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        //ImageView bmImage = (ImageView)findViewById(R.id.image);
        BitmapFactory.Options bmOptions;
        bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;
        bm = LoadImage(image_URL, bmOptions);
        //bmImage.setImageBitmap(bm);
        loadImageFromUrl("http://www.fortux.com/images/icon_objectives.gif");
        // Set up an array of the Thumbnail Image ID column we want
        String[] projection = {MediaStore.Images.Thumbnails._ID};
        // Create the cursor pointing to the SDCard
        cursor = managedQuery( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
        		projection, // Which columns to return
        		null,       // Return all rows
        		null,
        		MediaStore.Images.Thumbnails.IMAGE_ID);
        // Get the column index of the Thumbnails Image ID
        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

        GridView sdcardImages = (GridView) findViewById(R.id.sdcard);
        sdcardImages.setAdapter(new ImageAdapter(this));

        // Set up a click listener
        sdcardImages.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // Get the data location of the image
                String[] projection = {MediaStore.Images.Media.DATA};
                cursor = managedQuery( MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection, // Which columns to return
                        null,       // Return all rows
                        null,
                        null);
                columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToPosition(position);
                // Get image filename
                String imagePath = cursor.getString(columnIndex);
                // Use this path to do further processing, i.e. full screen display
            }
        });
    }

    /**
     * Adapter for our image files.
     */
    private class ImageAdapter extends BaseAdapter {

        private Context context;

        public ImageAdapter(Context localContext) {
            context = localContext;
        }

        public int getCount() {
            return cursor.getCount();
        }
        public Object getItem(int position) {
            return position;
        }
        public long getItemId(int position) {
            return position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView picturesView;
            if (convertView == null) {
                picturesView = new ImageView(context);
                // Move cursor to current position
                cursor.moveToPosition(position);
                // Get the current value for the requested column
                int imageID = cursor.getInt(columnIndex);
                // Set the content of the image based on the provided URI
                picturesView.setImageURI(Uri.withAppendedPath(
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));
                picturesView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                picturesView.setPadding(8, 8, 8, 8);
                picturesView.setLayoutParams(new GridView.LayoutParams(100, 100));
            }
            else {
                picturesView = (ImageView)convertView;
            }
            return picturesView;
        }
    }
    public static void loadImageFromUrl(String url) {
    	   OutputStream outStream = null;
    	   File file = new File(extStorageDirectory, "er.PNG");
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
    private InputStream OpenHttpConnection(String strURL) throws IOException{
    	 InputStream inputStream = null;
    	 URL url = new URL(strURL);
    	 URLConnection conn = url.openConnection();

    	 try{
    	  HttpURLConnection httpConn = (HttpURLConnection)conn;
    	  httpConn.setRequestMethod("GET");
    	  httpConn.connect();

    	  if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    	   inputStream = httpConn.getInputStream();
    	  }
    	 }
    	 catch (Exception ex)
    	 {
    	 }
    	 return inputStream;
    	}

}