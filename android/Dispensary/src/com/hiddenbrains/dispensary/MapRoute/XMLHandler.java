package com.hiddenbrains.dispensary.MapRoute;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class XMLHandler extends DefaultHandler implements ContentHandler
{
	private String name="";
	private boolean location;
	private LatitudeLongitude l = new LatitudeLongitude();
	
	@Override
	public void startElement(String uri, String localName, String qName,Attributes atts) throws SAXException 
	{
        if(localName.equalsIgnoreCase("location"))		
            location = true;
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		 name = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException 
	{
		if(location)
		{
			if(localName.equals("lat"))
				   l.setname(1,name);
	        if(localName.equals("lng"))
	        	l.setname(2,name);
		}
		if(localName.equals("location"))
			location=false;
	    
		if(localName.equals("formatted_address"))
	      {
	    	  l.setname(0,name);
	      }
	}
}
