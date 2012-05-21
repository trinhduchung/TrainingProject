package com.hiddenbrains.dispensary.LazyAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiddenbrains.dispensary.screen.R;

public class LazyAdapterForReview  extends BaseAdapter{
			private Activity activity;
			private ArrayList<String> title=new ArrayList<String>();
			private ArrayList<String> posteddate=new ArrayList<String>();
			private ArrayList<String> reviewby=new ArrayList<String>();
			private ArrayList<String> image_rating=new ArrayList<String>();
			private static LayoutInflater inflater=null;
		    public int size=0;
		  
			public LazyAdapterForReview(Activity a,ArrayList<String> title1,ArrayList<String> reviewBy1,ArrayList<String> postDate1,ArrayList<String> rating1) 
		    {
		        activity = a;
		        this.title=title1;
		        this.reviewby = reviewBy1;
		        this.posteddate=postDate1;
		        this.image_rating = rating1;
		        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		        size=reviewby.size();
		    }																																																			
			
			
			
			public int getCount() {
				return reviewby.size();
			}



			public Object getItem(int position) {
					return position;
			}



			public long getItemId(int position) {
				return position;
			}



			public class  ViewHolder  {
					public TextView title;
					public TextView reviewBy;
					public TextView posted;
					public ImageView img2;
			}

		
			public View getView(int position, View convertView, ViewGroup parent) {
				View v=convertView;
				ViewHolder holder;
				
				//if(convertView==null){
					v =inflater.inflate(R.layout.inflatefilereview,null);
					holder=new ViewHolder();
					holder.title=(TextView) v.findViewById(R.id.dl_textview1);
					holder.reviewBy=(TextView)v.findViewById(R.id.dl_reviewby_data);
					holder.posted=(TextView)v.findViewById(R.id.dl_posted_data);
					holder.img2=(ImageView)v.findViewById(R.id.dl_posted_data11);
					v.setTag(holder);
				// }
				//
				//else{   
					 holder=(ViewHolder)v.getTag();
				//}
				
				holder.title.setText(title.get(position).toString());
				holder.reviewBy.setText((reviewby.get(position).toString()));
				holder.posted.setText(posteddate.get(position).toString());
//				if(image_rating.get(position).toString().equals("0"))
//				{
//				   holder.img2.setImageResource(R.drawable.rating_0);	
				if(image_rating.get(position).toString().equals("1")){
				   holder.img2.setImageResource(R.drawable.rating_1);	
				}else if(image_rating.get(position).toString().equals("2")){
				   holder.img2.setImageResource(R.drawable.rating_2);	
				}else if(image_rating.get(position).toString().equals("3")){
				   holder.img2.setImageResource(R.drawable.rating_3);	
				}else if(image_rating.get(position).toString().equals("4")){
				   holder.img2.setImageResource(R.drawable.rating_4);	
				}else if(image_rating.get(position).toString().equals("5")){
				   holder.img2.setImageResource(R.drawable.rating_5);	
					}	
				else{
					holder.img2.setImageResource(R.drawable.rating_0);	
				}
				
				return v;	}
	}


