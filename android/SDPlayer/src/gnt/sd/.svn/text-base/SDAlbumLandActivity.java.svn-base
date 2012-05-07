package gnt.sd;

import gnt.sd.util.Util;
import gnt.sd.view.CoverAdapterView;
import gnt.sd.view.CoverAdapterView.OnItemClickListener;
import gnt.sd.view.SDAlbumCoverArt;
import gnt.sd.view.SDAlbumCoverFlow;
import gnt.sd.view.SDAlbumLandAdapter;
import gnt.sd.view.SDListSongShortView;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class SDAlbumLandActivity extends Activity implements
		OnItemClickListener {
	public static SDAlbumCoverFlow _coverFlow;
	Cursor _cursor;
	SDAlbumLandAdapter _adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album_land_view);
		_coverFlow = (SDAlbumCoverFlow) findViewById(R.id.albumland_coverflow);
		_coverFlow.setSpacing(-40);
		_coverFlow.setOnItemClickListener(this);
		_cursor = getContentResolver().query(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null,
				AlbumColumns.ALBUM + " ASC");
		_adapter = new SDAlbumLandAdapter(this, _cursor);
		_coverFlow.setAdapter(_adapter);
		_coverFlow.setSelection(_cursor.getCount() / 2, true);
	}

	@Override
	public void onItemClick(CoverAdapterView<?> parent, View view,
			int position, long id) {
		SDListSongShortView shotView = new SDListSongShortView(this, (SDAlbumCoverArt) view);
		shotView.setVisibility(View.INVISIBLE);
		this.addContentView(shotView, new ViewGroup.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		Util.applyRotation(view, shotView, 0, 90);
	}

}
