package com.toyou.outstoragesystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class ProductPutListActivity extends Activity {
	private ListView listView;
	private Button button;
	private ImageView imageView;
	private SharedPreferences sharedPreferences;
	private SharedPreferences myPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_productputlist);
		sharedPreferences=getSharedPreferences("BasicData", MODE_PRIVATE);
		myPreferences=getSharedPreferences("mydatabase", MODE_PRIVATE);
		button=(Button) findViewById(R.id.btn_upload);
		imageView=(ImageView) findViewById(R.id.imageViewLeft);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(ProductPutListActivity.this, ProductWareHousingActivity.class);
				startActivity(intent);
			}
		});
		listView=(ListView) findViewById(R.id.list_product);
		SimpleAdapter simpleAdapter=new SimpleAdapter(ProductPutListActivity.this, initData(), R.layout.layout_uploaddata_list, new String[]{"Number","state","date","person","department","type"},new int[]{R.id.tv_productnumber,R.id.tv_state,R.id.tv_date,R.id.tv_person,R.id.tv_department,R.id.tv_type});
		listView.setAdapter(simpleAdapter);
		setListViewHeightBasedOnChildren(listView); 
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
				String sql_complte="select * from UploadCompleteData where state='未上传'";
				Cursor cursor_complte=db.rawQuery(sql_complte, null);
				if (cursor_complte.getCount()>0) {
					db.execSQL("update UploadCompleteData set state='已上传' where state='未上传'");
					String sql="select * from SaveData GROUP BY Number";
					int total=0;
					Cursor cursor=db.rawQuery(sql, null);
					if (cursor.getCount()>0) {
						while (cursor.moveToNext()) {
							String NumberCode=cursor.getString(cursor.getColumnIndex("Number"));
							String make=cursor.getString(cursor.getColumnIndex("author"));
							String busitype=cursor.getString(cursor.getColumnIndex("buisesType"));
							String person=cursor.getString(cursor.getColumnIndex("Person"));
							String type=cursor.getString(cursor.getColumnIndex("wareType"));
							String department=cursor.getString(cursor.getColumnIndex("Department"));
							if (busitype.equals("自制加工")) {
								busitype="3";
							}else if (busitype.equals("自制退货")) {
								busitype="4";
							} 
							String iddepartment=getDepartment(cursor.getString(cursor.getColumnIndex("Department")));
							String idclerk=getClerk(cursor.getString(cursor.getColumnIndex("Person")));
							String idrdstyle="21";
							String warehouse=getWarehouse(cursor.getString(cursor.getColumnIndex("ware")));
							//String makeid=getClerk(make);
							String makeid=idclerk;
							String sql_Product="select * from SaveData where Number='"+NumberCode+"' GROUP BY Code";
							Cursor cursor_product=db.rawQuery(sql_Product, null);
							if (cursor_product!=null) {
								String code="";
								String str="";
								code=getNumber();
								if (code=="") {
									str="000000";
								}else {
									str=code;
								}
								str = str.equals(null) ? "00000" : str.substring(str.length() - 4);
								int i=Integer.parseInt(str);
								i++;
								SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
								String datetime = df.format(new Date());
								code="MC-" + datetime + "-" + new DecimalFormat("0000").format(i);
								int flag=InserSQL(code,make,busitype,iddepartment,idclerk,idrdstyle,warehouse,makeid);
								int idRDRecordDTO=0;
								List<Map<String,Object>> list_id=getid();
								for (int j = 0; j < list_id.size(); j++) {
									Map<String,Object> map=new HashMap<String, Object>();
									map=list_id.get(j);
									idRDRecordDTO=Integer.parseInt( map.get("id").toString());
								}
								int flags=0;
								int flag_Stock=0;
								if (flag>0) {
									while(cursor_product.moveToNext()){
										int quantity=getProductQuantity(cursor_product.getString(cursor_product.getColumnIndex("Code")),NumberCode,busitype);
										if (busitype=="4") {
											quantity=-quantity;
											quantity=Integer.parseInt(Integer.toString(quantity));
										}
										String barcode=cursor_product.getString(cursor_product.getColumnIndex("Code"));
									    String idinventory=getIdinventory(cursor_product.getString(cursor_product.getColumnIndex("Code")));
									    String idunit=getIdunit(cursor_product.getString(cursor_product.getColumnIndex("Code")));
									    String  ware=getWarehouse(cursor_product.getString(cursor_product.getColumnIndex("ware")));
										flags+=InsertRDRecord_b(Integer.toString(quantity),make,barcode,busitype,idinventory,idunit,ware,Integer.toString(idRDRecordDTO));
										flag_Stock+=InsertST_CurrentStock(Integer.toString(quantity), make, idinventory, idunit, ware);
									}
									total=flags;
								}
								cursor_product.close();
							}
						}
						if (total>0) {
							db.execSQL("delete from UploadData");
							Toast.makeText(ProductPutListActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
							onCreate(null);
						}
					}
				}else {
					String sql="select * from UploadData GROUP BY Number";
					int total=0;
					Cursor cursor=db.rawQuery(sql, null);
					if (cursor.getCount()>0) {
						while (cursor.moveToNext()) {
							String NumberCode=cursor.getString(cursor.getColumnIndex("Number"));
							String make=cursor.getString(cursor.getColumnIndex("author"));
							String busitype=cursor.getString(cursor.getColumnIndex("buisesType"));
							String person=cursor.getString(cursor.getColumnIndex("Person"));
							String type=cursor.getString(cursor.getColumnIndex("wareType"));
							String department=cursor.getString(cursor.getColumnIndex("Department"));
							if (busitype.equals("自制加工")) {
								busitype="3";
							}else if (busitype.equals("自制退货")) {
								busitype="4";
							} 
							String iddepartment=getDepartment(cursor.getString(cursor.getColumnIndex("Department")));
							String idclerk=getClerk(cursor.getString(cursor.getColumnIndex("Person")));
							String idrdstyle="21";
							String warehouse=getWarehouse(cursor.getString(cursor.getColumnIndex("ware")));
							//String makeid=getClerk(make);
							String makeid=idclerk;
							String sql_Product="select * from UploadData where Number='"+NumberCode+"'";
							Cursor cursor_product=db.rawQuery(sql_Product, null);
							if (cursor_product!=null) {
								String code="";
								String str="";
								code=getNumber();
								if (code=="") {
									str="000000";
								}else {
									str=code;
								}
								str = str.equals(null) ? "00000" : str.substring(str.length() - 4);
								int i=Integer.parseInt(str);
								i++;
								SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
								String datetime = df.format(new Date());
								code="MC-" + datetime + "-" + new DecimalFormat("0000").format(i);
								int flag=InserSQL(code,make,busitype,iddepartment,idclerk,idrdstyle,warehouse,makeid);
								int idRDRecordDTO=0;
								List<Map<String,Object>> list_id=getid();
								for (int j = 0; j < list_id.size(); j++) {
									Map<String,Object> map=new HashMap<String, Object>();
									map=list_id.get(j);
									idRDRecordDTO=Integer.parseInt( map.get("id").toString());
								}
								int flags=0;
								int flag_Stock=0;
								if (flag>0) {
									while(cursor_product.moveToNext()){
										int quantity=getProductQuantity(cursor_product.getString(cursor_product.getColumnIndex("Code")),NumberCode,busitype);
										if (busitype=="4") {
											quantity=-quantity;
											quantity=Integer.parseInt(Integer.toString(quantity));
										}
										String barcode=cursor_product.getString(cursor_product.getColumnIndex("Code"));
									    String idinventory=getIdinventory(cursor_product.getString(cursor_product.getColumnIndex("Code")));
									    String idunit=getIdunit(cursor_product.getString(cursor_product.getColumnIndex("Code")));
									    String  ware=getWarehouse(cursor_product.getString(cursor_product.getColumnIndex("ware")));
										flags+=InsertRDRecord_b(Integer.toString(quantity),make,barcode,busitype,idinventory,idunit,ware,Integer.toString(idRDRecordDTO));
										flag_Stock+=InsertST_CurrentStock(Integer.toString(quantity), make, idinventory, idunit, ware);
									}
									total=flags;
								}
								cursor_product.close();
							}
							SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd");
							String datetimes = dfs.format(new Date());
							db.execSQL("create table if not exists UploadCompleteData(_id integer primary key autoincrement,Number text not null,date text,person text,department text,type text,state text)");
							db.execSQL("insert into UploadCompleteData(Number,date,person,department,type,state) values('"+NumberCode+"','"+datetimes+"','"+person+"','"+department+"','"+type+"','已上传')");
						}
						if (total>0) {
							db.execSQL("delete from UploadData");
							Toast.makeText(ProductPutListActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
							onCreate(null);
						}
					}else {
						Toast.makeText(ProductPutListActivity.this, "当前没有要上传的数据",Toast.LENGTH_SHORT).show();
						onCreate(null);
					}
				}
				
			}
		});
	}
	private List<Map<String,Object>> initData(){
		List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();		
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		String sql="select * from UploadCompleteData";
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				Map<String, Object> map=new HashMap<String, Object>();
				map.put("Number", cursor.getString(cursor.getColumnIndex("Number")));
				map.put("date", cursor.getString(cursor.getColumnIndex("date")));
				map.put("person", cursor.getString(cursor.getColumnIndex("person")));
				map.put("department", cursor.getString(cursor.getColumnIndex("department")));
				map.put("type", cursor.getString(cursor.getColumnIndex("type")));
				map.put("state", cursor.getString(cursor.getColumnIndex("state")));
				list.add(map);
			}
			cursor.close();
		}
		db.close();
		return list;
	}
	public String getNumber(){
		String number="";
		final List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
		    	String address=sharedPreferences.getString("address", "");
				String port=sharedPreferences.getString("port", "");
				String dbusername=sharedPreferences.getString("dbusername", "");
				String dbpwd=sharedPreferences.getString("dbpwd", "");
				String dbname=myPreferences.getString("databasename", "");
				try
				{
					
					Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
					String datetime = df.format(new Date());
					String sql = "select top 1 code from ST_RDRecord where code like '%MC-" + datetime+ "%' order by code desc ";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String, Object> map=new HashMap<String, Object>();
						map.put("code",rs.getString("code"));
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
		Thread thread= new Thread(run);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i <list.size(); i++) {
			Map<String, Object> map=new HashMap<String, Object>();
			map=list.get(i);
			number=map.get("code").toString();
		}
		return number;
	}
	
	public int InserSQL(String code,String make,String busitype,String department,String idclerk,String idrdstyle,String warehouse,String makeid){
		final String codes=code;
		final String makes=make;
		final String busitypes=busitype;
		final String departments=department;
		final String idclerks=idclerk;
		final String idrdstyles=idrdstyle;
		final String warehouses=warehouse;
		final String makeids=makeid;
		int count=0;
		final List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
		    	String address=sharedPreferences.getString("address", "");
				String port=sharedPreferences.getString("port", "");
				String dbusername=sharedPreferences.getString("dbusername", "");
				String dbpwd=sharedPreferences.getString("dbpwd", "");
				String dbname=myPreferences.getString("databasename", "");
				try
				{
					
					Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					Calendar calendar=Calendar.getInstance();
					int month=calendar.get(Calendar.MONTH)+1;
					int year=calendar.get(Calendar.YEAR);
 					String datetime = df.format(new Date());
					String sql = "insert into ST_RDRecord(code,rdDirectionFlag,maker,accountingperiod,accountingyear,VoucherYear,VoucherPeriod,idbusitype,iddepartment,IdMarketingOrgan,idclerk,idrdstyle,idwarehouse,voucherState,makerid,idvouchertype,voucherdate,madedate,createdtime,updated) values('"+codes+"',1,'"+makes+"',"+month+","+year+","+year+","+month+","+busitypes+","+departments+",1,"+idclerks+","+idrdstyles+","+warehouses+",181,"+makeids+",15,'"+datetime+"','"+datetime+"','"+datetime+"','"+datetime+"')";					
					Statement stmt = conn.createStatement();
					int counts =stmt.executeUpdate(sql);
					android.util.Log.d("inf0","counts="+counts);
					Map< String, Object> map=new HashMap<String, Object>();
					map.put("counts", counts);
					list.add(map);
					stmt.close();
					conn.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		};
		Thread thread= new Thread(run);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map=new HashMap<String, Object>();
			map=list.get(i);
			count=Integer.parseInt(map.get("counts").toString());
		}
		return count;
	}
	public int InsertRDRecord_b(String quantity,String updatedBy,String InvBarCode,String idbusiTypeByMergedFlow,String idinventory,String idbaseunit,String idwarehouse,String idRDRecordDTO){
		final String quantitys=quantity;
		final String updatedBys=updatedBy;
		final String InvBarCodes=InvBarCode;
		final String idbusiTypeByMergedFlows=idbusiTypeByMergedFlow;
		final String idinventorys=idinventory;
		final String idbaseunits=idbaseunit;
		final String idwarehouses=idwarehouse;
		final String idRDRecordDTOs=idRDRecordDTO;
		int count=0;
		final List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
		    	String address=sharedPreferences.getString("address", "");
				String port=sharedPreferences.getString("port", "");
				String dbusername=sharedPreferences.getString("dbusername", "");
				String dbpwd=sharedPreferences.getString("dbpwd", "");
				String dbname=myPreferences.getString("databasename", "");
				try
				{
					
					Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
 					String datetime = df.format(new Date());
					String sql = "insert into ST_RDRecord_b(code,quantity,baseQuantity,price,basePrice,amount,updatedBy,InvBarCode,IsPresent,idbusiTypeByMergedFlow,idinventory,idbaseunit,idunit,idwarehouse,idRDRecordDTO,createdtime,updated) values('0000',"+quantitys+","+quantitys+",0,0,0,'"+updatedBys+"','"+InvBarCodes+"',0,"+idbusiTypeByMergedFlows+","+idinventorys+","+idbaseunits+","+idbaseunits+","+idwarehouses+","+idRDRecordDTOs+",'"+datetime+"','"+datetime+"')";					
					Statement stmt = conn.createStatement();
					int counts =stmt.executeUpdate(sql);
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("counts", counts);
					list.add(map);
					stmt.close();
					conn.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		};
		Thread thread= new Thread(run);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map=new HashMap<String, Object>();
			map=list.get(i);
			count=Integer.parseInt(map.get("counts").toString());;
		}
		return count;
	}
	public int InsertST_CurrentStock(String productForReceiveBaseQuantity,String updatedBy,String idinventory,String idbaseunit,String idwarehouse){
		final String productForReceiveBaseQuantitys=productForReceiveBaseQuantity;
		final String updatedBys=updatedBy;
		final String idinventorys=idinventory;
		final String idbaseunits=idbaseunit;
		final String idwarehouses=idwarehouse;
		int count=0;
		final List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
		    	String address=sharedPreferences.getString("address", "");
				String port=sharedPreferences.getString("port", "");
				String dbusername=sharedPreferences.getString("dbusername", "");
				String dbpwd=sharedPreferences.getString("dbpwd", "");
				String dbname=myPreferences.getString("databasename", "");
				try
				{
					
					Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
 					String datetime = df.format(new Date());
					String sql = "insert into ST_CurrentStock(productForReceiveBaseQuantity,isCarriedForwardOut,isCarriedForwardIn,updatedBy,idinventory,IdMarketingOrgan,idbaseunit,idwarehouse,createdtime,updated) values("+productForReceiveBaseQuantitys+",0,0,'"+updatedBys+"',"+idinventorys+",1,"+idbaseunits+","+idwarehouses+",'"+datetime+"','"+datetime+"')";					
					Statement stmt = conn.createStatement();
					int counts =stmt.executeUpdate(sql);
					Map<String, Object> map=new HashMap<String, Object>();
					map.put("counts", counts);
					list.add(map);
					stmt.close();
					conn.close();
				} catch (SQLException e)
				{
					e.printStackTrace();
				}
			}
		};
		Thread thread= new Thread(run);
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map=new HashMap<String, Object>();
			map=list.get(i);
			count=Integer.parseInt(map.get("counts").toString());;
		}
		return count;
	}
	
	
	public String getDepartment(String name){
		String id="";
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		String sql="select * from AA_Department where name='"+name+"'";
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				id=cursor.getString(cursor.getColumnIndex("PartnerId"));
			}
		}
		cursor.close();
		db.close();
		return id;
	}
	public String getClerk(String name){
		String id="";
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		String sql="select * from AA_Person where name='"+name+"'";
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				id=cursor.getString(cursor.getColumnIndex("PartnerId"));
			}
		}
		cursor.close();
		db.close();
		return id;
	}
	public String getWarehouse(String name){
		String id="";
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		String sql="select * from AA_Warehouse where name='"+name+"'";
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				id=cursor.getString(cursor.getColumnIndex("PartnerId"));
			}
			cursor.close();
		}
		db.close();
		return id;
	}
	public List<Map<String, Object>> getid(){
		final List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
		Runnable run = new Runnable()
		{			
			@Override
			public void run()
			{
		    	String address=sharedPreferences.getString("address", "");
				String port=sharedPreferences.getString("port", "");
				String dbusername=sharedPreferences.getString("dbusername", "");
				String dbpwd=sharedPreferences.getString("dbpwd", "");
				String dbname=myPreferences.getString("databasename", "");
				try
				{
					
					Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
					String sql = "select top 1 id from ST_RDRecord order by id desc";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("id", rs.getInt("id"));
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
	
	public int getProductQuantity(String code,String number,String buistype){
		int count=0;
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		String sql="SELECT count(*) as Total FROM ProductInfo where Code='"+code+"' and Number='"+number+"' and BuisType='"+buistype+"'";
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				count=Integer.parseInt(cursor.getString(cursor.getColumnIndex("Total")));
			}
		}
		cursor.close();
		db.close();
		return count;
	}
	
	public String getIdinventory(String code){
		String Idinventory="";
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		String sql="SELECT PartnerId FROM AA_Inventory where code='"+code+"'";
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				Idinventory=cursor.getString(cursor.getColumnIndex("PartnerId"));
			}
		}
		cursor.close();
		db.close();
		return Idinventory;
	}
	public String getIdunit(String code){
		String idunit="";
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		String sql="SELECT idunit FROM AA_Inventory where code='"+code+"'";
		Cursor cursor=db.rawQuery(sql, null);
		if (cursor!=null) {
			while (cursor.moveToNext()) {
				idunit=cursor.getString(cursor.getColumnIndex("idunit"));
			}
		}
		cursor.close();
		db.close();
		return idunit;
	}
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
