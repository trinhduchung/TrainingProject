package gnt.sd;

import gnt.sd.view.SDPlaylistCoverArt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Audio.PlaylistsColumns;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class SDNewPlayListActivity extends Activity {

	ImageButton _btnCoverArt;
	EditText _edtTitle;
	Button _btnSave;
	Button _btnCancel;
	boolean _hadChooseCoverArt = false;
	long _playlist_id;
	int _mode;
	Cursor _cursorPlaylist;
	public final static int MODE_NEW = 0;
	public final static int MODE_UPDATE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newplaylist_view);
		_mode = getIntent().getIntExtra("mode", MODE_NEW);
		if (_mode == MODE_UPDATE) {
			_playlist_id = getIntent().getLongExtra("playlist_id", 0);
		}
		setupUI();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("Set Cover Art");
		menu.add(0, 0, 0, "Take photo");
		menu.add(0, 1, 0, "Choose from library");
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 0) {
			Intent intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent, 0);
		} else {
			startActivityForResult(
					new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
					1);
		}
		return super.onContextItemSelected(item);
	}

	public void setupUI() {
		_btnCoverArt = (ImageButton) findViewById(R.id.newplayist_buttonImage);
		registerForContextMenu(_btnCoverArt);
		_btnCoverArt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openContextMenu(_btnCoverArt);
			}
		});
		_edtTitle = (EditText) findViewById(R.id.newplaylist_name);
		if (_mode == MODE_UPDATE) {
			String dir = getCacheDir() + "/" + _playlist_id + "_pl.png";
			File file = new File(dir);
			if (file.exists()) {
				Bitmap bitmap = BitmapFactory.decodeFile(dir);
				if (bitmap != null) {
					BitmapDrawable bmDrawable = new BitmapDrawable(bitmap);
					_btnCoverArt.setBackgroundDrawable(bmDrawable);
				}
			}
			ContentResolver contentResolver = getApplication()
					.getContentResolver();
			long id = getIntent().getLongExtra("playlist_id", 0);
			String selection = BaseColumns._ID + "=?";
			String[] selectionArgs = new String[] { String.valueOf(id) };
			_cursorPlaylist = contentResolver.query(
					Playlists.EXTERNAL_CONTENT_URI, null, selection,
					selectionArgs, Playlists.DEFAULT_SORT_ORDER);
			if (!_cursorPlaylist.moveToFirst()) {
				finish();
			}
			_edtTitle.setText(_cursorPlaylist.getString(_cursorPlaylist
					.getColumnIndex(PlaylistsColumns.NAME)));
		}
		_btnSave = (Button) findViewById(R.id.newplayist_buttonSave);
		_btnCancel = (Button) findViewById(R.id.newplayist_buttonCancel);
		_btnSave.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = _edtTitle.getText().toString();
				if (name.length() > 0) {
					if (_mode == MODE_NEW) {
						ContentResolver contentResolver = getApplication()
								.getContentResolver();
						ContentValues cv = new ContentValues();
						cv.put(PlaylistsColumns.NAME, name);
						Uri uri = contentResolver.insert(
								Playlists.EXTERNAL_CONTENT_URI, cv);
						long id = Long.valueOf(uri.getLastPathSegment());
						Intent intent = getIntent();
						intent.putExtra("ADD", "OK");
						setResult(RESULT_OK, intent);
						if (_hadChooseCoverArt) {
							SDPlaylistCoverArt.saveCoverArt(
									SDNewPlayListActivity.this,
									((BitmapDrawable) _btnCoverArt
											.getBackground()).getBitmap(), id);
						}
					} else {
						ContentResolver contentResolver = getApplication()
								.getContentResolver();
						ContentValues cv = new ContentValues();
						cv.put(PlaylistsColumns.NAME, name);
						String selection = BaseColumns._ID + "=?";
						String[] selectionArgs = new String[] { String
								.valueOf(_playlist_id) };
						contentResolver.update(Playlists.EXTERNAL_CONTENT_URI,
								cv, selection, selectionArgs);
						Intent intent = getIntent();
						intent.putExtra("ADD", "OK");
						setResult(RESULT_OK, intent);
						if (_hadChooseCoverArt) {
							SDPlaylistCoverArt.saveCoverArt(
									SDNewPlayListActivity.this,
									((BitmapDrawable) _btnCoverArt
											.getBackground()).getBitmap(),
									_playlist_id);
						}
					}
					finish();
				}
			}
		});

		_btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (resultCode == Activity.RESULT_OK) {
				Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
				Bitmap resizeBitmap = gnt.sd.util.Util.resizeBimap(
						selectedImage, 70, 70);
				selectedImage.recycle();
				if (resizeBitmap != null) {
					BitmapDrawable bmDrawable = new BitmapDrawable(resizeBitmap);
					_btnCoverArt.setBackgroundDrawable(bmDrawable);
					_hadChooseCoverArt = true;
				}

			}
		} else if (requestCode == 1) {
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = data.getData();
				Bitmap resizeBitmap = null;
				try {
					Bitmap bitmap = MediaStore.Images.Media.getBitmap(
							this.getContentResolver(), selectedImage);
					resizeBitmap = gnt.sd.util.Util.resizeBimap(bitmap, 70, 70);
					bitmap.recycle();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (resizeBitmap != null) {
					BitmapDrawable bmDrawable = new BitmapDrawable(resizeBitmap);
					_btnCoverArt.setBackgroundDrawable(bmDrawable);
					_hadChooseCoverArt = true;
				}

			}
		}

	}
}
