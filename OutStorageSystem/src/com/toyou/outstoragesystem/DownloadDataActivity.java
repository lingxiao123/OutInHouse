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
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class DownloadDataActivity extends Activity{
	private CheckBox allcheck;
	private CheckBox agencycheck;
	private CheckBox departmentcheck;
	private CheckBox warehousecheck;
	private CheckBox employeecheck;
	private CheckBox producttypecheck;
	private CheckBox productdatacheck;
	private CheckBox businesstypecheck;
	private CheckBox barcodecheck;
	private Button btnsave;
	private SharedPreferences sharedPreferences;
	private SharedPreferences myPreferences;
	private ProgressDialog progressDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloaddata);
		sharedPreferences=getSharedPreferences("BasicData", MODE_PRIVATE);
		myPreferences=getSharedPreferences("mydatabase", MODE_PRIVATE);
		allcheck=(CheckBox) findViewById(R.id.allcheck);
		agencycheck=(CheckBox) findViewById(R.id.agencycheck);
		departmentcheck=(CheckBox) findViewById(R.id.departmentcheck);
		warehousecheck=(CheckBox) findViewById(R.id.warehousecheck);
		employeecheck=(CheckBox) findViewById(R.id.employeecheck);
		producttypecheck =(CheckBox) findViewById(R.id.producttypecheck);
		productdatacheck=(CheckBox) findViewById(R.id.productdatacheck);
		businesstypecheck=(CheckBox) findViewById(R.id.businesstypecheck);
		barcodecheck=(CheckBox) findViewById(R.id.barcodecheck);
		btnsave=(Button) findViewById(R.id.btnsave);
		
		allcheck.setOnCheckedChangeListener(changeListener);
		agencycheck.setOnCheckedChangeListener(changeListener);
		departmentcheck.setOnCheckedChangeListener(changeListener);
		warehousecheck.setOnCheckedChangeListener(changeListener);
		employeecheck.setOnCheckedChangeListener(changeListener);
		producttypecheck.setOnCheckedChangeListener(changeListener);
		productdatacheck.setOnCheckedChangeListener(changeListener);
		businesstypecheck.setOnCheckedChangeListener(changeListener);
		barcodecheck.setOnCheckedChangeListener(changeListener);
		
		btnsave.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (allcheck.isChecked()) {
					//新建ProgressDialog对象
					progressDialog=new ProgressDialog(DownloadDataActivity.this);
					//设置显示风格
					progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					//最大进度
					progressDialog.show();
					final List<Map<String, Object>> list_Partner=getPartnerList();	
					progressDialog.setMax(list_Partner.size());
					SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
					
					List<Map<String, Object>> list_Inventory=getInventory();
					int j=0;
					db.execSQL("create table if not exists AA_Inventory(_id integer primary key autoincrement,PartnerId integer,name text,code text ,specification text,idinventoryclass text,idunit integer)");
					for (Map<String, Object> map : list_Inventory) {
						Log.d("info", "list_Inventory="+j);
			    		db.execSQL("insert into AA_Inventory(PartnerId,name,code,specification,idinventoryclass,idunit) values("+map.get("id")+",'"+map.get("name")+"','"+map.get("code")+"','"+map.get("specification")+"','"+map.get("idinventoryclass")+"',"+map.get("idunit")+")");
			    		j++;
					}
					List<Map<String, Object>> list_InventoryClass=getInventoryClass();
					db.execSQL("create table if not exists AA_InventoryClass(_id integer primary key autoincrement,PartnerId integer,name text,code text ,idparent text)");
					for (Map<String, Object> map : list_InventoryClass) {
			    		db.execSQL("insert into AA_InventoryClass(PartnerId,name,code,idparent) values("+map.get("id")+",'"+map.get("name")+"','"+map.get("code")+"','"+map.get("idparent")+"')");
					}
					List<Map<String, Object>> list_InventoryBarCode=getInventoryBarCode();
					db.execSQL("create table if not exists AA_InventoryBarCode(_id integer primary key autoincrement,PartnerId integer,name text,code text ,idinventoryDTO integer,barCode text)");
					for (Map<String, Object> map : list_InventoryBarCode) {
			    		db.execSQL("insert into AA_InventoryBarCode(PartnerId,name,code,idinventoryDTO,barCode) values("+map.get("id")+",'"+map.get("name")+"','"+map.get("code")+"',"+map.get("idinventoryDTO")+",'"+map.get("barCode")+"')");
					}
					List<Map<String, Object>> list_BusiType=getBusiType();
					db.execSQL("create table if not exists AA_BusiType(_id integer primary key autoincrement,PartnerId integer,name text,code text)");
					for (Map<String, Object> map : list_BusiType) {
			    		db.execSQL("insert into AA_BusiType(PartnerId,name,code) values("+map.get("id")+",'"+map.get("name")+"','"+map.get("code")+"')");
					}
					
			    	db.execSQL("create table if not exists AA_Partner(_id integer primary key autoincrement,PartnerId integer,name text,code text ,partnerAbbName text)");
			    	int i=1;
					for (Map<String, Object> map : list_Partner) {
			    		db.execSQL("insert into AA_Partner(PartnerId,name,code,partnerAbbName) values("+map.get("id")+",'"+map.get("name")+"','"+map.get("code")+"','"+map.get("partnerAbbName")+"')");
			    		progressDialog.setProgress(i);
			    		i++;
					}
					List<Map<String, Object>> list_PartnerClass=getParentClass();
					db.execSQL("create table if not exists AA_PartnerClass(_id integer primary key autoincrement,PartnerId integer,name text,code text ,idparent text)");
					for (Map<String, Object> map : list_PartnerClass) {
			    		db.execSQL("insert into AA_PartnerClass(PartnerId,name,code,idparent) values("+map.get("id")+",'"+map.get("name")+"','"+map.get("code")+"','"+map.get("idparent")+"')");
					}
					List<Map<String, Object>> list_Department=getDepartment();
					db.execSQL("create table if not exists AA_Department(_id integer primary key autoincrement,PartnerId integer,name text,code text ,idparent text)");
					for (Map<String, Object> map : list_Department) {
			    		db.execSQL("insert into AA_Department(PartnerId,name,code,idparent) values("+map.get("id")+",'"+map.get("name")+"','"+map.get("code")+"','"+map.get("idparent")+"')");
					}
					List<Map<String, Object>> list_Warehouse=getWarehouse();
					db.execSQL("create table if not exists AA_Warehouse(_id integer primary key autoincrement,PartnerId integer,name text,code text)");
					for (Map<String, Object> map : list_Warehouse) {
			    		db.execSQL("insert into AA_Warehouse(PartnerId,name,code) values("+map.get("id")+",'"+map.get("name")+"','"+map.get("code")+"')");
					}
					List<Map<String, Object>> list_Person=getPerson();
					db.execSQL("create table if not exists AA_Person(_id integer primary key autoincrement,PartnerId integer,name text,code text ,idparent text,iddepartment integer,mobilePhoneNo text)");
					for (Map<String, Object> map : list_Person) {
			    		db.execSQL("insert into AA_Person(PartnerId,name,code,iddepartment,mobilePhoneNo) values("+map.get("id")+",'"+map.get("name")+"','"+map.get("code")+"',"+map.get("iddepartment")+",'"+map.get("mobilePhoneNo")+"')");
					}	
					Log.d("info", "list_Partner.count="+list_Partner.size());
					Toast.makeText(DownloadDataActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
    public CompoundButton.OnCheckedChangeListener changeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()){
                case R.id.allcheck:
                    if (allcheck.isChecked()) {
                    	agencycheck.setChecked(true);
                    	departmentcheck.setChecked(true);
                    	warehousecheck.setChecked(true);
                    	employeecheck.setChecked(true);
                    	producttypecheck.setChecked(true);
                    	productdatacheck.setChecked(true);
                    	businesstypecheck.setChecked(true);
                    	barcodecheck.setChecked(true);
					}
                    break;
                case R.id.agencycheck:
                case R.id.departmentcheck:
                case R.id.warehousecheck:
                case R.id.employeecheck:
                case R.id.producttypecheck:
                case R.id.productdatacheck:
                case R.id.businesstypecheck:
                case R.id.barcodecheck:
                    String str=buttonView.getText().toString();
                    //checkboxall.setOnCheckedChangeListener(null);
                    if(agencycheck.isChecked()&&departmentcheck.isChecked()&&warehousecheck.isChecked()&&employeecheck.isChecked()&&producttypecheck.isChecked()&&productdatacheck.isChecked()&&businesstypecheck.isChecked()&&businesstypecheck.isChecked()&&barcodecheck.isChecked()){
                        //表示如果都选中时，把全选按钮也选中
                        allcheck.setChecked(true);
                    }else {
                        //否则就全选按钮去不选中，但是这样会触发checkboxall的监听，会把所有的都取消掉
                    	allcheck.setChecked(false);
                    }
            }
        }
    };
    /**
     * 往来单位
     * */
    public List<Map<String, Object>> getPartnerList(){
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
				Log.d("info","download="+address);
				try
				{
					
					Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
					String sql = "select * from AA_Partner";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("id", rs.getInt("id"));
						map.put("code",rs.getString("code"));
						map.put("name",rs.getString("name"));
						map.put("partnerAbbName", rs.getString("partnerAbbName"));
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
		Thread thread= new Thread(run,"");
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	    return list;
    }
   
    /**
     * 往来单位上下级
     * */
    public List<Map<String,Object>> getParentClass(){
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
					String sql = "select * from AA_PartnerClass";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("id", rs.getInt("id"));
						map.put("idparent",rs.getInt("idparent"));
						map.put("name",rs.getString("name"));
						map.put("code", rs.getString("code"));
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
		return list;
    }
    /**
     * 部门信息AA_Department
     * */
    public List<Map<String,Object>> getDepartment(){
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
					String sql = "select * from AA_Department";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("id", rs.getInt("id"));
						map.put("idparent",rs.getString("idparent"));
						map.put("name",rs.getString("name"));
						map.put("code", rs.getString("code"));
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
		return list;
    }
    /***
     * AA_Warehouse
     * */
    public List<Map<String,Object>> getWarehouse(){
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
					String sql = "select * from AA_Warehouse";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("id", rs.getInt("id"));
						map.put("name",rs.getString("name"));
						map.put("code", rs.getString("code"));
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
		return list;
    }
   /***
    * AA_Person
    * */
    public List<Map<String,Object>> getPerson(){
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
					String sql = "select * from AA_Person";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("id", rs.getInt("id"));
						map.put("name",rs.getString("name"));
						map.put("code", rs.getString("code"));
						map.put("iddepartment", rs.getString("iddepartment"));
						map.put("mobilePhoneNo", rs.getString("mobilePhoneNo"));
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
		return list;
    }
    
    /**
     * AA_Inventory
     * 
     * */
    public List<Map<String,Object>> getInventory(){
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
					String sql = "select * from AA_Inventory";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("id", rs.getInt("id"));
						map.put("name",rs.getString("name"));
						map.put("code", rs.getString("code"));
						map.put("specification", rs.getString("specification"));
						map.put("idinventoryclass", rs.getString("idinventoryclass"));
						map.put("idunit",rs.getString("idunit"));
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
    /**
     * 
     * AA_InventoryClass
     * */
    public List<Map<String,Object>> getInventoryClass(){
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
					String sql = "select * from AA_InventoryClass";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("id", rs.getInt("id"));
						map.put("name",rs.getString("name"));
						map.put("code", rs.getString("code"));
						map.put("idparent", rs.getString("idparent"));
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
    /**
     * AA_InventoryBarCode
     * */
    
    public List<Map<String,Object>> getInventoryBarCode(){
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
					String sql = "select * from AA_InventoryBarCode";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("id", rs.getInt("id"));
						map.put("name",rs.getString("name"));
						map.put("code", rs.getString("code"));
						map.put("idinventoryDTO", rs.getString("idinventoryDTO"));
						map.put("barCode", rs.getString("barCode"));
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
    /***
     * 
     * AA_BusiType
     * ***/
    public List<Map<String,Object>> getBusiType(){
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
					String sql = "select * from AA_BusiType";
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("id", rs.getInt("id"));
						map.put("name",rs.getString("name"));
						map.put("code", rs.getString("code"));
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
