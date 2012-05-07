package gnt.sd.view;

import gnt.sd.R;
import gnt.sd.controller.Service;
import gnt.sd.controller.ServiceListener;
import gnt.sd.controller.ServiceRespone;
import gnt.sd.util.Util;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class SDArtistImage extends RelativeLayout implements ServiceListener {
	ProgressBar _progessBar;
	ImageView _imageView;
	Context _context;
	long _id;
	long _artistId;
	Service _serviceDownload;

	public SDArtistImage(Context context) {
		super(context);
		_context = context;
		_imageView = new ImageView(context);
		_imageView.setScaleType(ScaleType.MATRIX);
		Bitmap bitmap =BitmapFactory.decodeResource(getResources(), R.drawable.no_cover_art);
		Bitmap refrectBitmap = Util.createReflectedImages(bitmap);
		bitmap.recycle();
		_imageView.setImageBitmap(refrectBitmap);
		float scale = context.getResources().getDisplayMetrics().density;
		CoverFlow.LayoutParams layoutParam = new CoverFlow.LayoutParams(
				(int) (150 * scale), (int) (150 * scale));
		_imageView.setLayoutParams(layoutParam);
		addView(_imageView);
		_progessBar = new ProgressBar(context);
		addView(_progessBar);

	}

	public SDArtistImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
		_imageView = new ImageView(context);
		_imageView.setScaleType(ScaleType.MATRIX);
		Bitmap bitmap =BitmapFactory.decodeResource(getResources(), R.drawable.no_cover_art);
		Bitmap refrectBitmap = Util.createReflectedImages(bitmap);
		bitmap.recycle();
		_imageView.setImageBitmap(refrectBitmap);
		float scale = context.getResources().getDisplayMetrics().density;
		CoverFlow.LayoutParams layoutParam = new CoverFlow.LayoutParams(
				(int) (150 * scale), (int) (150 * scale));
		_imageView.setLayoutParams(layoutParam);
		addView(_imageView);
		_progessBar = new ProgressBar(context);
		addView(_progessBar);
	}

	public void downloadImage(String path) {
		if (_serviceDownload == null) {
			_serviceDownload = new Service(this);
		}
		_serviceDownload.downloadImage(path);
	}

	public void getImage(String localPath) {

	}

	@Override
	public void onComplete(Service service, ServiceRespone result) {
		if (result.isSuccess()) {
			Bitmap bitmap = (Bitmap) result.getData();
			Bitmap refrectBitmap = Util.createReflectedImages(bitmap);
			bitmap.recycle();
			_imageView.setImageBitmap(refrectBitmap);
		}
		_progessBar.setVisibility(View.GONE);
	}
}
