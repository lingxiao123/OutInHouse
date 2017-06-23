package com.toyou.outstoragesystem;

import java.util.ArrayList;

import android.R.array;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends Activity{
private Button chooseUser;
private Button updateUser;
private TextView yonghuming;
private EditText yonghumima;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);
		findViews();
	}
	private void findViews() {
		chooseUser = (Button) findViewById(R.id.chooseUser);
		yonghuming= (TextView) findViewById(R.id.yonghuming);
		yonghumima= (EditText) findViewById(R.id.yonghumima);
		updateUser = (Button) findViewById(R.id.updateUser);
		chooseUser.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent inte = new Intent(UserActivity.this, ChooseUer.class);
				startActivityForResult(inte, 100);
			}
		});
		updateUser.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
				String newpwd = "" ;
				String name = "";
				newpwd=yonghumima.getText().toString();  
				name = yonghuming.getText().toString();
				String sql_user2="update AA_Person set pwd='"+newpwd+"'where name='"+name+"'";
				db.execSQL(sql_user2);
				Toast.makeText(UserActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==100&&resultCode==200) {
			ArrayList<String> arrayList=data.getStringArrayListExtra("data");
			yonghuming.setText(arrayList.get(0));
			yonghumima.setText(arrayList.get(1));			
		}
	}
	
}
