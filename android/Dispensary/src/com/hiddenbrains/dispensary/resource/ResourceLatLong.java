package com.hiddenbrains.dispensary.resource;

import com.hiddenbrains.dispensary.screen.DispensaryListScreen;
import com.hiddenbrains.dispensary.screen.From_Main_Search;

public class ResourceLatLong 
{
	private String title[];
	private String address[];
	private String lat_lng[][];
	
	public ResourceLatLong(int index) 
	{
		if(index==1)
		{
			int i = DispensaryListScreen.title.size();
	
			title = new String[i];
			address = new String[i];
			lat_lng = new String [i][2];
			
			for(int x=0;x<i;x++)
			{
				title [x] = DispensaryListScreen.title.get(x).toString();
				address [x] = DispensaryListScreen.address.get(x).toString();
				lat_lng[x][0] = DispensaryListScreen.lat.get(x).toString();
				lat_lng[x][1] = DispensaryListScreen.longt.get(x).toString();
			}
		}
		if(index==2)
		{
			int i = From_Main_Search.title.size();
	
			title = new String[i];
			address = new String[i];
			lat_lng = new String [i][2];
			
			for(int x=0;x<i;x++)
			{
				title [x] = From_Main_Search.title.get(x).toString();
				address [x] = From_Main_Search.address.get(x).toString();
				lat_lng[x][0] = From_Main_Search.lat.get(x).toString();
				lat_lng[x][1] = From_Main_Search.longt.get(x).toString();
			}
		}
	}
	public String[] getTitle(){
		return title;
	}
	
	public String[] getAddress(){
		return address;
	}
	
	public String[][] getLat_Lng(){
		return lat_lng;
	}

}
