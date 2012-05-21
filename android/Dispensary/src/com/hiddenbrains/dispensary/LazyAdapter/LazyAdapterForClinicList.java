package com.hiddenbrains.dispensary.LazyAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hiddenbrains.dispensary.screen.Doctors_Clinic_List;
import com.hiddenbrains.dispensary.screen.R;

public class LazyAdapterForClinicList extends BaseAdapter implements OnClickListener{
			Doctors_Clinic_List dcl=new Doctors_Clinic_List();
			private Activity activity;
			private ArrayList<String> title;
			private ArrayList<String> address;
			private ArrayList<String> distance;
			private ArrayList<String> phone;
			private static LayoutInflater inflater=null;
			public int size=0;
			public String TAG="Tofeeq";
		public LazyAdapterForClinicList(Activity a,ArrayList<String>title1,ArrayList<String> add1,ArrayList<String> distance1,ArrayList<String> phone1) 
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

	
	
		public View getView(int position, View convertView, ViewGroup parent) {
			View v=convertView;
			ViewHolder holder;
			
			//if(convertView==null){
				try{
				v =inflater.inflate(R.layout.inflatefile_for_clinic,null);
				}
				catch(Exception e){
					e.getMessage();
				}
				holder=new ViewHolder();
				try{
				holder.title=(TextView)v.findViewById(R.id.c_title);
				holder.address=(TextView)v.findViewById(R.id.c_address);
				holder.distance=(TextView)v.findViewById(R.id.c_distance);
				holder.phone=(TextView)v.findViewById(R.id.c_phone);
				holder.map=(ImageButton)v.findViewById(R.id.c_btn_map);
				holder.map.setOnClickListener(this);
				holder.call=(ImageButton)v.findViewById(R.id.c_btn_call);
				holder.call.setOnClickListener(this);
				}
				catch(Exception e){
					e.getMessage();
				}
				
					
					v.setTag(holder);
				
			//}
			//else
			//{   
				 holder=(ViewHolder)v.getTag();
			//}
			
		
			holder.title.setText((title.get(position).toString()));
			holder.address.setText(address.get(position).toString());
			holder.distance.setText(distance.get(position).toString());
			holder.phone.setText(phone.get(position).toString());
			return v;
        }



		public void onClick(View v) {
			switch(v.getId()){
					case R.id.c_btn_map:	Log.v(TAG, "Map button clicked");
										
										break;
					case R.id.c_btn_call:	Log.v(TAG, "Call button clicked");
										break;
			}
//			Log.v(TAG,"position id"+v.getLeft());
//			Log.v(TAG,"position id"+v.getTop());
			
			
			
		}
}
