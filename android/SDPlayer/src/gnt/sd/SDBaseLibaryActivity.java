package gnt.sd;

import gnt.sd.view.SDPlayerMiniView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
public class SDBaseLibaryActivity extends Activity {
	LinearLayout _headerView;
	ListView	_listView;
	SDPlayerMiniView _miniPlayer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_view);
		_headerView = (LinearLayout) findViewById(R.id.baseview_header);
		_listView = (ListView) findViewById(R.id.baseview_list);
		_miniPlayer = (SDPlayerMiniView) findViewById(R.id.baseview_miniplayer);
	}
	
	public void onCoverArtClicked(View v) {
		SDApplication._instance.putData(SDPlayerActivity.CODE_RELOAD, true);
		Intent intent = new Intent(this, SDPlayerActivity.class);
		startActivity(intent);
	}
}
