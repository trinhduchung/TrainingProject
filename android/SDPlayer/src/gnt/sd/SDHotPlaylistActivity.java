package gnt.sd;

import gnt.sd.controller.Service;
import gnt.sd.controller.ServiceAction;
import gnt.sd.controller.ServiceListener;
import gnt.sd.controller.ServiceRespone;
import gnt.sd.model.SDAudio;
import gnt.sd.model.SDPlaylist;
import gnt.sd.view.SDAlbumCoverArt;
import gnt.sd.view.SDArtistAdapter.SDArtistTableCell;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.provider.MediaStore.Audio.Playlists;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SDHotPlaylistActivity extends SDBaseLibaryActivity implements OnItemClickListener{
	
	private ServiceListener _listener = new ServiceListener() {
		
		@Override
		public void onComplete(Service service, ServiceRespone result) {
			Message message = handler.obtainMessage(1);
			handler.sendMessageDelayed(message, 20000);
			if(result.getAction() == ServiceAction.ActionGetHotVPop) {
				if(result.isSuccess()) {
					List<SDAudio> listSong = (List<SDAudio>) result.getData();
					_listHotVPop.add(listSong.get(0));
					_listHotVPop.add(listSong.get(1));
					_listHotVPop.add(listSong.get(2));
					Message msg = handler.obtainMessage(0, null);
					handler.sendMessage(msg);
				}
			}
			if(result.getAction() == ServiceAction.ActionGetHotKPop) {
				if(result.isSuccess()) {
					List<SDAudio> listSong = (List<SDAudio>) result.getData();
					_listHotKPop.add(listSong.get(0));
					_listHotKPop.add(listSong.get(1));
					_listHotKPop.add(listSong.get(2));
					Message msg = handler.obtainMessage(0, null);
					handler.sendMessage(msg);
				}
			}
			if(result.getAction() == ServiceAction.ActionGetHotAuMy) {
				if(result.isSuccess()) {
					List<SDAudio> listSong = (List<SDAudio>) result.getData();
					_listHotAuMy.add(listSong.get(0));
					_listHotAuMy.add(listSong.get(1));
					_listHotAuMy.add(listSong.get(2));
					Message msg = handler.obtainMessage(0, null);
					handler.sendMessage(msg);
				}
			}
		}
	};
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 0) {
				if(_listHotVPop.size() == 9 && _listHotKPop.size() == 9 && _listHotAuMy.size() == 9) {
					_wDialog.dismiss();
					Log.d("Playlist", "Size = " + _listHotVPop.size() + ";" + _listHotKPop.size() + ";" + _listHotAuMy.size());
				} else {
					Message message = handler.obtainMessage(1);
					handler.sendMessageDelayed(message, 20000);
				}
			}else if (msg.what == 1) {
				if(_wDialog.isShowing()) {
					_wDialog.dismiss();
				}
			}
		}
		
	};
	
	private Service _getHotVPopA;
	private Service _getHotVPopB;
	private Service _getHotVPopC;
	
	private Service _getHotKPopA;
	private Service _getHotKPopB;
	private Service _getHotKPopC;
	
	private Service _getHotAuMyA;
	private Service _getHotAuMyB;
	private Service _getHotAuMyC;
	
	private List<SDAudio> _listHotVPop;
	private List<SDAudio> _listHotKPop;
	private List<SDAudio> _listHotAuMy;
	
	private HotPlaylistAdapter _adapter;
	private String[] _playlistHot = {"Nhạc Hot Việt Nam","Nhạc Hot Hàn Quốc","Nhạc Hot Âu Mỹ"};
	
	private ProgressDialog _wDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_headerView.setVisibility(View.GONE);
		_getHotVPopA = new Service(_listener);
		_getHotVPopB = new Service(_listener);
		_getHotVPopC = new Service(_listener);
		_adapter = new HotPlaylistAdapter(this, _playlistHot);
		_listView.setAdapter(_adapter);
		_listView.setOnItemClickListener(this);
		
		_getHotKPopA = new Service(_listener);
		_getHotKPopB = new Service(_listener);
		_getHotKPopC = new Service(_listener);
		
		_getHotAuMyA = new Service(_listener);
		_getHotAuMyB = new Service(_listener);
		_getHotAuMyC = new Service(_listener);
		
		_listHotVPop = new ArrayList<SDAudio>();
		_listHotKPop = new ArrayList<SDAudio>();
		_listHotAuMy = new ArrayList<SDAudio>();
		
		_getHotVPopA.getHotVPop("Thủy Tiên");
		_getHotVPopB.getHotVPop("Khắc Việt");
		_getHotVPopC.getHotVPop("Cao Thái Sơn");
		
		_getHotKPopA.getHotKPop("SNSD");
		_getHotKPopB.getHotKPop("Secret");
		_getHotKPopC.getHotKPop("Kara");
		
		_getHotAuMyA.getHotAuMy("The Beatles");
		_getHotAuMyB.getHotAuMy("Westlife");
		_getHotAuMyC.getHotAuMy("Madonna");
		
		_wDialog = ProgressDialog.show(this, "", "Waiting for load playlist",true,true);
		_wDialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private class HotPlaylistAdapter extends BaseAdapter {
		SDHotPlaylistRowViewHolder[] _listCache;
		Context _context;
		String[] _items;
		
		public HotPlaylistAdapter(Context context, String[] list) {
			_context = context;
			_items = list;
			_listCache = new SDHotPlaylistRowViewHolder[_items.length];
		}
		
		@Override
		public int getCount() {
			if(_items != null || _items.length > 0) {
				return _items.length;
			} else {
				return 0;
			}
		}

		@Override
		public String getItem(int postition) {
			return _items[postition];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			String name = _items[position];
			_listCache[position] = new SDHotPlaylistRowViewHolder(_context);
			_listCache[position]._textArtist.setText(name);
			return _listCache[position];
		}
		
		public class SDHotPlaylistRowViewHolder extends RelativeLayout {
			TextView _textArtist;

			public SDHotPlaylistRowViewHolder(Context context) {
				super(context);
				LayoutInflater.from(context).inflate(R.layout.listartist_tablecell,
						this);
				_textArtist = (TextView) findViewById(R.id.artistCell_artist);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		List<SDAudio> listSong = new ArrayList<SDAudio>();
		if(position == 0) {
			listSong = _listHotVPop;
		} else if (position == 1) {
			listSong = _listHotKPop;
		} else if (position == 2) {
			listSong = _listHotAuMy;
		}
		//Switch to list activity
		SDApplication.Instance().putData("list", listSong);
		SDApplication.Instance().putData("artist", _playlistHot[position]);
		startActivity(new Intent(this, SDListPlayStreamingActivity.class));
	}
}
