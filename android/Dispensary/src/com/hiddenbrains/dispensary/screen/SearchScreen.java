package com.hiddenbrains.dispensary.screen;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hiddenbrains.dispensary.common.DispensaryConstant;

public class SearchScreen extends Activity implements OnClickListener{
	ImageButton btn_location,btn_search,btn_doctors;
	ImageButton btn_search_normal;
	private EditText et_city,et_zip;
	public static String search="mar";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
			if(DispensaryConstant.global_flag==0){
				Intent int1=new Intent(this,DispensaryListScreen.class);
			    startActivity(int1);
			}else if(DispensaryConstant.global_flag==3){
				Intent int1=new Intent(this,Doctors_Clinic_List.class);
			    startActivity(int1);
			}else{
				et_zip=(EditText) findViewById(R.id.enter_state);
			
				btn_location=(ImageButton) findViewById(R.id.d_btn_dispansary);
		        btn_search=(ImageButton) findViewById(R.id.d_btn_search);
		        btn_doctors=(ImageButton) findViewById(R.id.d_btn_doctors);
		        btn_search_normal=(ImageButton) findViewById(R.id.s_btn_search);
		        
		        btn_location.setOnClickListener(this);
		        btn_search.setOnClickListener(this);
		        btn_doctors.setOnClickListener(this);
		        btn_search_normal.setOnClickListener(this);
			}
		
			
	}
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.d_btn_dispansary:
			Intent intent=new Intent(this,Doctors_Clinic_List.class);
				//intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				DispensaryConstant.global_flag=0;
				break;
		case R.id.d_btn_doctors:
				Intent intent1=new Intent(this,Doctors_Clinic_List.class);
			//	intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent1);
				DispensaryConstant.global_flag=3;
				break;
		case R.id.s_btn_search:
			    try{
			    if(et_zip.getText().toString().equals("")){
					Builder builder = new AlertDialog.Builder(SearchScreen.this);
					builder.setTitle("Search");
					builder.setMessage("Enter one name keyword");
					builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							//finish();
						}
					});
					AlertDialog alert = builder.create();
				    alert.show();	
				}
				else{
					    Bundle bundle=new Bundle();
						bundle.putString("state_name",et_zip.getText().toString());
						Intent intent2=new Intent(this,From_Main_Search.class);
						intent2.putExtras(bundle);
						startActivity(intent2);
				}
			    }
				catch(IllegalArgumentException e){
					e.getMessage();
				}
				catch(Exception e){
					e.getMessage();
				}
			    break;
		 
		}
	}
}
