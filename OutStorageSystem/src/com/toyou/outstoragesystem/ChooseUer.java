package com.toyou.outstoragesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class ChooseUer extends Activity{
	private ListView usersitem;
	private ArrayList<String> arrayList=new ArrayList<String>();
	private SharedPreferences sharedPreferences;
	private List<Map<String, Object>> list_map=new ArrayList<Map<String,Object>>();
	private SimpleAdapter simp_adapter;
	private TextView user2;
	private TextView pass2;	
	private List<Map<String, Object>>datalist;
	private ImageView returnUser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userlist);
		usersitem =  (ListView) findViewById(R.id.usersitem);
		sharedPreferences=getSharedPreferences("BasicData",MODE_PRIVATE);
		SimpleAdapter simpleAdapter=new SimpleAdapter(this, initData(),R.layout.activity_useritem, 
				new String[]{"user2","pass2"},
				new int[]{R.id.user2,R.id.pass2});
		usersitem.setAdapter(simpleAdapter);
		usersitem.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {				
				Intent data=new Intent();
				arrayList.add(list_map.get(position).get("user2").toString());
				arrayList.add(list_map.get(position).get("pass2").toString());
				data.putStringArrayListExtra("data", arrayList);
				setResult(200, data);
				finish();
				findViews();
			}

			private void findViews() {
				returnUser =(ImageView) findViewById(R.id.returnUser);
				returnUser.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View arg0) {
						Intent inte = new Intent(ChooseUer.this,UserActivity.class);
						startActivity(inte);
					}
				});
			}
		});
	}
	private List<Map<String, Object>> 	initData() {
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		String sql="select * from AA_Person";
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				Map<String, Object> map=new HashMap<String, Object>();
				map.put("user2", cursor.getString(cursor.getColumnIndex("name")));
				map.put("pass2", cursor.getString(cursor.getColumnIndex("pwd")));
				list.add(map);
			}
		}
		cursor.close();
		db.close();
		list_map=list;
		return list;
	}


}
