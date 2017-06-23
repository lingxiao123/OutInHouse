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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class DownloadBillActivity extends Activity{
	private ListView billsdownitem;
	private SimpleAdapter simp_adapter;
	private ImageView imageView;
	private List<Map<String, Object>> datalist;
	private SharedPreferences sharedPreferences;
	private SharedPreferences myPreferences;
	private SharedPreferences datePreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_downloadbill);
		sharedPreferences=getSharedPreferences("BasicData", MODE_PRIVATE);
		myPreferences=getSharedPreferences("mydatabase", MODE_PRIVATE);
		datePreferences=getSharedPreferences("dateDataBase", MODE_PRIVATE);
		imageView=(ImageView) findViewById(R.id.imageViewLeft);
		// 返回主页
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(DownloadBillActivity.this, IndexActivity.class);
				startActivity(intent);
			}
		});

		final SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		billsdownitem =  (ListView) findViewById(R.id.billsdownitem);
		datalist = new ArrayList<Map<String,Object>>();
		simp_adapter= new SimpleAdapter(this, intaData(), R.layout.layout_downloadbill_list, 
				new String[]{"name","code","voucherdate","type"},
				new int[]{R.id.wldanwei2,R.id.yhdanju2,R.id.date2,R.id.style2}); 
		//3.视图加载适配器
		billsdownitem.setAdapter(simp_adapter);
		billsdownitem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int positions=position;
				new AlertDialog.Builder(DownloadBillActivity.this).setTitle("系统提示").setMessage("您确定要下载本条单据吗")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@SuppressWarnings("unused")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						final String code=datalist.get(positions).get("code").toString();
						final String idSaleDeliveryDTO=datalist.get(positions).get("idSaleDeliveryDTO").toString();
						String date=datalist.get(positions).get("voucherdate").toString();
						String idcustomer=datalist.get(positions).get("idcustomer").toString();
						//  判断是否有相同code的数据
						String sql_compare = "select *  from SaleDelivery where code "+ "='"+code+"'" ;
						Cursor cur=db.rawQuery(sql_compare, null);
						if(cur.getCount()>0){
							while (cur.moveToNext()) {
						//  删除相同code的明细
							String sql_del1 = "delete from SaleDelivery_b where idSaleDeliveryDTO in "+"(select idsaledelivery from SaleDelivery  where code  "+ "='"+code+"')";
						//  删除相同code的数据
							String sql_del2 = "delete from SaleDelivery  where code"+ "='"+code+"'" ;
							String sql_del3 = "delete from SaleDeliveryTemp where sourceVoucherCode"+ "='"+code+"'";
							db.execSQL(sql_del1);
							db.execSQL(sql_del2);
							db.execSQL(sql_del3);
						}								
					}
						
						String sql_SaleDelivery_insert="insert into SaleDelivery(code,voucherdate,idcustomer,idsaledelivery,state) values('"+code+"','"+date+"',"+idcustomer+","+idSaleDeliveryDTO+",0)";
						db.execSQL(sql_SaleDelivery_insert);
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
								String starttime=datePreferences.getString("starttime", "");
								String endtime=datePreferences.getString("endtime", "");
								try
								{
									Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
									String sql = "select * from SA_SaleDelivery_b where idSaleDeliveryDTO" +
											"="+idSaleDeliveryDTO+"";
									Statement stmt = conn.createStatement();
									ResultSet rs = stmt.executeQuery(sql);
									while (rs.next())
									{
										int quantity=rs.getInt("quantity");
										String updatedBy=rs.getString("updatedBy");
										int idinventory=rs.getInt("idinventory");
										int idbaseunit=rs.getInt("idbaseunit");
										int idSaleDeliveryDTO=rs.getInt("idSaleDeliveryDTO");
										String sql_SaleDelivery_b_insert="insert into SaleDelivery_b(quantity,updatedBy,idinventory,idbaseunit,idSaleDeliveryDTO) values("+quantity+",'"+updatedBy+"',"+idinventory+","+idbaseunit+","+idSaleDeliveryDTO+")";
										db.execSQL(sql_SaleDelivery_b_insert);
									}
									
									String sql_saledelivery_b_select="select * from SaleDelivery_b where idSaleDeliveryDTO="+idSaleDeliveryDTO+"";
									final SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
									Cursor cursor=db.rawQuery(sql_saledelivery_b_select, null);
									if (cursor.getCount()>0) {
										while (cursor.moveToNext()) {
											final String idinventory=cursor.getString(cursor.getColumnIndex("idinventory"));
											final int quantity=cursor.getInt(cursor.getColumnIndex("quantity"));
										
											Log.d("info", "idinventory="+idinventory);
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
														String sql = "select * from AA_BOM where idinventory="+idinventory+"";
														Statement stmt = conn.createStatement();
														ResultSet rs = stmt.executeQuery(sql);
														int m=0;
														 //套件条码
														 while (rs.next())
														 {
															m++;
															final int bomid=rs.getInt("id");
															//#region 
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
																		Log.d("info", "aa");
																		Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
																		String sql = "select * from AA_BOMChild where idbom="+bomid+"";
																		Statement stmt = conn.createStatement();
																		ResultSet rs = stmt.executeQuery(sql);
																		while (rs.next())
																		{
																			final String idinventory=rs.getString("idinventory");
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
																						String sql = "select * from AA_InventoryBarCode where idinventoryDTO="+idinventory+"";
																						Statement stmt = conn.createStatement();
																						ResultSet rs = stmt.executeQuery(sql);
																						while (rs.next())
																						{
																							db.execSQL("create table if not exists SaleDeliveryTemp(_id integer primary key autoincrement,code text,sourceVoucherCode text,quantity integer)");
																							String temp_insert="insert into SaleDeliveryTemp(code,sourceVoucherCode,quantity) values('"+rs.getString("barCode")+"','"+code+"',"+quantity+")";
																							Log.d("info", "temp_insert="+temp_insert);
																							db.execSQL(temp_insert);
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
															//#endregion
														}
														rs.close();
														stmt.close();
														conn.close();
														if (m==0) {
															 //散件 条码
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
																			Log.d("info", "bb");
																			Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
																			String sql = "select * from AA_InventoryBarCode where idinventoryDTO="+idinventory+"";
																			Statement stmt = conn.createStatement();
																			ResultSet rs = stmt.executeQuery(sql);
																			while (rs.next())
																			{
																				db.execSQL("create table if not exists SaleDeliveryTemp(_id integer primary key autoincrement,code text,sourceVoucherCode text,quantity integer)");
																				String temp_insert="insert into SaleDeliveryTemp(code,sourceVoucherCode,quantity) values('"+rs.getString("barCode")+"','"+code+"',"+quantity+")";
																				Log.d("info", "temp_insert="+temp_insert);
																				db.execSQL(temp_insert);
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
														 }
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
										}
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
						Toast.makeText(DownloadBillActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
						onCreate(null);
					}
				} ).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
			}
		});
	}
	//#region 加载数据
	public List<Map<String, Object>> intaData(){
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
				String starttime=datePreferences.getString("starttime", "");
				String endtime=datePreferences.getString("endtime", "");
				SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
				String sql_SaleDelivery_select="select code from SaleDelivery where state=1";
				Cursor cursor=db.rawQuery(sql_SaleDelivery_select, null);
				String str="";
				if (cursor.getCount()>0) {
					while (cursor.moveToNext()) {
						str+="'"+cursor.getString(cursor.getColumnIndex("code"))+"',";
					}
				}
				try
				{
					String sql="";
					Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
					sql = "select * from SA_SaleDelivery where voucherdate>='"+starttime+"' and voucherdate<='"+endtime+"'";
					if (str!="") {
						str=str.substring(0, str.lastIndexOf(','));
						sql+=" and code not in("+str+")";
					}
					Log.d("info", "SA_SaleDelivery-sql="+sql);
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery(sql);
					while (rs.next())
					{
						Log.d("info", "aa");
						Map<String,Object> map=new HashMap<String, Object>();
						map.put("name",getUnit(rs.getString("idcustomer")));
						map.put("code",rs.getString("code"));
						map.put("voucherdate",rs.getString("voucherdate"));
						map.put("type", "销货单生成销售出库单");
						map.put("idSaleDeliveryDTO",rs.getInt("id"));
						map.put("idcustomer", rs.getString("idcustomer"));
						list.add(map);
						Log.d("info", "bb");
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
		datalist=list;
		Log.d("info", "datalist="+datalist.size());
		return list;
	}
	//#endregion
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
}
