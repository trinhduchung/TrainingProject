package com.hiddenbrains.dispensary.screen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;

import com.hiddenbrains.dispensary.LazyAdapter.LazyAdapterForReview;
import com.hiddenbrains.dispensary.common.DispensaryConstant;

public class ReviewScreen extends Activity implements OnClickListener, Runnable{
    ImageButton btn_dispansary_list,btn_search,btn_doctors,btn_back;
    ArrayList<String> title=new ArrayList<String>();
    ArrayList<String> reviewby=new ArrayList<String>();
    ArrayList<String> posteddate=new ArrayList<String>();
    ArrayList<String> rating=new ArrayList<String>();
    ArrayList<String> review_id=new ArrayList<String>();
    private  ListView list;
    private LazyAdapterForReview lzm;
    private ProgressDialog pd;
    private int view_flag=0;
    private Builder builder;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review_list);
		view_flag=1;
		btn_dispansary_list=(ImageButton) findViewById(R.id.d_btn_dispansary);
	        btn_search=(ImageButton) findViewById(R.id.d_btn_search);
	        btn_doctors=(ImageButton) findViewById(R.id.d_btn_doctors);
	        btn_back=(ImageButton) findViewById(R.id.rl_btn_back);
	        
	        btn_dispansary_list.setOnClickListener(this);
	        btn_search.setOnClickListener(this);
	        btn_doctors.setOnClickListener(this);
	        btn_back.setOnClickListener(this);
	        if(view_flag==1){
				pd = ProgressDialog.show(this, "Please wait", "Loading...", true,false);
		        Thread thread = new Thread(this);
		        thread.start();
		        view_flag=0;
			}
	        
	}
	
	
	public void go_To_Details(int pos){
		try{
		Bundle bundle=new Bundle();
		bundle.putString("Review_id",review_id.get(pos));
		bundle.putString("title",title.get(pos));
		Intent intent =new Intent(this,ReviewDetail.class);
		intent.putExtras(bundle);
		startActivity(intent);
		}
		catch(Exception e){
			e.getMessage();
		}
	}
	
	public void onClick(View v) {
			switch(v.getId()){
					
					case R.id.d_btn_dispansary:
									Intent intent=new Intent(this,DispensaryListScreen.class);
									intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
									break;
					case R.id.d_btn_search:
										if(DispensaryConstant.global_flag==3){
											Intent intent1=new Intent(this,Doctors_Clinic_List.class);
											intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent1);
											DispensaryConstant.global_flag=2;
										}
										else if(DispensaryConstant.global_flag==0){
										Intent intent1=new Intent(this,DispensaryListScreen.class);
										intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent1);
										DispensaryConstant.global_flag=2;
										}
										else if(DispensaryConstant.global_flag==2){
											Intent intent1=new Intent(this,SearchScreen.class);
											intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											startActivity(intent1);
											DispensaryConstant.global_flag=2;
											}
								    break;
					case R.id.d_btn_doctors:
									
								   if(DispensaryConstant.global_flag==3){
										Intent intent1=new Intent(this,Doctors_Clinic_List.class);
										intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent1);
										DispensaryConstant.global_flag=3;
									}
									else if(DispensaryConstant.global_flag==0){
									Intent intent1=new Intent(this,DispensaryListScreen.class);
									intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent1);
									DispensaryConstant.global_flag=3;
									}
									else if(DispensaryConstant.global_flag==2){
										Intent intent1=new Intent(this,SearchScreen.class);
										intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent1);
										DispensaryConstant.global_flag=3;
										}
								     break;
					case R.id.rl_btn_back:
									finish();
					 				break;
								   
			}
		}
	
	@Override
    public void onResume()
	{
		super.onResume();
	}
	
	public void run() {
		
		WifiManager wifi=(WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		
		if(wifi.isWifiEnabled()||isOline(this)){
		
		Bundle bundle=getIntent().getExtras();
        String clinic_id=bundle.getString("clinic_id");
       // String clinic_name=bundle.getString("clinic_name");
        String jsonUrl=DispensaryConstant.REVIEW_LIST+"dispensary_id="+clinic_id;
        try{
        URL url=new URL(jsonUrl);
        URLConnection urlc=url.openConnection();
        BufferedReader bfr=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        String line;
        
        while((line=bfr.readLine())!=null){
        		JSONArray ja=new JSONArray(line);
        		
        		 for(int i=0;i<ja.length();i++){
        			JSONObject jo=(JSONObject)ja.get(i); 
        			title.add(jo.getString("title"));
        			reviewby.add(jo.getString("reviewer_name"));
        			posteddate.add(jo.getString("review_date"));
        			review_id.add(jo.getString("review_id"));
        			if((jo.getString("overall_rating").toString()).equalsIgnoreCase("rating-0.png")){
						  rating.add("1");	
						}else if((jo.getString("overall_rating").toString()).equalsIgnoreCase("rating-1.png")){
							  rating.add("1");		
						}else if(jo.getString("overall_rating").toString().equalsIgnoreCase("rating-2.png")){
							  rating.add("2");	
						}else if((jo.getString("overall_rating").toString()).equalsIgnoreCase("rating-3.png")){
							  rating.add("3");	
						}else if((jo.getString("overall_rating").toString()).equalsIgnoreCase("rating-4.png")){
							  rating.add("4");	
						}else if((jo.getString("overall_rating").toString()).equalsIgnoreCase("rating-5.png")){
							  rating.add("5");	
						}	
        		 }
         }
        }
        catch(IOException e){
        	e.getMessage();
        }
        catch(Exception e){
        	e.getMessage();
        }
        handler.sendEmptyMessage(0);
		}
		else{
			pd.dismiss();
			try{
				builder = new AlertDialog.Builder(ReviewScreen.this);
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
	    	  
	    	  list = (ListView) findViewById(R.id.d_list_view);
	    	  if(title.size()==0){
	    		  pd.dismiss();
	    		  builder = new AlertDialog.Builder(ReviewScreen.this);
				  builder.setMessage("No Review Available");
				  builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
				  builder.show();
	    	  }else{
	  		  lzm=new LazyAdapterForReview(ReviewScreen.this,title,reviewby,posteddate,rating);
	          list.setAdapter(lzm);
	          list.setOnItemClickListener(new  OnItemClickListener() {

	  			public void onItemClick(AdapterView<?> parent, View v,int position, long id) {
	  					go_To_Details(position);
	  						}
	  		});
	      	pd.dismiss();
	      }
	      }
	
	};
	
	 @SuppressWarnings("static-access")
		private boolean isOline(Context context){
			try{
				ConnectivityManager cm=(ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
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
	

