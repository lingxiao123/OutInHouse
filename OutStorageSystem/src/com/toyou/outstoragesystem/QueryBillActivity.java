package com.toyou.outstoragesystem;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class QueryBillActivity extends Activity {
	private EditText starttime;
	private EditText endtime;
	private Button btn_query;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_querybill);
		starttime=(EditText) findViewById(R.id.ksdate);
		endtime=(EditText) findViewById(R.id.jzdate);
		btn_query=(Button) findViewById(R.id.cxdanju);
		starttime.setInputType(InputType.TYPE_NULL);
		starttime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					Calendar c = Calendar.getInstance(Locale.CHINA);
					new DatePickerDialog(QueryBillActivity.this,
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									starttime.setText(year + "/" + (monthOfYear + 1)
											+ "/" + dayOfMonth);
								}
							}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
									.get(Calendar.DAY_OF_MONTH)).show();
				}
			}
		});
		starttime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance(Locale.CHINA);
				new DatePickerDialog(QueryBillActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								starttime.setText(year + "/" + (monthOfYear + 1)
										+ "/" + dayOfMonth);
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		endtime.setInputType(InputType.TYPE_NULL);
		endtime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					Calendar c = Calendar.getInstance(Locale.CHINA);
					new DatePickerDialog(QueryBillActivity.this,
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view,
										int year, int monthOfYear,
										int dayOfMonth) {
									endtime.setText(year + "/" + (monthOfYear + 1)
											+ "/" + dayOfMonth);
								}
							}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
									.get(Calendar.DAY_OF_MONTH)).show();
				}
			}
		});
		endtime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance(Locale.CHINA);
				new DatePickerDialog(QueryBillActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								endtime.setText(year + "/" + (monthOfYear + 1)
										+ "/" + dayOfMonth);
							}
						}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		btn_query.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sharedPreferences=getSharedPreferences("dateDataBase", MODE_PRIVATE);
				editor=sharedPreferences.edit();
				editor.putString("starttime",starttime.getText().toString().trim());
				editor.putString("endtime", endtime.getText().toString().trim());
				editor.commit();
				Intent intent=new Intent(QueryBillActivity.this, DownloadBillActivity.class);
				startActivity(intent);
			}
		});
	}
}
