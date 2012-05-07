package gnt.sd.view;

import gnt.sd.R;
import gnt.sd.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.Albums;
import android.provider.MediaStore.Audio.Artists;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SDAlbumCoverArt extends ImageView implements OnClickListener {

	Context _context;
	public long _id;
	long _artistId;
	boolean _isCreateReflected;
	String _artist;
	String _song;
	public SDAlbumCoverArt(Context context) {
		super(context);
		_context = context;
		setScaleType(ScaleType.FIT_XY);
		setImageResource(R.drawable.iwood);
	}

	public SDAlbumCoverArt(Context context, boolean isCreateReflected) {
		super(context);
		_context = context;
		_isCreateReflected = isCreateReflected;
		setScaleType(ScaleType.MATRIX);
		float scale = context.getResources().getDisplayMetrics().density;
		CoverFlow.LayoutParams layoutParam = new CoverFlow.LayoutParams(
				(int) (280 * scale), (int) (280 * scale));
		setLayoutParams(layoutParam);
		if (isCreateReflected == true) {
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.iwood);
			Bitmap refrectBitmap = Util.createReflectedImages(bitmap);
			bitmap.recycle();
			setImageBitmap(refrectBitmap);
//			this.setClickable(true);
//			this.setOnClickListener(this);
		} else {
			setImageResource(R.drawable.iwood);
		}
	}

	public SDAlbumCoverArt(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		setScaleType(ScaleType.FIT_XY);
		setImageResource(R.drawable.iwood);
	}

	public void getCoverArt(long id) {
		_id = id;
		Thread _thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				String dir = _context.getCacheDir() + "/" + _id + "_ab.png";
				File file = new File(dir);
				if (file.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(dir);
					if (bitmap != null) {
						if (_isCreateReflected) {
							Bitmap refrectBitmap = Util
									.createReflectedImages(bitmap);
							bitmap.recycle();
							setImage(refrectBitmap);
						} else {
							setImage(bitmap);
						}
					} 
				} else {
					Uri sArtworkUri = Uri
							.parse("content://media/external/audio/albumart");
					Uri uri = ContentUris.withAppendedId(sArtworkUri, _id);
					ContentResolver res = _context.getContentResolver();
					try {
						InputStream in = res.openInputStream(uri);
						Bitmap bitmap = BitmapFactory.decodeStream(in);
						FileOutputStream fos = new FileOutputStream(dir);
						bitmap.compress(CompressFormat.PNG, 100, fos);
						fos.close();
						if (_isCreateReflected) {
							Bitmap refrectBitmap = Util
									.createReflectedImages(bitmap);
							bitmap.recycle();
							setImage(refrectBitmap);
						} else {
							setImage(bitmap);
						}
						in.close();
					} catch (Exception e) {
						
						e.printStackTrace();
					} 
				}
			}
		});
		_thread.start();
	}

	
	public void getCoverArt(long id,String song, String artist) {
		_id = id;
		_artist = artist;
		_song = song;
		Thread _thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				String dir = _context.getCacheDir() + "/" + _id + "_ab.png";
				File file = new File(dir);
				if (file.exists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(dir);
					if (bitmap != null) {
						if (_isCreateReflected) {
							Bitmap refrectBitmap = Util
									.createReflectedImages(bitmap, _song, _artist);
							bitmap.recycle();
							setImage(refrectBitmap);
						} else {
							setImage(bitmap);
						}
					} else {
						Bitmap deBitmap = BitmapFactory.decodeResource(getResources(),
								R.drawable.iwood);
						Bitmap refrectBitmap = Util.createReflectedImages(deBitmap, _song, _artist);
						deBitmap.recycle();
						setImage(refrectBitmap);
					}
				} else {
					Uri sArtworkUri = Uri
							.parse("content://media/external/audio/albumart");
					Uri uri = ContentUris.withAppendedId(sArtworkUri, _id);
					ContentResolver res = _context.getContentResolver();
					try {
						InputStream in = res.openInputStream(uri);
						Bitmap bitmap = BitmapFactory.decodeStream(in);
						FileOutputStream fos = new FileOutputStream(dir);
						bitmap.compress(CompressFormat.PNG, 100, fos);
						fos.close();
						if (_isCreateReflected) {
							Bitmap refrectBitmap = Util
									.createReflectedImages(bitmap, _song, _artist);
							bitmap.recycle();
							setImage(refrectBitmap);
						} else {
							setImage(bitmap);
						}
						in.close();
					} catch (Exception e) {
						Bitmap deBitmap = BitmapFactory.decodeResource(getResources(),
								R.drawable.iwood);
						Bitmap refrectBitmap = Util.createReflectedImages(deBitmap, _song, _artist);
						deBitmap.recycle();
						setImage(refrectBitmap);
						e.printStackTrace();
					} 
				}
			}
		});
		_thread.start();
	}

	public void getArtistCoverArt(long id) {
		ContentResolver contentResolver = _context.getContentResolver();
		Cursor c = contentResolver.query(
				Artists.Albums.getContentUri("external", id), null, null, null,
				Albums.DEFAULT_SORT_ORDER);
		if (c != null) {
			try {
				if (c.moveToNext()) {
					long albumId = c.getLong(c.getColumnIndex(BaseColumns._ID));
					getCoverArt(albumId);
				}
				c.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	public void setImage(final Bitmap bitmap) {
		post(new Runnable() {
			@Override
			public void run() {
				setImageBitmap(bitmap);
			}
		});
	}

	public void getPlaylistCover(long id) {

	}

	@Override
	public void onClick(View v) {
//		SDListSongShortView shotView = new SDListSongShortView(_context, _id);
//		shotView.setVisibility(View.INVISIBLE);
//		ViewGroup parent = (ViewGroup)getRootView();
//	    parent.addView(shotView);
//	    applyRotation(this, shotView, 0, 90);
		
	}

	
}
