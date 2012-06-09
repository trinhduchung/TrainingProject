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

import com.hiddenbrains.dispensary.LazyAdapter.LazyAdapterMainList;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class From_Main_Search extends Activity implements OnClickListener,
		Runnable {
	private ProgressDialog pd;
	public static ArrayList<String> title = new ArrayList<String>();
	private ArrayList<String> distance = new ArrayList<String>();
	private ArrayList<String> imageUrl = new ArrayList<String>();
	private ArrayList<String> image = new ArrayList<String>();
	private ArrayList<String> dispensary_id = new ArrayList<String>();
	public static ArrayList<String> lat = new ArrayList<String>();
	public static ArrayList<String> longt = new ArrayList<String>();
	public static ArrayList<String> address = new ArrayList<String>();
	private ArrayList<String> icon_image = new ArrayList<String>();
	ImageButton btn_dispansary_list, btn_search, btn_doctors, btn_map;
	private LazyAdapterMainList lzm;
	private ListView list;
	private ImageButton btn_back;
	int view_flag = 0;
	Builder builder = null;
	private String msge = "";
	boolean flag = false;
	boolean flagException = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.serch_result_from_main);
		try {
			view_flag = 1;
			btn_dispansary_list = (ImageButton) findViewById(R.id.d_btn_dispansary);
			btn_search = (ImageButton) findViewById(R.id.d_btn_search);
			btn_doctors = (ImageButton) findViewById(R.id.d_btn_doctors);
			btn_back = (ImageButton) findViewById(R.id.btn_back);
			btn_map = (ImageButton) findViewById(R.id.d_btn_map);
			btn_map.setOnClickListener(this);
			list = (ListView) findViewById(R.id.d_list_view);
			btn_dispansary_list.setOnClickListener(this);
			btn_search.setOnClickListener(this);
			btn_doctors.setOnClickListener(this);
			btn_back.setOnClickListener(this);
			
			title = new ArrayList<String>();
			distance = new ArrayList<String>();
			imageUrl = new ArrayList<String>();
			image = new ArrayList<String>();
			dispensary_id = new ArrayList<String>();
			lat = new ArrayList<String>();
			longt = new ArrayList<String>();
			address = new ArrayList<String>();
			icon_image = new ArrayList<String>();
			
			pd = ProgressDialog.show(this, "Please wait", "Loading...", true,
					false);
			Thread thread = new Thread(this);
			thread.start();
		} catch (IllegalArgumentException ex) {
			ex.getMessage();
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.d_btn_dispansary:
			if (DispensaryConstant.global_flag == 3) {
				Intent intent1 = new Intent(this, Doctors_Clinic_List.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent1);
				DispensaryConstant.global_flag = 0;
			} else if (DispensaryConstant.global_flag == 0) {
				Intent intent1 = new Intent(this, DispensaryListScreen.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent1);
				DispensaryConstant.global_flag = 0;
			} else if (DispensaryConstant.global_flag == 2) {
				Intent intent1 = new Intent(this, SearchScreen.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent1);
				DispensaryConstant.global_flag = 0;
			}

			break;
		case R.id.d_btn_search:
			DispensaryConstant.global_flag = 2;
			// Intent intent1=new Intent(this,SearchScreen.class);
			// startActivity(intent1);
			finish();
			break;
		case R.id.d_btn_doctors:
			if (DispensaryConstant.global_flag == 3) {
				Intent intent1 = new Intent(this, Doctors_Clinic_List.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent1);
				DispensaryConstant.global_flag = 3;
			} else if (DispensaryConstant.global_flag == 0) {
				Intent intent1 = new Intent(this, DispensaryListScreen.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent1);
				DispensaryConstant.global_flag = 2;
			} else if (DispensaryConstant.global_flag == 2) {
				Intent intent1 = new Intent(this, SearchScreen.class);
				intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent1);
				DispensaryConstant.global_flag = 3;
			}
			break;
		case R.id.btn_back:
			finish();
			break;
		case R.id.d_btn_map:
			Bundle bundle = new Bundle();
			bundle.putInt("index", 2);
			// bundle.putString("lat",lat.get(0).toString());
			// bundle.putString("long",longt.get(0).toString());
			// bundle.putString("title",title.get(0).toString());
			// bundle.putString("address",address.get(0).toString());

			Intent intent = new Intent(this, MapScreenAll.class);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
	}

	public void run() {
		String str = DispensaryConstant.DISPENSARY_LIST + "latitude="
				+ DispensaryConstant.latitude + "&longitude="
				+ DispensaryConstant.longitude;
		WifiManager wifi = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);

		if (wifi.isWifiEnabled() || isOline(this)) {

			try {
				Bundle bundle = getIntent().getExtras();
				String state_name = bundle.getString("state_name");
				String state_name1 = state_name.replace(" ", "%20");
				if (!state_name.equals("")) {
					str = str + "&keyword=" + state_name1;
				}

				URL url = new URL(str);
				URLConnection urlc = url.openConnection();
				BufferedReader bfr = new BufferedReader(new InputStreamReader(
						urlc.getInputStream()));
				String line;

				while ((line = bfr.readLine()) != null) {
					JSONArray jsa = new JSONArray(line);
					for (int i = 0; i < jsa.length(); i++) {
						JSONObject jo = (JSONObject) jsa.get(i);
						if (i == 0) {
							if (jo.getString("success").equals("0")) {
								flag = true;
								msge = jo.getString("message");
								break;
							}
							dispensary_id.add(jo.getString("dispensary_id"));
							title.add(jo.getString("dispensary_name"));
							image.add(jo.getString("review"));
							distance.add(jo.getString("distance"));
							lat.add(jo.getString("latitude"));
							longt.add(jo.getString("longitude"));
							address.add(jo.getString("address"));
							icon_image.add(jo.getString("icon_image"));
							if (jo.getString("image").equalsIgnoreCase(
									"noimage.png")) {
								imageUrl.add(DispensaryConstant.noImageConstant);
							} else {
								imageUrl.add(jo.getString("image"));
							}
						} else {
							dispensary_id.add(jo.getString("dispensary_id"));
							title.add(jo.getString("dispensary_name"));
							image.add(jo.getString("review"));
							distance.add(jo.getString("distance"));
							lat.add(jo.getString("latitude"));
							longt.add(jo.getString("longitude"));
							address.add(jo.getString("address"));
							icon_image.add(jo.getString("icon_image"));
							if (jo.getString("image").equalsIgnoreCase(
									"noimage.png")) {
								imageUrl.add(DispensaryConstant.noImageConstant);
							} else {
								imageUrl.add(jo.getString("image"));
							}
						}

					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
				flagException = true;

			} catch (IllegalArgumentException e) {
				e.getMessage();
				flagException = true;
			} catch (IOException e) {
				flagException = true;
				e.printStackTrace();
			} catch (JSONException e1) {
				flagException = true;
				e1.printStackTrace();
			}
			try {

				if (flagException) {
					handler1.sendEmptyMessage(0);
				} else if (flag) {
					handler1.sendEmptyMessage(0);
				}
			} catch (Exception e) {
				e.getMessage();
			}
			handler.sendEmptyMessage(0);
		} else {
			pd.dismiss();
			try {
				builder = new AlertDialog.Builder(From_Main_Search.this);
				builder.setTitle("Connection Support");
				builder.setMessage("Connection not available");
				builder.setPositiveButton("ok",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						});
				handler2.sendEmptyMessage(0);
			} catch (Exception e) {
				e.getMessage();
			}
		}
	}

	private Handler handler2 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AlertDialog alert = builder.create();
			alert.show();
		}
	};

	private Handler handler1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			builder = new AlertDialog.Builder(From_Main_Search.this);
			builder.setTitle("No Records");
			builder.setMessage(msge);
			builder.setPositiveButton("ok",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	};
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			lzm = new LazyAdapterMainList(From_Main_Search.this, title,
					distance, image, imageUrl, address, icon_image);
			try {
				list.setAdapter(lzm);
			} catch (IllegalArgumentException e) {
				e.getMessage();
			} catch (Exception e) {
				e.getMessage();
			}
			list.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					go_To_Details(position);
				}
			});
			pd.dismiss();
		}

	};

	public void go_To_Details(int pos) {
		try {
			String dis_data = dispensary_id.get(pos);
			String dis = distance.get(pos);
			String marker = icon_image.get(pos);
			Bundle bundle = new Bundle();
			bundle.putString("Dispensary_id", dis_data);
			bundle.putString("distance", dis);
			bundle.putString("marker", marker);
			Intent intent = new Intent(this, Dispansary_Detail.class);
			intent.putExtras(bundle);
			startActivity(intent);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	@SuppressWarnings("static-access")
	private boolean isOline(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(context.CONNECTIVITY_SERVICE);
			if (cm == null)
				return false;
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info == null)
				return false;
			return info.isConnectedOrConnecting();
		} catch (Exception e) {
			e.getMessage();
			return false;
		}
	}

}