package gnt.sd.view;

import gnt.sd.model.SDAudio;

import java.util.List;

import android.content.Context;
import android.provider.BaseColumns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SDPlayerCoverFlowAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private SDAlbumCoverArt[] mImages;
	List<SDAudio> _listSong;
	Context _context;
	public SDPlayerCoverFlowAdapter(Context context, List<SDAudio> list) {
		_listSong = list;
		_context = context;
		mImages = new SDAlbumCoverArt[list.size()];
	}

	@Override
	public int getCount() {
		return mImages.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		if( _listSong.get(position) != null)
			return _listSong.get(position).getId();
		return -1;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Use this code if you want to load from resources
		// ImageView i = new ImageView(mContext);
		// i.setImageResource(mImageIds[position]);
		// i.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
		// i.setScaleType(ImageView.ScaleType.MATRIX);
		// return i;
		if(mImages[position] == null) {
			mImages[position] = new SDAlbumCoverArt(_context, true);
			if(_listSong.get(position) != null)
				mImages[position].getCoverArt(_listSong.get(position).getAlbumId(), _listSong.get(position).getTitle(), _listSong.get(position).getArtist());
		}
		return mImages[position];
		
	}
	/**
	 * Returns the size (0.0f to 1.0f) of the views depending on the 'offset' to
	 * the center.
	 */
	public float getScale(boolean focused, int offset) {
		/* Formula: 1 / (2 ^ offset) */
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

}
