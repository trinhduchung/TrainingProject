package gnt.sd.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SDArtistImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private SDArtistImage[] mImages;
	private ArrayList<String> _listImage;

	public SDArtistImageAdapter(Context c, ArrayList<String> listImage) {
		_listImage = listImage;
		mImages = new SDArtistImage[_listImage.size()];
		for(int i = 0; i< mImages.length; i++) {
			mImages[i] = new SDArtistImage(c);
			mImages[i].downloadImage(_listImage.get(i));
		}
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
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// Use this code if you want to load from resources
		// ImageView i = new ImageView(mContext);
		// i.setImageResource(mImageIds[position]);
		// i.setLayoutParams(new CoverFlow.LayoutParams(130, 130));
		// i.setScaleType(ImageView.ScaleType.MATRIX);
		// return i;

		return mImages[position]._imageView;
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
