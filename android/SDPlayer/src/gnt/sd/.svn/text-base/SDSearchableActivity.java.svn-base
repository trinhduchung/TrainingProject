package gnt.sd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SDSearchableActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_view);
//		 final Intent queryIntent = getIntent();
//	      final String queryAction = queryIntent.getAction();
//	      if (Intent.ACTION_SEARCH.equals(queryAction)) {
//	         Log.d("SearchActivity", "Search Invoke");
//	         String searchKeywords = queryIntent.getStringExtra(SearchManager.QUERY);
//	      }
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	     // String query = intent.getStringExtra(SearchManager.QUERY);
	     // doMySearch(query);
	    }
	}

}
