package gnt.sd;

import gnt.sd.controller.Service;
import gnt.sd.controller.ServiceListener;
import gnt.sd.controller.ServiceRespone;
import gnt.sd.view.CoverFlow;
import gnt.sd.view.SDArtistImageAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SDBioActivity extends Activity implements ServiceListener {
	CoverFlow _coverFlow;
	TextView _textTitle;
	TextView _textBio;
	Service _serviceGetBio;
	ProgressBar _loadingBio;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.artistbio_view);
		String artist = getIntent().getStringExtra("artist");
		String bio = getIntent().getStringExtra("bio");
		_coverFlow = (CoverFlow) findViewById(R.id.artistbio_coverflow);
		_coverFlow.setSpacing(-15);
		_textTitle = (TextView) findViewById(R.id.artistbio_title);
		_textTitle.setText(artist);
		_textTitle = (TextView) findViewById(R.id.artistbio_bio);
		_textTitle.setText(bio);
		_loadingBio = (ProgressBar) findViewById(R.id.artistbio_loadbio);
		_serviceGetBio = new Service(this);
		_serviceGetBio.getArtistImage(artist);
	}

	@Override
	public void onComplete(Service service, ServiceRespone result) {
		if (result.isSuccess()) {
			@SuppressWarnings({ "unchecked" })
			ArrayList<String> list = (ArrayList<String>) result.getData();
			SDArtistImageAdapter coverImageAdapter = new SDArtistImageAdapter(
					this, list);
			_coverFlow.setAdapter(coverImageAdapter);
			_coverFlow.setSelection(list.size()/2, true);
		}
		_loadingBio.setVisibility(View.GONE);
	}
}
