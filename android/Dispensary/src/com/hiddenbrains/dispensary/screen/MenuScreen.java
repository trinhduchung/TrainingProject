package com.hiddenbrains.dispensary.screen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MenuScreen extends Activity {

	private ListView mListMenu;
	private List<MenuItem> mItems = new ArrayList<MenuScreen.MenuItem>();
	private ProgressDialog mDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_screen);
		mListMenu = (ListView) findViewById(R.id.list_menu);
		String id = getIntent().getStringExtra("id");
		new FeetMenuTask().execute("http://www.thcfinder.com/menu.php?dispid=" + id);
	}
	
	public class FeetMenuTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... urls) {
			try {
				runOnUiThread(new Runnable() {
					public void run() {
						show();
					}
				});
				
				String url = urls[0];
				System.out.println(url);
				final DefaultHttpClient client = new DefaultHttpClient();
				HttpParams params = client.getParams();
				HttpConnectionParams.setConnectionTimeout(params, 20000);
				HttpConnectionParams.setSoTimeout(params, 25000);
				// set parameter
				HttpProtocolParams.setUseExpectContinue(client.getParams(), true);

				HttpGet get = new HttpGet(url);
				HttpResponse response = client.execute(get);
				InputStream in = response.getEntity().getContent();
				BufferedReader r = new BufferedReader(new InputStreamReader(in));
				StringBuilder total = new StringBuilder();
				String line;
				while ((line = r.readLine()) != null) {
				    total.append(line);
				}

				String str = total.toString();
				str = str.replaceAll("&", " and ");
				File sdCard = Environment.getExternalStorageDirectory();
				File dir = new File (sdCard.getAbsolutePath() + "/dir1/dir2");
				dir.mkdirs();
				File file = new File(dir, "menu.xml");
				FileWriter writer = new FileWriter(file);
				writer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
				writer.append("<root>");
		        writer.append(str.toString());
		        writer.append("</root>");
		        writer.flush();
		        writer.close();
				
		        InputStream inputStream = new FileInputStream(file);
		        
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = builderFactory.newDocumentBuilder();
				Document document = builder.parse(inputStream);
				Element element = document.getDocumentElement();
				NodeList list = element.getElementsByTagName("menuitem");
				for (int i = 0;i < list.getLength();i++) {
					MenuItem item = new MenuItem();
					Element menuElement = (Element) list.item(i);
					
					Element nameElement = (Element) menuElement.getElementsByTagName("name").item(0);
					item.name = nameElement.getFirstChild().getNodeValue();
					
					Element typeElement = (Element) menuElement.getElementsByTagName("type").item(0);
					item.type = typeElement.getFirstChild().getNodeValue();
					
					Element priceGElement = (Element) menuElement.getElementsByTagName("price_g").item(0);
					item.priceG = priceGElement.getFirstChild().getNodeValue();
					
					Element price8Element = (Element) menuElement.getElementsByTagName("price_8").item(0);
					item.price8 = price8Element.getFirstChild().getNodeValue();
					
					Element price4Element = (Element) menuElement.getElementsByTagName("price_4").item(0);
					item.price4 = price4Element.getFirstChild().getNodeValue();
					
					Element price2Element = (Element) menuElement.getElementsByTagName("price_2").item(0);
					item.price2 = price2Element.getFirstChild().getNodeValue();
					
					Element priceOZElement = (Element) menuElement.getElementsByTagName("price_oz").item(0);
					item.priceOZ = priceOZElement.getFirstChild().getNodeValue();
					
					mItems.add(item);
				}
				
			} catch (SAXException ex) {
				ex.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			ListMenuAdapter adapter = new ListMenuAdapter(MenuScreen.this, mItems);
			mListMenu.setAdapter(adapter);
			hide();
		}
		
	}
	
	private void show() {
		mDialog = ProgressDialog.show(this, "", "Loading ...", true, true);
	}
	
	private void hide() {
		mDialog.dismiss();
	}
	
	public class MenuItem {
		public String menuId;
		public String dispId;
		public String type;
		public String name;
		public String strainId;
		public String desc;
		public String priceG;
		public String price8;
		public String price4;
		public String price2;
		public String priceOZ;
		public int active;
		public int position;
		public String thcPercent;
		public String cbdPercent;
		public String creationTime;
		public String lastUpdated;
		public String inStock;
		public String cbnPercent;
	}

	public class ListMenuAdapter extends BaseAdapter {

		public static final int TYPE_HEADER = 0x00;
		public static final int TYPE_NORMAL = TYPE_HEADER + 0x01;
		private List<MenuItem> mMenuItems = new ArrayList<MenuScreen.MenuItem>();
		private Context mContext; 
		
		public ListMenuAdapter(Context context, List<MenuItem> items) {
			mContext = context;
			mMenuItems = items;
		}
		
		@Override
		public int getCount() {
			if (mMenuItems == null || mMenuItems.size() <= 0) {
				return 0;
			}
			return mMenuItems.size();
		}

		@Override
		public MenuItem getItem(int position) {
			return mMenuItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			RowViewHolder viewHolder;
			View rowView = convertView;
			MenuItem item = getItem(position);
			//if (rowView == null) {
			rowView = getViewForType(getItemViewType(position), item.type);
			viewHolder = new RowViewHolder(rowView, item.type);
			rowView.setTag(viewHolder);
			//}
			
			viewHolder = (RowViewHolder) rowView.getTag();
			
			String dolar = "$";
			viewHolder.name.setText(item.name);
			viewHolder.buttonG.setText(dolar + item.priceG);
			if (item.type.equals("Edibles") || item.type.equals("Merchandise") || item.type.equals("Clones") || item.type.equals("Seeds")) {
				
			} else {
				viewHolder.button8.setText(dolar + item.price8);
				if (!item.type.equals("Concentrates")) {
					viewHolder.button4.setText(dolar + item.price4);
					viewHolder.button2.setText(dolar + item.price2);
					viewHolder.button82.setText(dolar + item.priceOZ);
				}
			}
			if (item.type.equals("Hybrid")) {
				viewHolder.header.setBackgroundResource(R.drawable.header_hybrid);
			} else if (item.type.equals("Indica")) {
				viewHolder.header.setBackgroundResource(R.drawable.header_indicia);
			} else if (item.type.equals("Sativa")) {
				viewHolder.header.setBackgroundResource(R.drawable.header_satvia);
			} else if (item.type.equals("Merchandise")) {
				viewHolder.header.setBackgroundResource(R.drawable.header_merchandise);
			} else if (item.type.equals("Edibles")) {
				viewHolder.header.setBackgroundResource(R.drawable.header_edibles);
			} else if (item.type.equals("Concentrates")) {
				viewHolder.header.setBackgroundResource(R.drawable.header_concentrates);
			} else if (item.type.equals("Clones")) {
				viewHolder.header.setBackgroundResource(R.drawable.header_clones);
			}
			
			return rowView;
		}

		@Override
		public int getItemViewType(int position) {
			if (isStartGroup(position)) {
				return TYPE_HEADER;
			}
			return TYPE_NORMAL;
		}
		
		private View getViewForType(int type, String itemType) {
			View v = LayoutInflater.from(mContext).inflate(R.layout.list_menu_row_view, null);;//edibles, merchandise, clones and seeds
			if (itemType.equals("Edibles") || itemType.equals("Merchandise") || itemType.equals("Clones") || itemType.equals("Seeds")) {
				v = LayoutInflater.from(mContext).inflate(R.layout.list_menu_row_view_one_item, null);
			} else if (itemType.equals("Concentrates")) {
				v = LayoutInflater.from(mContext).inflate(R.layout.list_menu_row_concentrates, null);
			}
			if (type == TYPE_HEADER) {
				return v;
			} else {
				v.findViewById(R.id.menu_header).setVisibility(View.GONE);
				return v;
			}
		}
		
		private boolean isStartGroup(int position) {
			if (position <= 0) {
				return true;
			}
			
			MenuItem prev = mMenuItems.get(position - 1);
			MenuItem curr = mMenuItems.get(position);
			
			if (!prev.type.equalsIgnoreCase(curr.type)) {
				return true;
			}
			
			return false;
		}
	}
	
	public class RowViewHolder {
		public ImageView header;
		public TextView name;
		public Button buttonG;
		public Button button8;
		public Button button4;
		public Button button2;
		public Button button82;
		public String itemType;
		
		public RowViewHolder(View v, String item_type) {
			itemType = item_type;
			header = (ImageView) v.findViewById(R.id.menu_header);
			name = (TextView) v.findViewById(R.id.menu_name);
			
			View v1 = v.findViewById(R.id.button1);
			buttonG = (Button)v1.findViewById(R.id.menu_button_price);
			
			if (itemType.equals("Edibles") || itemType.equals("Merchandise") || itemType.equals("Clones") || itemType.equals("Seeds")) {
				((ImageView)v1.findViewById(R.id.menu_type_icon)).setBackgroundResource(R.drawable.ea);
			} else {
				((ImageView)v1.findViewById(R.id.menu_type_icon)).setBackgroundResource(R.drawable.icon_g);
				View v2 = v.findViewById(R.id.button2);
				button8 = (Button)v2.findViewById(R.id.menu_button_price);
				((ImageView)v2.findViewById(R.id.menu_type_icon)).setBackgroundResource(R.drawable.icon_18);
				if (itemType.equals("Concentrates")) {
					((ImageView)v1.findViewById(R.id.menu_type_icon)).setBackgroundResource(R.drawable.g_12);
					((ImageView)v2.findViewById(R.id.menu_type_icon)).setBackgroundResource(R.drawable.icon_g);
				} else {
					View v3 = v.findViewById(R.id.button3);
					button4 = (Button)v3.findViewById(R.id.menu_button_price);
					((ImageView)v3.findViewById(R.id.menu_type_icon)).setBackgroundResource(R.drawable.icon_14);
					
					View v4 = v.findViewById(R.id.button4);
					button2 = (Button)v4.findViewById(R.id.menu_button_price);
					((ImageView)v4.findViewById(R.id.menu_type_icon)).setBackgroundResource(R.drawable.icon_12);
					
					View v5 = v.findViewById(R.id.button5);
					button82 = (Button)v5.findViewById(R.id.menu_button_price);
					((ImageView)v5.findViewById(R.id.menu_type_icon)).setBackgroundResource(R.drawable.oz);
				}
			}
		}
	}
}
