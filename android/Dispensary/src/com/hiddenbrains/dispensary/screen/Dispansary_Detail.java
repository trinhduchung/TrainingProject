package com.hiddenbrains.dispensary.screen;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hiddenbrains.dispensary.MapRoute.MapRouteActivity;
import com.hiddenbrains.dispensary.common.DispensaryConstant;
import com.hiddenbrains.dispensary.common.Utils;


public class Dispansary_Detail extends Activity implements android.view.View.OnClickListener, Runnable{
	private ImageButton btn_location,btn_search,btn_doctors;
	private ImageButton btn_back_mainscreen,btn_map;
    private	ImageButton btn_review,btn_addtocontact,btn_getDir;
	private TextView txt_add,txt_phone,txt_url,txt_mail,txt_dis,txt_des,txt_title;
	
	private int review_count;
	
	private ImageView img_rating,img_main;
	private WebView webview;
	private Bitmap image_view;
	private String html_data,s;
	private Builder builder;
	private ProgressDialog pd;
	
	private String phone_no;
	
	String dis_id;
	private String mDispensaryId;
	private String marker;
	private File cacheDir;
	private String strurl="http://www.thcfinder.com/disp/";
    private String temp="";
    
    boolean flag = true;
    int view_flag=0;
    Vector<String> data = new Vector<String>();
     
    TextView txt1,txt2,txt3,txt4,txt5,txt6;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dispensary_details);
		view_flag=1;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
    	{
           cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"LazyList");
    	}
        else
        {
        	cacheDir = this.getApplicationContext().getCacheDir();
        }
    	
    	if(!cacheDir.exists()){
    		cacheDir.mkdir();
    	}
//**********************************************************************************************    	
    	
    	webview = (WebView) findViewById(R.id.webkitWebView1);
    	webview.setFocusableInTouchMode(false);
    	webview.getSettings().setJavaScriptEnabled(true);
    	webview.setVerticalScrollBarEnabled(false);
    	webview.setHorizontalScrollBarEnabled(false);
    	webview.addJavascriptInterface(this, "interface");
    	webview.addJavascriptInterface(this, "contactSupport");
//    	webview.setClickable(false);
    	webview.setFocusable(true);
//    	webview.setTouchDelegate(null);
    	
    	webview.setOnTouchListener(new View.OnTouchListener() {
    	    @Override
    	    public boolean onTouch(View v, MotionEvent event) {
    	    	WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
				boolean values=false;
				
				try{
				s = hr.getExtra();
				String extra = "~LinkAction~sendemail~email";
				String extra1 = "~LinkAction~phone~"+data.elementAt(4).toString();
				String extra2 = "~LinkAction~openbrowser~"+data.elementAt(5).toString();
				if(event.getAction()==1)
				{
					if(s.equals(extra))
					{
						try 
						{
					    	String str1=data.elementAt(6).toString();
					    	Log.d("DS",s +" Checking! : "+str1);
					    	Intent i = new Intent(android.content.Intent.ACTION_SEND);
					    	i.setType("text/plain");
					    	i.putExtra(android.content.Intent.EXTRA_EMAIL  , new String[]{str1});
					    	i.putExtra(android.content.Intent.EXTRA_SUBJECT, "subject of email");
					    	i.putExtra(android.content.Intent.EXTRA_TEXT  , "body of email");
					    	startActivity(android.content.Intent.createChooser(i, "Send mail..."));
//					    	flag = true;
					    	values = true;
					    	webview.invalidate();
					    	webview.clearFocus();
//					    	webview.setNextFocusUpId(R.id.btn_back);
					    	webview.computeScroll();
					    	
					    } 
						catch (android.content.ActivityNotFoundException e) 
				    	{
				    	    e.getMessage();
				    	}
					}else if(s.equals(extra1))
					{
						try
						{
							
					    	Intent callIntent = new Intent(Intent.ACTION_CALL);
							String str="tel:"+data.elementAt(4).toString();
						    callIntent.setData(Uri.parse(str));
						    startActivity(callIntent);
						    Log.d("DS",s +" Checking : "+str);
						    values = true;
						   
						    webview.clearFocus();
						    webview.invalidate();
//						    webview.setNextFocusDownId(R.id.btn_back);
						    webview.computeScroll();
					    }
						catch(Exception e)
						{
				    		e.getMessage();
				    	}
					}else if(s.equals(extra2))
					{
						try
						{
							Intent webIntent = new Intent(Intent.ACTION_VIEW);
							
							String str=""+data.elementAt(5).toString();
							
							
							webIntent.setData(Uri.parse(str));
						    startActivity(webIntent);
						    
						    values = true;
						    webview.clearFocus();
						    webview.invalidate();
//						    webview.setNextFocusDownId(R.id.btn_back);
						    webview.computeScroll();
					    }
						catch(Exception e)
						{
				    		e.getMessage();
				    	}
					}
					else
					{
						values = true;
					}
				}
				}catch (Exception e) {
					values=true;
				}
				
    	    	return values;
    	    }
    	});

    	
    	
//    	class MainScreenActivity extends WebViewClient {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return false                ;
//            }
//        }
//    	
//    	webview.setWebViewClient(new MainScreenActivity());

    	
    	
//    	webview.setOnTouchListener(new View.OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent paramMotionEvent) {
//				// TODO Auto-generated method stub
//				WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
//				boolean values=false;
//				
//				
//				
//				
//				try {
//					//
//					try {
//						if (hr.getExtra().equals(null)) {
//
//						}
//					} catch (Exception e) {
//						values = false;
//						
//						
//					}
//					
//
//					s = hr.getExtra();
//					
////					int it = hr.getType();
//					String extra = "~LinkAction~sendemail~email";
//					String extra1 = "~LinkAction~phone~"+data.elementAt(4).toString();
//					if(paramMotionEvent.getAction()!=0)
//					{
//						if(s.equals(extra))
//						{
//							try 
//							{
//								
//						    	String str1=data.elementAt(6).toString();
//						    	  Log.d("DS",s +" Checking! : "+str1);
//						    	Intent i = new Intent(android.content.Intent.ACTION_SEND);
//						    	i.setType("text/plain");
//						    	i.putExtra(android.content.Intent.EXTRA_EMAIL  , new String[]{str1});
//						    	i.putExtra(android.content.Intent.EXTRA_SUBJECT, "subject of email");
//						    	i.putExtra(android.content.Intent.EXTRA_TEXT  , "body of email");
//						    	startActivity(android.content.Intent.createChooser(i, "Send mail..."));
////						    	flag = true;
//						    	values = true;
//						    } 
//							catch (android.content.ActivityNotFoundException e) 
//					    	{
//					    	    e.getMessage();
//					    	}
//						}else if(s.equals(extra1))
//						{
//							try
//							{
//								
//						    	Intent callIntent = new Intent(Intent.ACTION_CALL);
//								String str="tel:"+phone_no;
//							    callIntent.setData(Uri.parse(str));
//							    startActivity(callIntent);
//							    Log.d("DS",s +" Checking : "+str);
////							    flag = true;
//							    values = true;
//						    }
//							catch(Exception e)
//							{
//					    		e.getMessage();
//					    	}
//						}else{
//							values = true;
//						}
//					}
//					
//				} catch (Exception e) {
//					// TODO: handle exception
//					Log.v("log_tag", "Exception: " + e);
//					values = false ;
//				}
//				return values;
//				// }
//
//			}
//		});

//    	webview.setWebViewClient(new MyWebViewClient());
		// webView.setPadding(0, 0, 0, 0);
		// webView.setInitialScale(getScale());
//    	webview.setInitialScale(30);
//
//    	webview.setInitialScale(30);
		

    	
//**********************************************************************************************
    	

		ScrollView sc=(ScrollView) findViewById(R.id.scroll);
		sc.setVerticalScrollBarEnabled(false);
		
		try{
		btn_back_mainscreen=(ImageButton) findViewById(R.id.d_btn_back);
		btn_map=(ImageButton) findViewById(R.id.d_btn_map);
		btn_back_mainscreen.setOnClickListener(this);
		btn_getDir=(ImageButton) findViewById(R.id.d_btn_direction);
		btn_getDir.setOnClickListener(this);
		btn_map.setOnClickListener(this);
		}
		catch(Exception e){
			e.getMessage();
		}
		
		((ImageButton) findViewById(R.id.d_btn_menu)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Dispansary_Detail.this, MenuScreen.class);
				intent.putExtra("id", mDispensaryId);
				startActivity(intent);
			}
		});
		
		btn_review=(ImageButton) findViewById(R.id.d_btn_review);
		btn_addtocontact=(ImageButton) findViewById(R.id.d_btn_add);
		btn_review.setOnClickListener(this);
		btn_addtocontact.setOnClickListener(this);
		
		btn_location=(ImageButton) findViewById(R.id.d_btn_location);
		btn_search=(ImageButton) findViewById(R.id.d_btn_search);
		btn_doctors=(ImageButton) findViewById(R.id.d_btn_doctors);
		
		btn_doctors.setOnClickListener(this);
		btn_location.setOnClickListener(this);
		btn_search.setOnClickListener(this);
		if(view_flag==1){
		pd = ProgressDialog.show(this, "Please wait", "Loading...", true,false);
		Thread thread = new Thread(this);
		thread.start();
		
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
	    	webview.goBack();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
    public void onResume()
	{
		super.onResume();
	}
	
	public void run(){
				WifiManager wifi=(WifiManager) this.getSystemService(Context.WIFI_SERVICE);
				if(wifi.isWifiEnabled()||isOline(this)){
		
				try {
	 			Bundle bundle=getIntent().getExtras();
	 			dis_id=bundle.getString("Dispensary_id");
	 	   	 	data.addElement(bundle.getString("Dispensary_id"));
	 	   	 	marker=bundle.getString("marker");
	 	   	 	data.addElement(bundle.getString("distance"));
	 	   	 	String str=DispensaryConstant.DISPENSARY_DETAILS+"dispensary_id="+dis_id+"&latitude="+DispensaryConstant.latitude+"&longitude="+DispensaryConstant.longitude;
	 	   	 	try
	 	   	 	{
	 	   	 		URL url=new URL(str);
	 	   	 	 	URLConnection urlc=url.openConnection();
	 	   	 	 	BufferedReader bfr=new BufferedReader(new InputStreamReader(urlc.getInputStream()));
	 	   	 	 	String line;
	 	   	 	 	while((line=bfr.readLine())!=null){
	 	   	 	 		JSONArray jsa=new JSONArray(line);
	 	 	        	for(int i=0;i<jsa.length();i++){
	 	 	        		JSONObject jo=(JSONObject)jsa.get(0);
	 	   	 	 	    	if(jo.getString("dispensary_id").equalsIgnoreCase(dis_id)){
	 	   	 	 	    		try
	 	   	 	 	    		{
	 	   	 	 	    			temp = jo.getString("large_image_url");
	 	   	 	 	    		}catch(Exception e){
	 	   	 	 	        		e.getMessage();
	 	   	 	 	        	}
	 	   	 	 	    		mDispensaryId = jo.getString("dispensary_id");
	 	   	 	 	    		data.addElement(jo.getString("dispensary_name"));//2
	 	   	 	 	    		data.addElement(jo.getString("address"));//3
	 	   	 	 	    		data.addElement(jo.getString("phone"));//4
	 	   	 	 	    		data.addElement(jo.getString("website"));//5
	 	   	 	 	    		data.addElement(jo.getString("email"));//6
	 	   	 					data.addElement(jo.getString("latitude"));//7
	 	   	 					data.addElement(jo.getString("longitude"));//8
	 	   	 					data.addElement(jo.getString("description"));//9
	 	   	 					data.addElement(jo.getString("average_rating"));//10
	 	   	 					html_data = jo.getString("html_data");
	 	   	 					try{
	 	   	 						Log.d("Bugs",html_data);
//	 	   	 						html_data.replaceFirst(">Reviews (0)", "");
	 	   	 						String temp = html_data.substring(0, html_data.indexOf(">Reviews"));
	 	   	 						String temp1 = html_data.substring(html_data.indexOf(">Reviews")+14);
	 	   	 						html_data = temp+temp1;
	 	   	 					Log.d("Bugs",html_data);
	 	   	 					}catch (Exception e) {
									// TODO: handle exception
								}
	 	        			}				 
	 	 				}
	 	   			}
	 	   	 	 	
		 	   	 	String jsonUrl = DispensaryConstant.REVIEW_LIST+"dispensary_id="+data.elementAt(0).toString();
		 	        URL urlReviewList = new URL(jsonUrl);
		 	        URLConnection urlcReviewList = urlReviewList.openConnection();
		 	        BufferedReader bfrReviewList = new BufferedReader(new InputStreamReader(urlcReviewList.getInputStream()));
		 	        String lineReviewList;
		 	        int count = 0;
		 	        while((lineReviewList = bfrReviewList.readLine())!=null){
		 	        		JSONArray ja=new JSONArray(lineReviewList);
		 	        		count += ja.length();
		 	        }
		 	       
		 	       review_count = count;
		 	       runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						((TextView)findViewById(R.id.review_count)).setText(String.format("%d", review_count));
					}
				});
	 	   	 	}
	 	   	 	catch(MalformedURLException e){
	 	   	 	 	e.printStackTrace();
	 	   	 	}
	 	   	 	catch(IOException e){
	 		       	e.printStackTrace();
	 		    }
	 		    catch(JSONException e1){
	 		      	e1.printStackTrace();
	 		    }			
				}
				catch(Exception e){
	 			e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
				view_flag=0;
				}
				else{
					pd.dismiss();
					try{
						builder = new AlertDialog.Builder(Dispansary_Detail.this);
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


	    	 final String mimeType = "text/html";
	    	 final String encoding = "utf-8";
//	    	 String html_data1 = html_data.replace("a href="+"~LinkAction~phone~702-476-6325"+">702-476-6325</a>", "abc");
	    	 webview.loadDataWithBaseURL(html_data, html_data, mimeType, encoding, null);
	    
//	    	 webview.setWebViewClient(new WebViewClient() {
//	    		    public boolean shouldOverrideUrlLoading(WebView view, String url){
//	    		        // do your handling codes here, which url is the requested url
//	    		        // probably you need to open that url rather than redirect:
//	    		        view.loadUrl(html_data);
//	    		        return false; // then it is not handled by default action
//	    		   }
//	    		});

	    	 pd.dismiss();
	      }
	};
	
	public void onClick(View v) 
	{
		switch(v.getId())
		{
		
				case R.id.d_btn_location:
						finish();
						break;
				case R.id.d_btn_search:
					try{
					if(DispensaryConstant.global_flag==3){
						Intent intent1=new Intent(this,DispensaryListScreen.class);
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
					}
					catch(Exception e){
						e.getMessage();
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
				case R.id.d_btn_map:
					Bundle bundle=new Bundle();
					bundle.putString("lat",data.elementAt(7).toString());
					bundle.putString("long",data.elementAt(8).toString());
					bundle.putString("title",data.elementAt(2).toString());
					bundle.putString("address",data.elementAt(3).toString());
					bundle.putString("marker",marker);
					Intent intent=new Intent(this,MapScreen.class);
					intent.putExtras(bundle);
					startActivity(intent);
					 break;
			    case R.id.d_btn_review:
			    	if (review_count == 0) {
			    		AlertDialog dialog;
			    		builder = new AlertDialog.Builder(Dispansary_Detail.this);
			    		builder.setMessage("No Review Available");
						builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
						dialog = builder.create();
						dialog.show();
			    	} else {
				    	Bundle bundle1=new Bundle();
				    	bundle1.putString("clinic_name",data.elementAt(2).toString());
				    	bundle1.putString("clinic_id",data.elementAt(0).toString());
				    	Intent intent2=new  Intent(this,ReviewScreen.class);
				    	intent2.putExtras(bundle1);
				    	startActivity(intent2);
			    	}
					break;
					
			    case R.id.d_btn_add:
			    	Intent addContactIntent = new Intent(Contacts.Intents.Insert.ACTION, Contacts.People.CONTENT_URI);
			    	addContactIntent.putExtra(Contacts.Intents.Insert.NAME, data.elementAt(2).toString()); // an example, there is other data available
			    	addContactIntent.putExtra(Contacts.Intents.Insert.PHONE, data.elementAt(4));
			    	addContactIntent.putExtra(Contacts.Intents.Insert.EMAIL, data.elementAt(6));
			    	startActivity(addContactIntent);

			    	break;

			    case R.id.d_btn_direction:
			    	try
			    	{
				       MapRoute.dlat=data.get(7);
				       MapRoute.dlong=data.get(8);
				       Intent callIntent = new Intent(Dispansary_Detail.this,MapRouteActivity.class);
				       startActivity(callIntent);
			    	}
			    	catch(Exception e){
			    		e.getMessage();
			    	}
			    	break;
			    
		}
		
	}
	
	private Bitmap getBitmap(String url) 
	{
	
	        String filename = String.valueOf(url.hashCode());
	        File f = new File(cacheDir, filename);
	        
	        Bitmap b = decodeFile(f);
	        if(b!=null)
	            return b;
	        
	        try {
	            Bitmap bitmap=null;
	            InputStream is = new URL(url).openStream();
	            OutputStream os = new FileOutputStream(f);
	            Utils.CopyStream(is, os);
	            os.close();
	            bitmap = decodeFile(f);
	            return bitmap;
	        } catch (Exception ex){
	           ex.printStackTrace();
	           return null;
	        }
	 }
	 
	 private Bitmap decodeFile(File f)
	 {
	        try {
	            BitmapFactory.Options o = new BitmapFactory.Options();
	            o.inJustDecodeBounds = true;
	            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
	            
	            final int REQUIRED_SIZE=70;
	            int width_tmp=o.outWidth, height_tmp=o.outHeight;
	            int scale=1;
	            while(true){
	                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	                    break;
	                width_tmp/=2;
	                height_tmp/=2;
	                scale++;
	            }
	            
	            BitmapFactory.Options o2 = new BitmapFactory.Options();
	            o2.inSampleSize=scale;
	            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	        } catch (FileNotFoundException e) {}
	        return null;
	    }
		private boolean isOline(Context context){
			try{
				ConnectivityManager cm=(ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
