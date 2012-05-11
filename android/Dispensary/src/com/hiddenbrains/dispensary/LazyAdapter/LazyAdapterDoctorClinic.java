package com.hiddenbrains.dispensary.LazyAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hiddenbrains.dispensary.screen.Doctors_Clinic_List;
import com.hiddenbrains.dispensary.screen.MapScreen;
import com.hiddenbrains.dispensary.screen.R;

public class LazyAdapterDoctorClinic extends BaseAdapter{
			Doctors_Clinic_List dcl=new Doctors_Clinic_List();
			private Activity activity;
			private ArrayList<String> title;
			private ArrayList<String> address;
			private ArrayList<String> distance;
			private ArrayList<String> phone;
			private static LayoutInflater inflater=null;
			public int size=0;
			public String TAG="Tofeeq";
		public LazyAdapterDoctorClinic(Activity a,ArrayList<String>title1,ArrayList<String> add1,ArrayList<String> distance1,ArrayList<String> phone1) 
			{
	        activity = a;
	        title=new ArrayList<String>();
	        address=new ArrayList<String>();
			distance=new ArrayList<String>();
			phone=new ArrayList<String>();
	        this.title = title1;
	        this.distance=distance1;
	        this.address=add1;
	        this.phone=phone1;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        size=title.size();
	    }																																																			
		
		
		
		public int getCount() {
			return title.size();
		}



		public Object getItem(int position) {
				return position;
		}



		public long getItemId(int position) {
			return position;
		}



		public class  ViewHolder  {
		public TextView title;
		public TextView address;
		public TextView distance;
		public TextView phone;
		public ImageButton map,call;
		}

	
	
		public View getView(final int position, View convertView, ViewGroup parent) {
			View v=convertView;
			ViewHolder holder;
			
			if(convertView==null){
				try{
				v =inflater.inflate(R.layout.doctor_clinic_list_row,null);
				}
				catch(Exception e){
					e.getMessage();
				}
				holder=new ViewHolder();
				try{
				holder.title=(TextView)v.findViewById(R.id.title);
				holder.address=(TextView)v.findViewById(R.id.address_value);
				holder.distance=(TextView)v.findViewById(R.id.distance_value);
				holder.phone=(TextView)v.findViewById(R.id.phone_value);
				holder.map=(ImageButton)v.findViewById(R.id.btn_map);
				}
				catch(Exception e){
					e.getMessage();
				}
				
					
					v.setTag(holder);
				
			}
			else
			{   
				 holder=(ViewHolder)v.getTag();
			}
			holder.title.setText((title.get(position).toString()));
			holder.address.setText(address.get(position).toString());
			holder.distance.setText(distance.get(position).toString());
			holder.phone.setText(phone.get(position).toString());
			holder.map.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Bundle bundle=new Bundle();
					bundle.putString("lat",Doctors_Clinic_List.lat.get(position));
					bundle.putString("long",Doctors_Clinic_List.longt.get(position));
					bundle.putString("title",title.get(position));
					bundle.putString("address",address.get(position));
					bundle.putString("marker","green-leaf.png");
					Intent intent=new Intent(activity,MapScreen.class);
					intent.putExtras(bundle);
					activity.startActivity(intent);
				}
			});
			holder.call=(ImageButton)v.findViewById(R.id.btn_call);
			holder.call.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					String str="tel: "+phone.get(position);
				    callIntent.setData(Uri.parse(str));
				    activity.startActivity(callIntent);

				}
			});
			return v;
        }
}
