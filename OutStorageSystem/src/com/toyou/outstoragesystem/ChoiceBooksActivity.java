package com.toyou.outstoragesystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.toyou.outstoragesystem.DataBaseUtil;

public class ChoiceBooksActivity extends Activity {
	private Context context;
	private ListView listView;
	private ArrayList<String> arrayList=new ArrayList<String>();
	private SharedPreferences sharedPreferences;
	private List<Map<String, Object>> list_map=new ArrayList<Map<String,Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choicebooks);
		context=this;
		listView=(ListView) findViewById(R.id.list_books);
		sharedPreferences=getSharedPreferences("BasicData",MODE_PRIVATE);
		SimpleAdapter simpleAdapter=new SimpleAdapter(this, initData(),R.layout.layout_choicebooks_list, new String[]{"number","books","name"},new int[]{R.id.tv_number,R.id.tv_books,R.id.tv_name});
		listView.setAdapter(simpleAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {				
				Intent data=new Intent();
				Log.d("info", "返回 账套"+(String)list_map.get(position).get("books"));
				Log.d("info", "返回 名称"+(String)list_map.get(position).get("name"));
				arrayList.add(list_map.get(position).get("books").toString());
				arrayList.add(list_map.get(position).get("name").toString());
				data.putStringArrayListExtra("data", arrayList);
				setResult(200, data);
				finish();
			}
		});
	}
	private List<Map<String,Object>> initData(){
		String address=sharedPreferences.getString("address", "");
		String port=sharedPreferences.getString("port", "");
		String dbusername=sharedPreferences.getString("dbusername", "");
		String dbpwd=sharedPreferences.getString("dbpwd", "");
		Log.d("info", "address="+address+",port="+port+",dbusername="+dbusername+",dbpwd="+dbpwd);
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		list=getList();
		list_map=getList();
		return list;
	}
	private List<Map<String,Object>> getList(){
		final List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
				try
				{
					String address=sharedPreferences.getString("address", "");
					String port=sharedPreferences.getString("port", "");
					String dbusername=sharedPreferences.getString("dbusername", "");
					String dbpwd=sharedPreferences.getString("dbpwd", "");
					Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, "UFTSystem");
					String sql = "select * from EAP_Account";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						Log.d("info","名称:"+rs.getString("cAcc_Name"));
						map.put("number", rs.getInt("cAcc_Num"));
						map.put("books",rs.getString("DsName"));
						map.put("name", rs.getString("cAcc_Name"));
						list.add(map);
					}
					rs.close();
					stmt.close();
					conn.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		};
		Thread thread=new Thread(run);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return list;
	}
}
