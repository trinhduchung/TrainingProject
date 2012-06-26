package com.hiddenbrains.dispensary.maphelper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.maps.OverlayItem;
import com.hiddenbrains.dispensary.common.DispensaryConstant;
import com.hiddenbrains.dispensary.screen.DispansaryApplication;
import com.hiddenbrains.dispensary.screen.Dispansary_Detail;
import com.hiddenbrains.dispensary.screen.DispensaryListScreen;
import com.hiddenbrains.dispensary.screen.MapScreenAll;
import com.hiddenbrains.dispensary.screen.R;

public class BalloonOverlayView extends FrameLayout {
	private LinearLayout layout;
	private TextView title;
	private TextView snippet;
	private ListView listView;
	private int currentIndex = 0;
	private OverlayItem[] overlayItems;
	private Context mContext;

	public BalloonOverlayView(Context context, int balloonBottomOffset) {
		super(context);

		mContext = context;
		setPadding(10, 0, 10, balloonBottomOffset);
		layout = new LinearLayout(context);
		layout.setVisibility(VISIBLE);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View v;
		if (DispansaryApplication.map_flag == 1) {
			v = inflater.inflate(R.layout.balloon_map_one_item_overlay, layout);
			title = (TextView) v.findViewById(R.id.balloon_item_title);
			snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
		} else {
			v = inflater.inflate(R.layout.balloon_map_overlay, layout);
			// title = (TextView) v.findViewById(R.id.balloon_item_title);
			// snippet = (TextView) v.findViewById(R.id.balloon_item_snippet);
			DispansaryApplication app = DispansaryApplication.sharedInstance();
			if (app.getData(MapScreenAll.LIST_OVERLAY_ITEM) != null) {
				overlayItems = (OverlayItem[]) app
						.getData(MapScreenAll.LIST_OVERLAY_ITEM);
			} else {
				overlayItems = new OverlayItem[0];
			}

			listView = (ListView) v.findViewById(R.id.list_ballon_item);
			ListBallonAdapter adapter = new ListBallonAdapter(context,
					overlayItems);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(itemClickListener);
		}
		ImageView close = (ImageView) v.findViewById(R.id.close_img_button);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				layout.setVisibility(GONE);
			}
		});

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;

		addView(layout, params);

	}

	public OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			DispansaryApplication app = DispansaryApplication.sharedInstance();
			go_To_Details(position, app);
		}
	};

	public void go_To_Details(int pos, Application app) {
		try {
			String dis_data = DispensaryListScreen.dispensary_id.get(pos);
			String marker = DispensaryListScreen.icon_image.get(pos);
			String dis = DispensaryListScreen.distance.get(pos);
			Bundle bundle = new Bundle();
			bundle.putString("Dispensary_id", dis_data);
			bundle.putString("distance", dis);
			bundle.putString("marker", marker);
			Intent intent = new Intent(mContext, Dispansary_Detail.class);
			intent.putExtras(bundle);
			((Activity) mContext).startActivity(intent);
		} catch (Exception e) {
			e.getMessage();
		}
	}

	public class ListBallonAdapter extends BaseAdapter {

		private OverlayItem[] items;
		private Context context;

		public ListBallonAdapter(Context ctx, OverlayItem[] inputs) {
			this.context = ctx;
			items = inputs;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (items == null || items.length == 0) {
				return 0;
			} else {
				return items.length;
			}
		}

		@Override
		public OverlayItem getItem(int position) {
			// TODO Auto-generated method stub
			return items[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = LayoutInflater.from(context).inflate(
					R.layout.ballon_map_overlay_list_row_view_holder, null);
			BallonListItemRowViewHolder viewHolder = new BallonListItemRowViewHolder(
					convertView);
			viewHolder.setData(getItem(position), position);
			return convertView;
		}

	}

	public class BallonListItemRowViewHolder {

		public TextView title;
		public LinearLayout layout;

		public BallonListItemRowViewHolder(View v) {
			title = (TextView) v.findViewById(R.id.balloon_item_title);
			layout = (LinearLayout) v.findViewById(R.id.balloon_inner_layout);
		}

		public void setData(OverlayItem item, int position) {
			if (position == currentIndex) {
				// layout.setBackgroundColor(Color.GREEN);
			}
			layout.setVisibility(VISIBLE);
			if (item.getTitle() != null) {
				title.setVisibility(VISIBLE);
				title.setText(item.getTitle());
			} else {
				title.setVisibility(GONE);
			}
		}
	}

	public void setData(OverlayItem item, int index) {
		currentIndex = index;
		layout.setVisibility(VISIBLE);

		if (DispansaryApplication.map_flag == 1) {

			if (item.getTitle() != null) {
				title.setVisibility(VISIBLE);
				title.setText(item.getTitle());
			} else {
				title.setVisibility(GONE);
			}
			if (item.getSnippet() != null) {
				snippet.setVisibility(VISIBLE);
				snippet.setText(item.getSnippet());
			} else {
				snippet.setVisibility(GONE);
			}

		}
	}
}