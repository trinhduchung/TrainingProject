package bohemian.ex;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageColorPickerActivity extends Activity {
	private Uri imageUri;
	private ImageView mImageView;
	private TextView mColorTextView;
	private ImageView mColorImageView;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK) {
			if (requestCode == CAPTURE_IMAGE_SURFACE) {
				Uri uri = data.getParcelableExtra("path");
				imageUri = uri;
				setImage();
			} else if (requestCode == CAPTURE_IMAGE) {
				setImage();
			}
		}
		
	}

	private static final int CAPTURE_IMAGE = 1;
	private static final int CAPTURE_IMAGE_SURFACE = 2;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mImageView = (ImageView) findViewById(R.id.img);
		mColorTextView = (TextView) findViewById(R.id.lblColor);
		mColorImageView = (ImageView) findViewById(R.id.colorImg);
	}
	
	public void getColorBitmap(Bitmap bitmap) {
		int picw = 1;//bitmap.getWidth();
		int pich = 1;//bitmap.getHeight();
		int[] pix = new int[picw * pich];
		bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);
		long color = bitmap.getPixel(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
//		System.out.printf("Color at %d , %d : %l",bitmap.getWidth() / 2 , bitmap.getHeight() / 2, color);
		int R = 0, G = 0, B = 0;
		for (int y = 0; y < pich; y++) {
			for (int x = 0; x < picw; x++) {
				int index = y * picw + x;
				R = (pix[index] >> 16) & 0xff; // bitwise shifting
				G = (pix[index] >> 8) & 0xff;
				B = pix[index] & 0xff;
					
				// R,G.B - Red, Green, Blue
				// to restore the values after RGB modification, use
				// next statement
				pix[index] = 0xff000000 | (R << 16) | (G << 8) | B;
				Log.e("----------------------","R : " + R + ", G : " + G + ", B : " + B);
				mColorTextView.setText("Color : " + "R : " + R + ", G : " + G + ", B : " + B);
				mColorImageView.setBackgroundColor(pix[index]);
			}
		}
	}
	
	private void setImage() {
		try {
		// Read in bitmap of captured image
        Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        //set image 
        Drawable drawable = new BitmapDrawable(bitmap);
        mImageView.setBackgroundDrawable(drawable);
        //get color
        this.getColorBitmap(bitmap);
        
        // Create entry in media store for image
        // (Don't use insertImage() because it uses default compression setting of 50 - no way to change it)
        ContentValues values = new ContentValues();
        values.put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = null;
        try {
            uri = getContentResolver().insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (UnsupportedOperationException e) {
            System.out.println("Can't write to external media storage.");
            try {
                uri = getContentResolver().insert(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
            } catch (UnsupportedOperationException ex) {
                System.out.println("Can't write to internal media storage.");                           
//                this.fail("Error capturing image - no media storage found.");
                return;
            }
        }
        // Add compressed version of captured image to returned media store Uri
        OutputStream os  = getContentResolver().openOutputStream(uri);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.close();

        //bitmap.recycle();
        //bitmap = null;
        //System.gc();
        /*
        // Add image to results
        results.put(createMediaFile(uri));

        if (results.length() >= limit) {
            // Send Uri back to JavaScript for viewing image
            this.success(new PluginResult(PluginResult.Status.OK, results, "navigator.device.capture._castMediaFile"), this.callbackId);
        } else {
            // still need to capture more images
            captureImage();
        }
        */
    } catch (IOException e) {
        e.printStackTrace();
//        this.fail("Error capturing image.");
    }
	}
	
	public void onCaptureImage(View v) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File photo = new File(Environment.getExternalStorageDirectory(),  "capture.jpg");
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        this.imageUri = Uri.fromFile(photo);

//        startActivityForResult(intent, CAPTURE_IMAGE);
        Intent i = new Intent(this, ImageCapture.class);
        startActivityForResult(i, CAPTURE_IMAGE_SURFACE);
	}
	
}