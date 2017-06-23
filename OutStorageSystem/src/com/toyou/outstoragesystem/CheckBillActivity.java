package com.toyou.outstoragesystem;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class CheckBillActivity extends Activity {
	private ListView listView;
	private SharedPreferences sharedPreferences;
	private SharedPreferences myPreferences;
	private SharedPreferences saledeliveryPreferences;
	Editor editor;
	private Button button;
	private List<Map<String,Object>> list_map=new ArrayList<Map<String,Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkbill);
		sharedPreferences=getSharedPreferences("BasicData", MODE_PRIVATE);
		myPreferences=getSharedPreferences("mydatabase", MODE_PRIVATE);
		saledeliveryPreferences=getSharedPreferences("saledeliverydatabase", MODE_PRIVATE);
		editor=saledeliveryPreferences.edit();
		listView=(ListView) findViewById(R.id.list_bill);
		button=(Button) findViewById(R.id.btn_bill_upload);
		SimpleAdapter simpleAdapter=new SimpleAdapter(this, initData(), R.layout.layout_checkbill_list, new String[]{"name","code","voucherdate","type"},new int[]{R.id.txt_units,R.id.txt_bill,R.id.txt_date,R.id.txt_type});
		listView.setAdapter(simpleAdapter);
		setListViewHeightBasedOnChildren(listView);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				editor.putString("name",list_map.get(position).get("name").toString());
				editor.putString("code",list_map.get(position).get("code").toString());
				editor.putString("voucherdate",list_map.get(position).get("voucherdate").toString());
				editor.putString("idsaledelivery",list_map.get(position).get("idsaledelivery").toString());
				editor.commit();
				Intent intent=new Intent(CheckBillActivity.this,PickPackActivity.class);
				startActivity(intent);
			}
		});
		
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(CheckBillActivity.this, QueryBillActivity.class);
				startActivity(intent);
			}
		});
	}
	private List<Map<String,Object>> initData(){
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();	
		String sql_SaleDelivery="select * from SaleDelivery where state=0";
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		Cursor cursor=db.rawQuery(sql_SaleDelivery, null);
		if(cursor.getCount()>0){
			while (cursor.moveToNext()) {
				Map<String, Object> map=new HashMap<String, Object>();
				map.put("name", getUnit(cursor.getString(cursor.getColumnIndex("idcustomer"))));
				map.put("code", cursor.getString(cursor.getColumnIndex("code")));
				map.put("voucherdate", cursor.getString(cursor.getColumnIndex("voucherdate")));
				map.put("type", "销货单转销售出库单");
				map.put("idsaledelivery", cursor.getString(cursor.getColumnIndex("idsaledelivery")));
				list.add(map);
			}
		}
		list_map=list;
		return list;
	}
	//#region 获取往来单位名称
	public String getUnit(String id){
		String name="";
		String sql="select * FROM AA_Partner where PartnerId="+id+"";
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor.getCount()>0) {
			while (cursor.moveToNext()) {
				name=cursor.getString(cursor.getColumnIndex("name"));
			}
		}
		return name;
	}
	//#endregion
    public void setListViewHeightBasedOnChildren(ListView listView) {   
        // 获取ListView对应的Adapter   
        ListAdapter listAdapter = listView.getAdapter();   
        if (listAdapter == null) {   
            return;   
        }   
   
        int totalHeight = 0;   
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {   
            // listAdapter.getCount()返回数据项的数目   
            View listItem = listAdapter.getView(i, null, listView);   
            // 计算子项View 的宽高   
            listItem.measure(0, 0);    
            // 统计所有子项的总高度   
            totalHeight += listItem.getMeasuredHeight();    
        }   
   
        ViewGroup.LayoutParams params = listView.getLayoutParams();   
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));   
        // listView.getDividerHeight()获取子项间分隔符占用的高度   
        // params.height最后得到整个ListView完整显示需要的高度   
        listView.setLayoutParams(params);   
    }   
}
