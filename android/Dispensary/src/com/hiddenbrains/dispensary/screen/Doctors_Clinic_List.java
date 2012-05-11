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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hiddenbrains.dispensary.LazyAdapter.LazyAdapterDoctorClinic;
import com.hiddenbrains.dispensary.common.DispensaryConstant;

public class Doctors_Clinic_List extends Activity implements OnClickListener, Runnable{
	private ImageButton btn_dispansery_list,btn_search,btn_doctors;
	
	public static boolean flag=false;
	private ImageButton btn_s_from_now;
	private ArrayList<String> title;
	private ArrayList<String> address;
	private ArrayList<String> distance;
	private ArrayList<String> phone;
	private ArrayList<String> dis_list_id=new ArrayList<String>();
	public  static ArrayList<String> lat=new ArrayList<String>();
	public  static ArrayList<String> longt=new ArrayList<String>();
	private Builder builder=null;
	private String msge="";
	private  String url=null;
	private ProgressDialog pd;
	private ListView list;
	private ImageButton btn_refresh;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		try{
	    setContentView(R.layout.clinic_list);
	    list=(ListView) findViewById(R.id.list);
		}
		catch(Exception e){
			e.getMessage();
		 }
		if(DispensaryConstant.global_flag==0){
			Intent int1=new Intent(this,DispensaryListScreen.class);
			int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(int1);
			finish();
		}
		else if(DispensaryConstant.global_flag==2){
			Intent int1=new Intent(this,SearchScreen.class);
			int1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(int1);
			finish();
		}else{
			btn_s_from_now=(ImageButton) findViewById(R.id.clinic_search);
			btn_dispansery_list=(ImageButton) findViewById(R.id.d_btn_location);
	        btn_doctors=(ImageButton) findViewById(R.id.d_btn_doctors);
	        btn_search=(ImageButton) findViewById(R.id.d_btn_search);
	        btn_refresh=(ImageButton) findViewById(R.id.refresh);
	        btn_refresh.setOnClickListener(this);
	        
	       
	        
	        btn_doctors.setOnClickListener(this);
	        btn_dispansery_list.setOnClickListener(this);
	        btn_search.setOnClickListener(this);
	        btn_s_from_now.setOnClickListener(this);
	        
	       pd = ProgressDialog.show(this, "Please wait", "Loading...", true,false);
	       Thread thread = new Thread(this);
	       thread.start();
		}
		//DispensaryConstant.global_flag=0;
		
        
  	}

	
	@Override
    public void onResume()
	{
		super.onResume();
		
	}
	public void onClick(View v){ 

	switch(v.getId()){
		
		case R.id.d_btn_location:
			Intent intent=new Intent(this,DispensaryListScreen.class);
			startActivity(intent);
			DispensaryConstant.global_flag=0;
			finish();
					break;
		case R.id.d_btn_search:
			Intent intent1=new Intent(this,SearchScreen.class);
			DispensaryConstant.global_flag=2;
			startActivity(intent1);
			finish();
					break;
		case R.id.d_btn_doctors:
					break;
		case R.id.clinic_search: 
			Intent inten=new Intent(this,SearchFromClinicList.class);
			startActivity(inten);     
			break;	
		case R.id.refresh:
			   pd = ProgressDialog.show(this, "Please wait", "Refreshing Data...", true,false);
		       Thread thread = new Thread(this);
		       thread.start();
			break;
		
		}	
		
	}


	public void run() {
		
		WifiManager wifi=(WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		
		if(wifi.isWifiEnabled() || isOline(this)){
		
		if(flag==false)
		{
	        url=DispensaryConstant.Clinic_List+"latitude="+DispensaryConstant.latitude+"&longitude="+DispensaryConstant.longitude;
		}
	    else
	    {
	        	Bundle bundle=getIntent().getExtras();
	        	String city_name=bundle.getString("city");
	        	url=DispensaryConstant.Clinic_List+"latitude="+DispensaryConstant.latitude+"&longitude="+DispensaryConstant.longitude+"&keyword="+city_name;
	     }
	    try
	    {
	        	URL murl=new URL(url);
	        	URLConnection urlc=murl.openConnection();
	        	urlc.connect();
	        	BufferedReader bfr=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
	        	String line;
	        //	boolean flag=false;
	        	title=new ArrayList<String>();
	 	        address=new ArrayList<String>();
	 			distance=new ArrayList<String>();
	 			phone=new ArrayList<String>();
	        	while((line=bfr.readLine())!=null)
	        	{
	        	  JSONArray jsa=new JSONArray(line);
	        	  JSONObject jo1=(JSONObject)jsa.get(0);
	        	  String suc=jo1.getString("success");
	        	  if(suc.equals("0")){
						msge=jo1.getString("message");
      			   }else if(suc.equals("1")){{
	        		for(int i=0;i<jsa.length();i++)
	        		{
	        			JSONObject jo=(JSONObject)jsa.get(i);
	        			dis_list_id.add(jo.getString("clinic_id"));
	        			title.add(jo.getString("clinic_name"));
	        			address.add(jo.getString("address"));
	        			distance.add(jo.getString("distance"));
	        			phone.add(jo.getString("phone"));
	        			lat.add(jo.getString("latitude"));
	        			longt.add(jo.getString("longitude"));
						}
				    }
      			   }
	        	}
	        	
	        		
	        }
	        catch(MalformedURLException e)
	        {
	        	e.printStackTrace();
	        }
	        catch(IOException e)
	        {
	        	e.printStackTrace();
	        }
	        catch(JSONException e1)
	        {
	        	e1.printStackTrace();
	        	finish();
	        }
	        if(title.size()==0){
	        	handler1.sendEmptyMessage(0);
	        }else{
	        	handler.sendEmptyMessage(0);
	        }
		}               //  -------------------------------------------------------------Connection if closed
		else{
			pd.dismiss();
			try{
				builder = new AlertDialog.Builder(Doctors_Clinic_List.this);
				builder.setTitle("Connection Support");
				builder.setMessage("Connection not available");
				builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
				    finish();
					}
				});
				handler2.sendEmptyMessage(0);
			 	}
			 catch(Exception e){
				 e.getMessage();
			 }
		}
	}
	private Handler handler = new Handler() 
	{
	      @Override
	      public void handleMessage(Message msg) 
	      {
	    	  LazyAdapterDoctorClinic ddl=new LazyAdapterDoctorClinic(Doctors_Clinic_List.this, title, address, distance, phone);
	    	  list.setAdapter(ddl);
	    	  pd.dismiss();  
	    	  flag=false;
	      }
	}; 
	
	private Handler handler1 = new Handler() 
	{
	      @Override
	      public void handleMessage(Message msg) 
	      {   
	    	pd.dismiss();  
    	    flag=false;
	    	builder = new AlertDialog.Builder(Doctors_Clinic_List.this);
		    builder.setTitle("No Records");
			builder.setMessage(msge);
			builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					finish();
				}});
	    	  AlertDialog alert = builder.create();
		      alert.show();
	      }
	};
		
	private Handler handler2 = new Handler() 
	{
	      @Override
	      public void handleMessage(Message msg) 
	      {   
	    	  AlertDialog alert=builder.create();
	    	  alert.show();
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
