package com.hiddenbrains.dispensary.screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hiddenbrains.dispensary.common.DispensaryConstant;

public class SearchFromClinicList extends Activity implements OnClickListener{
	private ImageButton btn_dispansary_list,btn_search,btn_doctors,btn_back;
	private EditText clinic_name;
	private ImageButton search;
	//private ImageButton btn_search_here;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchfrom_clinic);
		   btn_dispansary_list=(ImageButton) findViewById(R.id.d_btn_dispansary);
	       btn_search=(ImageButton) findViewById(R.id.d_btn_search);
	       btn_doctors=(ImageButton) findViewById(R.id.d_btn_doctors);
	       btn_back=(ImageButton) findViewById(R.id.s_btn_back);
	       search=(ImageButton) findViewById(R.id.btn_search_by_name);
	      
	       clinic_name=(EditText) findViewById(R.id.enter_Clinic_name);
	      // btn_search_here=(ImageButton) findViewById(R.id.s_btn_search);
	       
	       search.setOnClickListener(this);
	       btn_doctors.setOnClickListener(this);
	       btn_dispansary_list.setOnClickListener(this);
	       btn_search.setOnClickListener(this);
	       btn_back.setOnClickListener(this);
	}
	public void onClick(View v) {
        switch(v.getId()){
		
		case R.id.d_btn_dispansary:
			DispensaryConstant.global_flag=0;
			Intent intent2=new Intent(this,Doctors_Clinic_List.class);
			intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent2);
						break;
		case R.id.d_btn_search:
			DispensaryConstant.global_flag=2;
			Intent intent1=new Intent(this,Doctors_Clinic_List.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent1);
					break;
		case R.id.d_btn_doctors:finish();
						break;
		case R.id.s_btn_back:
			finish();
						break;
		case R.id.btn_search_by_name:
							Intent int2=new Intent(this,Doctors_Clinic_List.class);
							Bundle bundle=new Bundle();
							String str=clinic_name.getText().toString();
							String state_name1=str.replace(" ","%20");
							if(clinic_name.getText().toString().equals(""))
							Toast.makeText(this,"Enter name",Toast.LENGTH_SHORT).show();
							else{
							bundle.putString("city",state_name1);
							int2.putExtras(bundle);
							startActivity(int2);
							Doctors_Clinic_List.flag=true;
							}
						break;
			}	
	}
        
}
