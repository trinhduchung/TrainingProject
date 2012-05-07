package gnt.sd.view;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SDAlbumLandAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private SDAlbumCoverArt[] mImages;
	Cursor _cursor;
	Context _context;
	public SDAlbumLandAdapter(Context context, Cursor c) {
		_cursor = c;
		_context = context;
		mImages = new SDAlbumCoverArt[_cursor.getCount()];
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
		_cursor.moveToPosition(position);
		return _cursor.getLong(_cursor
				.getColumnIndex(BaseColumns._ID));
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
			_cursor.moveToPosition(position);
			mImages[position] = new SDAlbumCoverArt(_context, true);
			mImages[position].getCoverArt(_cursor.getLong(_cursor
					.getColumnIndex(BaseColumns._ID)), _cursor.getString(_cursor.getColumnIndex(Albums.ALBUM)), _cursor.getString(_cursor.getColumnIndex(Albums.ARTIST)));
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
