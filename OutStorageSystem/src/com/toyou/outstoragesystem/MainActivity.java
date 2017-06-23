package com.toyou.outstoragesystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Spinner spinner;
	private List<String> data_list;
	private List<String> lists;
	private ArrayAdapter<String> adapter;
	private CheckBox checkBox;
	private EditText pwdEditText;
	private Button btnLogin;
	private Context context;
	private ImageView imageView;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
        btnLogin=(Button) findViewById(R.id.btnLogin);
        pwdEditText=(EditText) findViewById(R.id.pwd);
		spinner=(Spinner) findViewById(R.id.sp_username);
		imageView=(ImageView) findViewById(R.id.imgset);
		context=this;
		data_list=list_person();
        adapter=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,data_list);
        spinner.setAdapter(adapter);
        this.spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
			String username=spinner.getSelectedItem().toString();
				String sql="select * from AA_Person where name='"+username+"'";
				SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
				Cursor cursor=db.rawQuery(sql, null);
				if (cursor.getCount()>0) {
					while (cursor.moveToNext()) {
						pwdEditText.setText(cursor.getString(cursor.getColumnIndex("pwd")));
					}
				}
				cursor.close();
				db.close();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
        btnLogin.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				String username=(String) spinner.getSelectedItem();
				String pwd=pwdEditText.getText().toString().trim();
				SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
				String sql="select * from AA_Person where name='"+username+"' and pwd='"+pwd+"'";
				Cursor cursor=db.rawQuery(sql, null);
				if (cursor.getCount()>0) {
					//判断用户名和密码是否正确
					Toast.makeText(context, "登录成功",Toast.LENGTH_SHORT ).show();
					sharedPreferences=getSharedPreferences("loginUser", MODE_PRIVATE);
					editor=sharedPreferences.edit();
					editor.putString("loginUserName", username);
					editor.commit();
					Intent intent=new Intent(context, IndexActivity.class);
					startActivity(intent);
				}else {
					Toast.makeText(context, "用户名或密码错误",Toast.LENGTH_SHORT ).show();
					return;
				}
			}
		});
        imageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent=new Intent(context,SetupActivity.class);
				startActivity(intent);
			}
		});
	}
	public List<String> list_person(){
		List<String> list=new ArrayList<String>();
		String sql="select * from AA_Person";
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		Cursor cursor_person=db.rawQuery(sql, null);
		if (cursor_person.getCount()>0) {
			while (cursor_person.moveToNext()) {
				list.add(cursor_person.getString(cursor_person.getColumnIndex("name")));
			}
		}
		cursor_person.close();
		db.close();
		return list;
	}
}
