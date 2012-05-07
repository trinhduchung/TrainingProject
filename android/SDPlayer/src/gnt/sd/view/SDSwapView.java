package gnt.sd.view;

import android.view.View;
import android.view.animation.DecelerateInterpolator;

public final class SDSwapView implements Runnable {
	View _image1;
	View _image2;
	int _argee;
	public SDSwapView(View image1, View image2, int argee) {
		this._image1 = image1;
		this._image2 = image2;
		_argee = argee;
	}

	@Override
	public void run() {
		final float centerX = _image1.getWidth() / 2.0f;
		final float centerY = _image1.getHeight() / 2.0f;
		SDFlipAnimation rotation;
			_image1.setVisibility(View.GONE);
			_image2.setVisibility(View.VISIBLE);
			_image2.requestFocus();
			rotation = new SDFlipAnimation(-_argee, 0, centerX, centerY);
		
		rotation.setDuration(500);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new DecelerateInterpolator());
		_image2.startAnimation(rotation);
	}
}