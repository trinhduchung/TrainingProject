/**
 * 
 */
package bohemian.ex;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author bohemian
 *
 */
public class ImageCapture extends Activity implements Callback, OnClickListener {
	
	private Camera camera = null;
	private SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
    
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Button btnCapture;
    private Button btnUse;
    private Uri filePath;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e(getClass().getSimpleName(), "onCreate");
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.video_view);
		surfaceView = (SurfaceView) findViewById(R.id.cameraView);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		btnCapture = (Button) findViewById(R.id.buttonCapture);
		btnCapture.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				captureImage();
				btnCapture.setClickable(false);
				btnUse.setVisibility(View.VISIBLE);
			}
		});
		btnUse = (Button) findViewById(R.id.buttonUse);
	}
	
	public void onButtonUseClicked(View v) {
		setResult();
	}
	
	private void setResult() {
		Intent intent = new Intent();
		intent.putExtra("path", filePath);
		setResult(RESULT_OK, intent);
		finish();
	}  
	
	private void captureImage() {
		ImageCaptureCallback iccb = null;
		try {
			String filename = timeStampFormat.format(new Date());
			ContentValues values = new ContentValues();
			values.put(Media.TITLE, filename);
			values.put(Media.DESCRIPTION, "Image capture by camera");
			Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);

			iccb = new ImageCaptureCallback(getContentResolver().openOutputStream(uri));
			filePath = uri;
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.e(getClass().getSimpleName(), ex.getMessage(), ex);
		}

		camera.takePicture(mShutterCallback, mPictureCallbackRaw, iccb);
	}
	
	public String osVersion() {
    	String osVer = android.os.Build.VERSION.RELEASE;
        return osVer;
    }
	
	private boolean OsVersionIsOver22() {
		char[] str;
		str = osVersion().toCharArray();
		if(str[0] > '2'){
			return true;
		} else if (str[0] == '2' && str[2] > '2') {
			return true;
		} else {
			return false;
		}
	}
	
	//on android 2.3 and later
	private Camera getFrontFacingCamera() {
		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();
		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				try {
					cam = Camera.open(camIdx);
				} catch (RuntimeException e) {
				}
			}
		}
		return cam;
	}
	
	Camera.PictureCallback mPictureCallbackRaw = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera c) {
            Log.e(getClass().getSimpleName(), "PICTURE CALLBACK RAW: " + data);
//            setResult();
//            camera.startPreview();
        }
    };
    
    Camera.PictureCallback mPictureCallbackJpeg = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera c) {
            Log.e(getClass().getSimpleName(), "PICTURE CALLBACK JPEG: data.length = " + data);
        }
    };
    
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
    	public void onShutter() {
    		Log.e(getClass().getSimpleName(), "SHUTTER CALLBACK");
//    		setResult();
    	}
    };
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		ImageCaptureCallback iccb = null;
    	if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
    	try {
    		String filename = timeStampFormat.format(new Date());
    		ContentValues values = new ContentValues();
    		values.put(Media.TITLE, filename);
    		values.put(Media.DESCRIPTION, "Image capture by camera");
    		Uri uri = getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, values);
    		
    		iccb = new ImageCaptureCallback( getContentResolver().openOutputStream(uri));
    		filePath = uri;
    	} catch(Exception ex ){
    		ex.printStackTrace();
    		Log.e(getClass().getSimpleName(), ex.getMessage(), ex);
    	}
    	}
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	setResult();
            return true;
        }
 
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            camera.takePicture(mShutterCallback, mPictureCallbackRaw, iccb);
            return true;
        }
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
	 */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w,
			int h) {
		// TODO Auto-generated method stub
		Camera.Parameters parameters = camera.getParameters();
		parameters.setPreviewSize(w, h);
		camera.setParameters(parameters);
		camera.startPreview();
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		Log.e(getClass().getSimpleName(), "surfaceCreated");
        if(OsVersionIsOver22()) {
        	Log.d("SUCCCCCCCCCCC", "0000" + holder);
        	camera = getFrontFacingCamera();
        }
       	if(camera == null){
       		Log.d("SUCCCCCCCCCCC", "00001" + holder);
       		camera = Camera.open();
       	}
       	try {
			camera.setPreviewDisplay(holder);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		if (null != camera) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		finish();
	}

}
