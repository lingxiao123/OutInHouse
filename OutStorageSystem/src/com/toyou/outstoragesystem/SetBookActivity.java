package com.toyou.outstoragesystem;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SetBookActivity extends Activity {
	private Button btncheck;
	private Button btnSave;
	private EditText name;
	private EditText books;
	private Context context;
	private ImageView imageView;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setbook);
		btncheck=(Button) findViewById(R.id.btncheck);
		btnSave=(Button) findViewById(R.id.btnsave);
		name=(EditText) findViewById(R.id.name);
		books=(EditText) findViewById(R.id.books);
		imageView=(ImageView) findViewById(R.id.img_return);
		context=this;
		
		btncheck.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,ChoiceBooksActivity.class);
				startActivityForResult(intent,100);
			}
		});
		
		btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sharedPreferences=getSharedPreferences("mydatabase", MODE_PRIVATE);
				editor=sharedPreferences.edit();
				editor.putString("databasename", books.getText().toString().trim());
				editor.putString("databases", name.getText().toString().trim());
				editor.commit();
				Log.d("info", "name="+name.getText().toString().trim());
				Toast.makeText(context, "保存成功", Toast.LENGTH_SHORT).show();
			}
		});
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context,SetupActivity.class);
				startActivity(intent);
			}
		});
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==100&&resultCode==200) {
			ArrayList<String> arrayList=data.getStringArrayListExtra("data");
			books.setText(arrayList.get(0));
			name.setText(arrayList.get(1));
		}
	}
}
