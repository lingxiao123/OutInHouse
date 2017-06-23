package com.toyou.outstoragesystem;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ServerSetActivity extends Activity {
	private EditText address;
	private EditText port;
	private EditText dbusername;
	private EditText dbpwd;
	private Button btnsave;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serverset);
		address=(EditText) findViewById(R.id.address);
		port=(EditText) findViewById(R.id.port);
		dbusername=(EditText) findViewById(R.id.dbusername);
		dbpwd=(EditText) findViewById(R.id.dbpwd);
		btnsave=(Button) findViewById(R.id.btnsave);
		sharedPreferences=getSharedPreferences("BasicData",MODE_PRIVATE);
		editor=sharedPreferences.edit();
		address.setText(sharedPreferences.getString("address", ""));
		port.setText(sharedPreferences.getString("port", ""));
		dbusername.setText(sharedPreferences.getString("dbusername", ""));
		dbpwd.setText(sharedPreferences.getString("dbpwd", ""));
		
		btnsave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putString("address", address.getText().toString().trim());
				editor.putString("port", port.getText().toString().trim());
				editor.putString("dbusername", dbusername.getText().toString().trim());
				editor.putString("dbpwd", dbpwd.getText().toString().trim());
				editor.commit();
				Toast.makeText(ServerSetActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
			}
		});
	}
}
