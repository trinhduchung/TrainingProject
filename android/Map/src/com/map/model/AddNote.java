package com.map.model;

import com.map.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNote extends Activity {

	EditText txtNote;
	Button addNoteBtn;
	static DbAdapter dbAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnote);
        
        txtNote = (EditText)findViewById(R.id.txtNote);
        addNoteBtn = (Button)findViewById(R.id.btnAddnote);
        addNoteBtn.setOnClickListener(mAddnoteListener);
    }
    
    private View.OnClickListener mAddnoteListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			createNote();
			setResult(RESULT_OK);
			finish();
		}
	};
	
	private void createNote() {
		/*String noteName = txtNote.getText().toString();
		dbAdapter.insertNote(noteName, "");*/
		QueryImageDatabase t = new QueryImageDatabase();
		t.createNote("good man", "con ga keu ecec");
	}
	
	static void setDbAdapter(DbAdapter db){
		dbAdapter = db;
	}
}
