package gnt.sd.view;

import android.view.View;
import android.view.animation.Animation;

public final class SDDisplayNextView implements Animation.AnimationListener {
	View _image1;
	View _image2;
	int _argee;
	public SDDisplayNextView( View image1,
			View image2, int argee) {
		this._image1 = image1;
		this._image2 = image2;
		_argee = argee;
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		_image1.post(new SDSwapView(_image1, _image2, _argee));
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}
}