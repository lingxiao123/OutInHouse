package com.toyou.outstoragesystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class CheckOddNumberActivity extends Activity {
	private EditText txt_checkNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkoddnumber);
		txt_checkNumber=(EditText) findViewById(R.id.txt_checkNumber);
		
		txt_checkNumber.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(CheckOddNumberActivity.this, CheckBillActivity.class);
				startActivity(intent);
			}
		});
	}
}
