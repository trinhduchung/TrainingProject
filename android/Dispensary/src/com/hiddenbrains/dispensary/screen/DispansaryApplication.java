/**
 * 
 */
package com.hiddenbrains.dispensary.screen;

import java.util.HashMap;

import android.app.Application;

/**
 * @author trinhduchung
 *
 */
public class DispansaryApplication extends Application {
	
	private static DispansaryApplication _instance = null;
	private final HashMap<Object,Object> _data = new HashMap<Object,Object>();
	public static int map_flag = 0;
	
	/**
	 * 
	 */
	public DispansaryApplication() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		_instance = this;
	}
	
	public static DispansaryApplication sharedInstance() {
		return _instance;
	}
	
	public void putData(Object key, Object value) {
		_data.put(key, value);
	}

	public void removeData(Object key) {
		_data.remove(key);
	}

	public Object getData(Object key) {
		return _data.get(key);
	}
	
}
