package com.hiddenbrains.dispensary.LazyAdapter;

import java.util.ArrayList;

import com.hiddenbrains.dispensary.imageloader.ImageLoader;
import com.hiddenbrains.dispensary.screen.R;



import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapterMainList extends BaseAdapter{
	   	private Activity activity;
		private ArrayList<String> title=new ArrayList<String>();
		private ArrayList<String> distance=new ArrayList<String>();
		private ArrayList<String> image_rating=new ArrayList<String>();
		private ArrayList<String> online_image=new ArrayList<String>();
		private ArrayList<String> address=new ArrayList<String>();
		private ArrayList<String> icon_image=new ArrayList<String>();
		private static LayoutInflater inflater=null;
	    public int size=0;
	    ImageLoader imageLoader;
	  
		public LazyAdapterMainList(Activity a,ArrayList<String> name,ArrayList<String> dis1,ArrayList<String> image,ArrayList<String>  online_image1,ArrayList<String>  address1,ArrayList<String>  icon_image1) 
	    {
	        activity = a;
	        title=new ArrayList<String>();
			distance=new ArrayList<String>();
			image_rating=new ArrayList<String>();
			online_image=new ArrayList<String>();
			address=new ArrayList<String>();
			icon_image=new ArrayList<String>();
	        title = name;
	        distance=dis1;
	        image_rating=image;
	        address=address1;
	        icon_image=icon_image1;
	        this.online_image = online_image1;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        imageLoader=new ImageLoader(activity.getApplicationContext());
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
		public TextView title_txt;
		public TextView distance_txt;
		public TextView reviews_txt;
		public TextView address_txt;
		public ImageView img1;
		public ImageView img2;
		public ImageView iconimage;
		}

	
	
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View v=convertView;
			ViewHolder holder;

			try
			{
				if(convertView==null)
				{
					try
					{
						v =inflater.inflate(R.layout.dispansary_list_view_main,null);
					}
					catch(Exception e)
					{
						e.getMessage();
					}
					holder=new ViewHolder();
					try
					{
						holder.title_txt = (TextView)v.findViewById(R.id.dl_title);
						holder.reviews_txt = (TextView)v.findViewById(R.id.dl_reviews_data);
						holder.distance_txt = (TextView)v.findViewById(R.id.dl_distance_data);
						holder.address_txt = (TextView)v.findViewById(R.id.dl_address_data);
					}
					catch(Exception e)
					{
						e.getMessage();
					}
					try
					{
						holder.img1=(ImageView) v.findViewById(R.id.dl_image_main);
						holder.img2=(ImageView) v.findViewById(R.id.dl_image_rating);
						holder.iconimage=(ImageView) v.findViewById(R.id.dl_image_icon);
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
				if(image_rating.get(position).toString().equals("(0)"))
				{
					   holder.img2.setImageResource(R.drawable.rating_0);	
					}else if(image_rating.get(position).toString().equals("(1)")){
					   holder.img2.setImageResource(R.drawable.rating_1);	
					}else if(image_rating.get(position).toString().equals("(2)")){
					   holder.img2.setImageResource(R.drawable.rating_2);	
					}else if(image_rating.get(position).toString().equals("(3)")){
					   holder.img2.setImageResource(R.drawable.rating_3);	
					}else if(image_rating.get(position).toString().equals("(4)")){
					   holder.img2.setImageResource(R.drawable.rating_4);	
					}else if(image_rating.get(position).toString().equals("(5)")){
					   holder.img2.setImageResource(R.drawable.rating_5);	
					}	
				else{
						holder.img2.setImageResource(R.drawable.rating_0);	
					}
				if(title.get(position).toString().equals(""))
				{
					holder.title_txt.setText("");
				}
				else
				{
					holder.title_txt.setText((title.get(position).toString()));
					
				}
				if(image_rating.get(position).toString().equals(""))
				{
					holder.reviews_txt.setText("");
				}
				else
				{
					holder.reviews_txt.setText((image_rating.get(position).toString()));
					
				}
				if(distance.get(position).toString().equals(""))
				{
					holder.distance_txt.setText("");
				}
				else
				{
					holder.distance_txt.setText(distance.get(position).toString());
				}
				if(address.get(position).toString().equals(""))
				{
					holder.address_txt.setText("");
				}
				else
				{
					holder.address_txt.setText(address.get(position).toString());
				}
				
				
				if(icon_image.get(position).toString().equals("green-leaf.png"))
				{
					   holder.iconimage.setImageResource(R.drawable.green_leaf);	
				}
				else if(icon_image.get(position).toString().equals("blue-leaf.png"))
				{
					   holder.iconimage.setImageResource(R.drawable.blue_leaf);	
				}
				else if(icon_image.get(position).toString().equals("orange_leaf.png") || icon_image.get(position).toString().equals("orange-leaf.png"))
				{
					   holder.iconimage.setImageResource(R.drawable.orange_leaf);	
				}
				else if(icon_image.get(position).toString().equals("delivery.png"))
				{
					   holder.iconimage.setImageResource(R.drawable.delivery);	
				}
				else if(icon_image.get(position).toString().equals("delivery_blue.png"))
				{
					   holder.iconimage.setImageResource(R.drawable.delivery_blue);	
				}
				else if(icon_image.get(position).toString().equals("delivery_orange.png"))
				{
					   holder.iconimage.setImageResource(R.drawable.delivery_orange);	
				} else if (icon_image.get(position).toString().equals("app_diamond.png")) {
					holder.iconimage.setImageResource(R.drawable.app_diamond);
				} 
				else
				{
						Log.e("imge_missing", icon_image.get(position).toString());
						holder.iconimage.setImageResource(R.drawable.green_leaf);	
				}
				
				
				 holder.img1.setTag(online_image.get(position));
			     imageLoader.DisplayImage(online_image.get(position), activity, holder.img1);
			}catch(Exception e)
				{
					e.getMessage();
				}
		 return v;
}
}
