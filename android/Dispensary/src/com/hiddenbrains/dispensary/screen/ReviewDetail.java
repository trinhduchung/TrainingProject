package com.hiddenbrains.dispensary.screen;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hiddenbrains.dispensary.common.DispensaryConstant;

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
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class ReviewDetail extends Activity implements OnClickListener, Runnable{
	private ImageButton btn_dispansary_list,btn_search,btn_doctors,btn_back;
	
	private TextView txt_maintile,txt_subtile,review,postDate,description;
	ImageView rating_qua,rating_loc,rating_bat,rating_know,rating_price,rating_overall;
	private ProgressDialog pd;
	String main_title;
	private Builder builder;
	ArrayList<String> all_Data=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.review_details);
		
			txt_maintile=(TextView) findViewById(R.id.main_title);
			txt_subtile=(TextView) findViewById(R.id.subtitle);
			review=(TextView) findViewById(R.id.dl_reviewby_data);
			postDate=(TextView) findViewById(R.id.dl_posted_date);
			description=(TextView) findViewById(R.id.des_data);
			
			rating_overall=(ImageView) findViewById(R.id.overall_rating);
			rating_qua=(ImageView) findViewById(R.id.qua_rating);
			rating_bat=(ImageView) findViewById(R.id.bat_rating);
			rating_loc=(ImageView) findViewById(R.id.loc_rating);
			rating_know=(ImageView) findViewById(R.id.know_rating);
			rating_price=(ImageView) findViewById(R.id.price_rating);
		
			btn_dispansary_list=(ImageButton) findViewById(R.id.d_btn_dispansary);
	        btn_search=(ImageButton) findViewById(R.id.d_btn_search);
	        btn_doctors=(ImageButton) findViewById(R.id.d_btn_doctors);
	        btn_back=(ImageButton) findViewById(R.id.d_btn_back);
	        
	        btn_back.setOnClickListener(this);
	        btn_dispansary_list.setOnClickListener(this);
	        btn_search.setOnClickListener(this);
	        btn_doctors.setOnClickListener(this);
	        
	        ScrollView sc=(ScrollView) findViewById(R.id.scroll);
	        sc.setVerticalScrollBarEnabled(false);
	        
	        pd = ProgressDialog.show(this, "Please wait", "Loading...", true,false);
	        Thread thread = new Thread(this);
	        thread.start();
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
					case R.id.d_btn_back:
									finish();
								break;
					}
	
	 }
		
	public void run() {
			
			WifiManager wifi=(WifiManager) this.getSystemService(Context.WIFI_SERVICE);
			
			if(wifi.isWifiEnabled()||isOline(this)){
				
			Bundle bundle=getIntent().getExtras();
	        String review_id=bundle.getString("Review_id");
	        main_title=bundle.getString("title");
	        String Url=DispensaryConstant.REVIEW_DETAILS+"review_id="+review_id;
	        //+"latitude="+DispensaryConstant.latitude+"&longitude="+DispensaryConstant.longitude;
	        try{
	        URL url=new URL(Url);
	        URLConnection urlc=url.openConnection();
	        BufferedReader bfr=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
	        String line;
	        
	        while((line=bfr.readLine())!=null){
	        		JSONArray ja=new JSONArray(line);
	         for(int i=0;i<ja.length();i++){
	         			JSONObject jo=(JSONObject)ja.get(i); 
	         			all_Data.add(jo.getString("reviewer_name"));
	         			all_Data.add(jo.getString("review_date"));
	         			all_Data.add(jo.getString("overall_rating"));
	         			all_Data.add(jo.getString("location"));
	         			all_Data.add(jo.getString("budtenders"));
	         			all_Data.add(jo.getString("knowledge"));
	         			all_Data.add(jo.getString("price"));
	         			all_Data.add(jo.getString("med"));
	         			all_Data.add(jo.getString("description"));
	         			
	           }
	        }
	        }
	        catch(Exception e){
	        	e.getMessage();
	        }
		 handler.sendEmptyMessage(0);
		}
	else{
		pd.dismiss();
		try{
			builder = new AlertDialog.Builder(ReviewDetail.this);
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
//		txt_maintile.setText(main_title);
		txt_maintile.setText("Review Details");
	  	 txt_subtile.setText(main_title);
	  	 
      			if(all_Data.get(0).equals("")){
      				review.setVisibility(View.GONE);
      			}
      			else{
      				review.setText(all_Data.get(0));
	         				
      			}
      			if(all_Data.get(1).equals("")){
      				postDate.setVisibility(View.GONE);
      			}
      			else{
      				postDate.setText(all_Data.get(1));
 	         	}
      			if(all_Data.get(2).equalsIgnoreCase("rating-0.png")){
     				rating_overall.setImageResource(R.drawable.rating_0);
					}else if(all_Data.get(2).equalsIgnoreCase("rating-1.png")){
						rating_overall.setImageResource(R.drawable.rating_1);		
					}else if(all_Data.get(2).equalsIgnoreCase("rating-2.png")){
						rating_overall.setImageResource(R.drawable.rating_2);
					}else if(all_Data.get(2).equalsIgnoreCase("rating-3.png")){
						rating_overall.setImageResource(R.drawable.rating_3);
					}
					else if(all_Data.get(2).equalsIgnoreCase("rating-4.png"))
							{
						rating_overall.setImageResource(R.drawable.rating_4);
					}
					else if(all_Data.get(2).equalsIgnoreCase("rating-5.png")){
						rating_overall.setImageResource(R.drawable.rating_5);
					}	
      			
      			if(all_Data.get(3).equalsIgnoreCase("rating-0.png")){
      				rating_loc.setImageResource(R.drawable.rating_0);
					}else if(all_Data.get(3).equalsIgnoreCase("rating-1.png")){
						rating_loc.setImageResource(R.drawable.rating_1);		
					}else if(all_Data.get(3).equalsIgnoreCase("rating-2.png")){
						rating_loc.setImageResource(R.drawable.rating_2);
					}else if(all_Data.get(3).equalsIgnoreCase("rating-3.png")){
						rating_loc.setImageResource(R.drawable.rating_3);
					}
					else if(all_Data.get(3).equalsIgnoreCase("rating-4.png"))
							{
						rating_loc.setImageResource(R.drawable.rating_4);
					}
					else if(all_Data.get(3).equalsIgnoreCase("rating-5.png")){
						rating_loc.setImageResource(R.drawable.rating_5);
					}	
      			
      			if(all_Data.get(4).equalsIgnoreCase("rating-0.png")){
      				rating_bat.setImageResource(R.drawable.rating_0);
					}else if(all_Data.get(4).equalsIgnoreCase("rating-1.png")){
						rating_bat.setImageResource(R.drawable.rating_1);		
					}else if(all_Data.get(4).equalsIgnoreCase("rating-2.png")){
						rating_bat.setImageResource(R.drawable.rating_2);
					}else if(all_Data.get(4).equalsIgnoreCase("rating-3.png")){
						rating_bat.setImageResource(R.drawable.rating_3);
					}
					else if(all_Data.get(4).equalsIgnoreCase("rating-4.png"))
							{
						rating_bat.setImageResource(R.drawable.rating_4);
					}
					else if(all_Data.get(4).equalsIgnoreCase("rating-5.png")){
						rating_bat.setImageResource(R.drawable.rating_5);
					}	
      		
      			if(all_Data.get(5).equalsIgnoreCase("rating-0.png")){
      				rating_know.setImageResource(R.drawable.rating_0);
					}else if(all_Data.get(5).equalsIgnoreCase("rating-1.png")){
						rating_know.setImageResource(R.drawable.rating_1);		
					}else if(all_Data.get(5).equalsIgnoreCase("rating-2.png")){
						rating_know.setImageResource(R.drawable.rating_2);
					}else if(all_Data.get(5).equalsIgnoreCase("rating-3.png")){
						rating_know.setImageResource(R.drawable.rating_3);
					}
					else if(all_Data.get(5).equalsIgnoreCase("rating-4.png"))
							{
						rating_know.setImageResource(R.drawable.rating_4);
					}
					else if(all_Data.get(5).equalsIgnoreCase("rating-5.png")){
						rating_know.setImageResource(R.drawable.rating_5);
					}	
      			if(all_Data.get(6).equalsIgnoreCase("rating-0.png")){
      				rating_price.setImageResource(R.drawable.rating_0);
					}else if(all_Data.get(6).equalsIgnoreCase("rating-1.png")){
						rating_price.setImageResource(R.drawable.rating_1);		
					}else if(all_Data.get(6).equalsIgnoreCase("rating-2.png")){
						rating_price.setImageResource(R.drawable.rating_2);
					}else if(all_Data.get(6).equalsIgnoreCase("rating-3.png")){
						rating_price.setImageResource(R.drawable.rating_3);
					}
					else if(all_Data.get(6).equalsIgnoreCase("rating-4.png"))
							{
						rating_price.setImageResource(R.drawable.rating_4);
					}
					else if(all_Data.get(6).equalsIgnoreCase("rating-5.png")){
						rating_price.setImageResource(R.drawable.rating_5);
					}	
      		
      			if(all_Data.get(7).equalsIgnoreCase("rating-0.png")){
      				rating_qua.setImageResource(R.drawable.rating_0);
					}else if(all_Data.get(7).equalsIgnoreCase("rating-1.png")){
						rating_qua.setImageResource(R.drawable.rating_1);		
					}else if(all_Data.get(7).equalsIgnoreCase("rating-2.png")){
						rating_qua.setImageResource(R.drawable.rating_2);
					}else if(all_Data.get(7).equalsIgnoreCase("rating-3.png")){
						rating_qua.setImageResource(R.drawable.rating_3);
					}
					else if(all_Data.get(7).equalsIgnoreCase("rating-4.png"))
							{
						rating_qua.setImageResource(R.drawable.rating_4);
					}
					else if(all_Data.get(7).equalsIgnoreCase("rating-5.png")){
						rating_qua.setImageResource(R.drawable.rating_5);
					}	
     			if(all_Data.get(8).equals("")){
     				description.setVisibility(View.GONE);
     			}
     			else
     			{
     			description.setText(all_Data.get(8));
     			}
	  	    		  pd.dismiss();
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
