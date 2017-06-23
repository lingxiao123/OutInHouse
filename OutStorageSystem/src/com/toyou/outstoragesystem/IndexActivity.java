package com.toyou.outstoragesystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class IndexActivity extends Activity {
	private ImageView iv_ccpd;
	private ImageView iv_jhzx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_index);
		iv_ccpd=(ImageView) findViewById(R.id.imageCcpd);
		iv_jhzx=(ImageView) findViewById(R.id.imageJhzx);
		iv_jhzx.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(IndexActivity.this, CheckOddNumberActivity.class);
				startActivity(intent);
			}
		});
		iv_ccpd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(IndexActivity.this, ProductWareHousingActivity.class);
				startActivity(intent);
			}
		});
	}
}
