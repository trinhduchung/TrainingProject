package gnt.sd.view;

import gnt.sd.R;

import java.io.File;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

public class  SDPlaylistCoverArt extends ImageView {

	Context _context;
	public SDPlaylistCoverArt(Context context) {
		super(context);
		_context = context;
		setScaleType(ScaleType.FIT_XY);
		setImageResource(R.drawable.no_cover_art);

	}

	public SDPlaylistCoverArt(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		setScaleType(ScaleType.FIT_XY);
		setImageResource(R.drawable.no_cover_art);
	}

	public void getCoverArt(long id) {
		String dir = _context.getCacheDir() + "/" + id + "_pl.png";
		File file = new File(dir);
		if (file.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(dir);
			if (bitmap != null) {
				setImageBitmap(bitmap);
			} 
		}
	}
	
	public static void saveCoverArt(Context context, Bitmap bitmap, long id) {
		String dir = context.getCacheDir() + "/" + id + "_pl.png";
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(dir);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
