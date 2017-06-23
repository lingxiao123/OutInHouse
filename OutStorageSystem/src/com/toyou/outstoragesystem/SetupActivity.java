package com.toyou.outstoragesystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class SetupActivity extends Activity {
	private Context context;
	private ListView listView;
	private ImageView imageView;
	private static final int IMAGES[] =new int[]{R.drawable.set01,R.drawable.configure,R.drawable.download,R.drawable.user};
	private static final String[] TITLE=new String[]{"服务器配置","账套选择","数据下载","用户设置"}; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		context=this;
		listView=(ListView) findViewById(R.id.listview);
		imageView=(ImageView) findViewById(R.id.img_return);
		SimpleAdapter simpleAdapter=new SimpleAdapter(this, initData(), R.layout.layout_list, new String[]{"icon","title"},new int[]{R.id.iv_icon,R.id.tv_title});
		listView.setAdapter(simpleAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					Intent intent=new Intent(context,ServerSetActivity.class);
					startActivity(intent);
					break;
				case 1:
					Intent intents=new Intent(context,SetBookActivity.class);
					startActivity(intents);
					break;
				case 2:
					Intent intentss=new Intent(context,DownloadDataActivity.class);
					startActivity(intentss);
					break;
				case 3:
					Intent ints=new Intent(context,UserActivity.class);
					startActivity(ints);
					break;
				default:
					break;
				}
			}
		});
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(context, MainActivity.class);
				startActivity(intent);
			}
		});
	}
	private List<Map<String,Object>> initData(){
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();		
		for (int i = 0; i < IMAGES.length; i++) {
			Map<String,Object> map=new HashMap<String, Object>();
			map.put("icon", IMAGES[i]);
			map.put("title",TITLE[i]);
			list.add(map);
		}
		return list;
	}
}
