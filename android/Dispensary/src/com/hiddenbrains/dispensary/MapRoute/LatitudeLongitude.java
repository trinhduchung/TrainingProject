package com.hiddenbrains.dispensary.MapRoute;

import java.util.ArrayList;

public class LatitudeLongitude 
{
   public static ArrayList<String> south_west = new ArrayList<String>();
   
   public static ArrayList<String> getname()
   {
	   return south_west;
   }
   public void setname(int i,String lat)
   {
	   LatitudeLongitude.south_west.add(i,lat);
	   
   }
}
