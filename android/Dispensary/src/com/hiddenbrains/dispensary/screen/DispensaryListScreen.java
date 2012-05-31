package com.hiddenbrains.dispensary.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hiddenbrains.dispensary.LazyAdapter.LazyAdapterMainList;
import com.hiddenbrains.dispensary.common.DispensaryConstant;

public class DispensaryListScreen extends Activity implements OnClickListener,Runnable 
{
	/** Called when the activity is first created. */
	public static ArrayList<String> title;
	private ArrayList<String> distance;
	private ArrayList<String> image;
	private ArrayList<String> online_image;
	private ArrayList<String> dispensary_id;
	public static ArrayList<String> address;
	public static ArrayList<String> lat=new ArrayList<String>();
	public static ArrayList<String> longt=new ArrayList<String>();
	public static ArrayList<String> icon_image=new ArrayList<String>();
	private ListView list;
	private LazyAdapterMainList lzm;
	private ImageButton btn_dispansary_list,btn_search,btn_doctors,btn_map;
	private  Builder builder;
	private ProgressDialog pd;
	private ImageButton btn_refresh;
	private boolean flag=false;
	private String position;
	private boolean fromOverlay;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dispensaries_list);
        Intent intent = getIntent();
        fromOverlay = intent.getBooleanExtra("from_overlay", false);
        if (fromOverlay) {
        	position = intent.getStringExtra("position");
        }
        if(DispensaryConstant.global_flag==2){
        	Intent intent1=new Intent(this,SearchScreen.class);
			startActivity(intent1);
        	finish();
        }else if(DispensaryConstant.global_flag==3){
        	Intent intent2=new Intent(this,Doctors_Clinic_List.class);
			startActivity(intent2);	
			finish();
        }else{
        	 btn_dispansary_list=(ImageButton) findViewById(R.id.d_btn_dispansary);
             btn_search=(ImageButton) findViewById(R.id.d_btn_search);
             btn_doctors=(ImageButton) findViewById(R.id.d_btn_doctors);
             btn_refresh=(ImageButton) findViewById(R.id.refresh);
             btn_map=(ImageButton) findViewById(R.id.d_btn_map);
 	        btn_map.setOnClickListener(this);
             
            list=(ListView) findViewById(R.id.d_list_view);
            
             btn_dispansary_list.setOnClickListener(this);
             btn_search.setOnClickListener(this);
             btn_doctors.setOnClickListener(this);
             btn_refresh.setOnClickListener(this);
         
     			pd = ProgressDialog.show(this, "Please wait", "Loading...", true,false);
     	        Thread thread = new Thread(this);
     	        thread.start();
        }
        
       
	    }
	@Override
    public void onResume()
	{
		super.onResume();
	}
	public void onClick(View v) {
		switch(v.getId()){
				
				case R.id.d_btn_dispansary:
								break;
				case R.id.d_btn_search:
					DispensaryConstant.global_flag=2;
					Intent intent1=new Intent(this,SearchScreen.class);
					startActivity(intent1);
					finish();
							break;
				case R.id.d_btn_doctors:
					DispensaryConstant.global_flag=3;
					Intent intent2=new Intent(this,Doctors_Clinic_List.class);
					startActivity(intent2);
					finish();
							break;
				case R.id.refresh:
					   pd = ProgressDialog.show(this, "Please wait", "Refreshing Data...", true,false);
				       Thread thread = new Thread(this);
				       thread.start();
					break;
				case R.id.d_btn_map:
					Bundle bundle=new Bundle();
					
					bundle.putInt("index",1);

					Intent intent=new Intent(this,MapScreenAll.class);
					intent.putExtras(bundle);
					startActivity(intent);
					 break;
		}
	}
	
	public void go_To_Details(int pos){
		try{
		String dis_data=dispensary_id.get(pos);
		String marker=icon_image.get(pos);
		String dis=distance.get(pos);
		Bundle bundle=new Bundle();
		bundle.putString("Dispensary_id",dis_data);
		bundle.putString("distance", dis);
		bundle.putString("marker", marker);
		Intent intent =new Intent(this,Dispansary_Detail.class);
		intent.putExtras(bundle);
		startActivity(intent);
		}
		catch(Exception e){
			e.getMessage();
		}
	}
	public void run() 
	{
		String str = DispensaryConstant.DISPENSARY_LIST+"latitude="+DispensaryConstant.latitude+"&longitude="+DispensaryConstant.longitude;
		if (fromOverlay) {
			String[] arrStr = position.split(",");
			if (arrStr.length >= 2) {
				str = DispensaryConstant.DISPENSARY_LIST+"latitude="+arrStr[0].trim()+"&longitude="+arrStr[1].trim();
				System.out.println(str);
			}
		}
			    WifiManager wifimanger=(WifiManager) this.getSystemService(Context.WIFI_SERVICE);
				
			    /****CehckIng Wifi or Network Connection****/
			    try
			    {
				    if(wifimanger.isWifiEnabled()||isOline(this)){
				    try
				    {   
		        	URL url=new URL(str);
		        	URLConnection urlc=url.openConnection();
		        	BufferedReader bfr=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
		        	String line;
		        	title=new ArrayList<String>();
		        	distance=new ArrayList<String>();
		        	image=new ArrayList<String>();
		        	online_image=new ArrayList<String>();
		        	
		        	dispensary_id=new ArrayList<String>();
		        	address=new ArrayList<String>();
		        	while((line=bfr.readLine())!=null)
		        	{
			        	JSONArray jsa=new JSONArray(line);
			        	for(int i=0;i<jsa.length();i++)
			        	{
			        			JSONObject jo=(JSONObject)jsa.get(i);
			        			dispensary_id.add(jo.getString("dispensary_id"));
			        			title.add(jo.getString("dispensary_name"));
			        			image.add(jo.getString("review"));
			        			distance.add(jo.getString("distance"));
			        			address.add(jo.getString("city_state"));
			        			lat.add(jo.getString("latitude"));
			        			longt.add(jo.getString("longitude"));
			        			icon_image.add(jo.getString("icon_image"));
			        				if(jo.getString("image").equalsIgnoreCase("noimage.png"))
			        				{
			        				online_image.add(DispensaryConstant.noImageConstant);
			        				}
			        				else
			        				{
			        				online_image.add(jo.getString("image"));
			        				}
		      			}
		        	}
			    }
			    catch(MalformedURLException e)
			    {
			    	e.printStackTrace();
			    	flag=true;
			    }
			    catch(IOException e)
			    		{
			    	e.printStackTrace();
			    	flag=true;
			    		}
			    catch(JSONException e1)
			    	{
			    	flag=true;
			    	e1.printStackTrace();
			    	}
			    handler.sendEmptyMessage(0);
			    }                                 // Connection If Closed
			 else{
				 pd.dismiss();
				 try{
					builder = new AlertDialog.Builder(DispensaryListScreen.this);
					builder.setTitle("Connection Support");
					builder.setMessage("Connection not available");
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					handler1.sendEmptyMessage(0);
				 	}
				 catch(Exception e){
					 e.getMessage();
				 }
			 }
		   }
		  catch(Exception e){
			 e.getMessage();
		  }
				 
	}
	
	private Handler handler1=new Handler(){
		 @Override
	      public void handleMessage(Message msg) 
	      {
				AlertDialog alert = builder.create();
			    alert.show();
	      }
	};

	private Handler handler = new Handler() 
	{
	      @Override
	      public void handleMessage(Message msg) 
	      {
	    	  if(flag){
	    		    builder = new AlertDialog.Builder(DispensaryListScreen.this);
					builder.setTitle("Connection Support");
					builder.setMessage("Server Not supporting,try later");
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
					builder.show();
	    	  }else{
	    	  lzm = new LazyAdapterMainList(DispensaryListScreen.this,title,distance,image,online_image,address,icon_image);
	          try
	          {
	        	  list.setAdapter(lzm);
	          }
	          catch(Exception e){
	          	e.getMessage();
	          }
	          list.setOnItemClickListener(new  OnItemClickListener() {

	  			public void onItemClick(AdapterView<?> parent, View v,int position, long id) {
	  					go_To_Details(position);
	  						}
	  		});
	    	 }
	    	  pd.dismiss();
	    	  
	      }
	
	};
	private boolean isOline(Context context){
		try{
			ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(cm==null)
				return false;
			NetworkInfo info=cm.getActiveNetworkInfo();
			if(info==null)
				return false;
			return info.isConnectedOrConnecting();
		}
		catch(Exception e){
			e.getMessage();
			return false;
		}
	}
	
}