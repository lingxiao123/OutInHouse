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
import java.util.Locale;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.device.ScanManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

@SuppressLint({ "UseSparseArrays", "SimpleDateFormat" })
public class PickPackActivity extends Activity implements OnClickListener,
OnPageChangeListener 
{
	// 顶部左右侧按钮
		private ImageView imageViewLeft, imageViewRight;
		// 中部菜单3个Linearlayout
		private LinearLayout ll_title;
		private LinearLayout ll_detail;
		private LinearLayout ll_content;
		private Spinner spinner_warehouse;
		private Spinner spinner_person;
		private List<String> data_list_person;
		private ArrayAdapter<String> personAdapter;
		private List<String> data_list_warehouse;
		private ArrayAdapter<String> warehouseAdapter;
		private SharedPreferences sharedPreferences;
		private SharedPreferences saledeliveryPreferences;
		private SharedPreferences myPreferences;
		private SharedPreferences loginsharedPreferences;
		private Editor editor;
		private EditText zdrText;
		private EditText numberText;
		private EditText billTypeText;
		private EditText sourceVoucherCodeText;
		private EditText customerText;
		private EditText deportmentText;
		private EditText outinhouseText;
		private Button upload;
		// 中部菜单3个文本
		private TextView tv_title;
		private TextView tv_detail;
		private TextView tv_content;
		private EditText date;
		// 中间内容区域
		private ViewPager viewPager;
		// ViewPager适配器ContentAdapter
		private ContentAdapter adapter;

		private List<View> views;

		private final static String SCAN_ACTION = "android.intent.ACTION_DECODE_DATA";//
		private EditText showScanResult;
		private int type;
		private int outPut;
		private Vibrator mVibrator;
		private ScanManager mScanManager;
		private SoundPool soundpool = null;
	    SoundPool sp;
	    HashMap<Integer, Integer> spMap;
		private int soundid;
		private String barcodeStr;
		private boolean isScaning = false;
		private View detail_02;
		private EditText bianmaText;
		private EditText txmText;
		private EditText total;
		private EditText productName;
		private ListView list_product_content;
		private EditText warehouseText;
		
		private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
			@SuppressWarnings("unused")
			@Override
			public void onReceive(Context context, Intent intent) {
				isScaning = false;
				soundpool.play(soundid, 1, 1, 0, 0, 1);
				showScanResult.setText("");
				mVibrator.vibrate(100);
				byte[] barcode = intent.getByteArrayExtra("barcode");
				int barocodelen = intent.getIntExtra("length", 0);
				byte temp = intent.getByteExtra("barcodeType", (byte) 0);
				barcodeStr = new String(barcode, 0, barocodelen);
				showScanResult.setText(barcodeStr);
				SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
				if (!barcodeStr.equals(null)&&!barcodeStr.equals("")) {
					String code=barcodeStr;
					code=code.substring(0, code.indexOf("D"));
					//判断当前扫描的条码是否包含在当前销货单中的产品条码中
					String sql_exits="select * from SaleDeliveryTemp where sourceVoucherCode='"+sourceVoucherCodeText.getText()+"' and code='"+code+"'";
					Cursor cursor_exits=db.rawQuery(sql_exits, null);
					if (cursor_exits.getCount()>0) {
						//如果条码包含在销货单据中，下一步则判断产品的个数
						String sql_pruduct="select * from SaleDeliveryScanManager where barcode='"+barcodeStr+"'";
						Cursor productCursor=db.rawQuery(sql_pruduct,null);
						if (productCursor.getCount()>0) {
							playSound(5,0);
						}else {
							//判断扫描的数量是否小于等于销货单该产品的数量
							int scan_count=0;
							int temp_count=0;
							
							String sql_temp="select * from SaleDeliveryTemp where sourceVoucherCode='"+sourceVoucherCodeText.getText()+"' and code='"+code+"'";
							Cursor cursor_temp=db.rawQuery(sql_temp, null);
							if (cursor_temp.getCount()>0) {
								while (cursor_temp.moveToNext()) {
									temp_count=Integer.parseInt(cursor_temp.getString(cursor_temp.getColumnIndex("quantity")).toString());
								}
							}
							cursor_temp.close();
							String sql_scan="select count(*) as quantity from SaleDeliveryScanManager where sourcevouchercode='"+sourceVoucherCodeText.getText()+"' and code='"+code+"' GROUP BY code";
							Cursor cursor_scanCursor=db.rawQuery(sql_scan, null);
							if (cursor_scanCursor.getCount()>0) {
								while (cursor_scanCursor.moveToNext()) {
									scan_count=Integer.parseInt(cursor_scanCursor.getString(cursor_scanCursor.getColumnIndex("quantity")).toString());
								}
							}
							cursor_scanCursor.close();
							//如果扫描的数量小于单据的数量 则可以继续扫描 
							if (scan_count<temp_count) {
								String sql_scan_insert="insert into SaleDeliveryScanManager(code,barcode,sourcevouchercode,buistype) values('"+code+"','"+barcodeStr+"','"+sourceVoucherCodeText.getText()+"',17)";
								db.execSQL(sql_scan_insert);
								playSound(1, 0);
								loadData();
							}else{
								playSound(6,0);
							}
						}
						productCursor.close();
					}else {
						playSound(4,0);
					}
					cursor_exits.close();
				}
				db.close();
			}
		};
		/**
		 * 語音提示
		 * */
		public void InitSound() {
	        sp = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
	        spMap = new HashMap<Integer, Integer>();
	        spMap.put(1, sp.load(this, R.raw.ok, 1));
	        spMap.put(2, sp.load(this, R.raw.error, 1));
	        spMap.put(3, sp.load(this, R.raw.ng, 1));
	        spMap.put(4, sp.load(this, R.raw.noprudect, 1));
	        spMap.put(5, sp.load(this, R.raw.norepat, 1));
	        spMap.put(6, sp.load(this, R.raw.havascan, 1));
	    }
		/**
		 * 語音提示
		 * */
	    public void playSound(int sound, int number) {
	        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
	        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
	        float volumnRatio = volumnCurrent / audioMaxVolumn;
	        sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, number,  1f);
	    }

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_pickpack);
			InitSound();
			findViews();
			// 初始化控件
			initView();
			// 初始化底部按钮事件
			initEvent();

			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			setupView();
			sharedPreferences=getSharedPreferences("BasicData", MODE_PRIVATE);
		}

		private void initScan() {
			mScanManager = new ScanManager();
			mScanManager.openScanner();
			mScanManager.switchOutputMode(0);
			soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
			soundid = soundpool.load("/etc/Scan_new.ogg", 1);
		}

		private void setupView() {
			showScanResult = (EditText) detail_02.findViewById(R.id.scan_result);
		}

		@Override
		protected void onDestroy() {
			super.onDestroy();
		}

		@Override
		protected void onPause() {
			super.onPause();
			if (mScanManager != null) {
				mScanManager.stopDecode();
				isScaning = false;
			}
			unregisterReceiver(mScanReceiver);
		}

		@Override
		protected void onResume() {
			super.onResume();
			initScan();
			showScanResult.setText("");
			IntentFilter filter = new IntentFilter();
			filter.addAction(SCAN_ACTION);
			registerReceiver(mScanReceiver, filter);
		}

		@Override
		protected void onStart() {
			super.onStart();
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			return super.onKeyDown(keyCode, event);
		}

		private void initEvent() {
			// 设置按钮监听
			ll_title.setOnClickListener(this);
			ll_detail.setOnClickListener(this);
			ll_content.setOnClickListener(this);
			// 设置ViewPager滑动监听
			viewPager.setOnPageChangeListener(this);
		}

		@SuppressLint("SimpleDateFormat")
		private void initView() {
			// 中部菜单3个Linearlayout
			this.ll_title = (LinearLayout) findViewById(R.id.ll_title);
			this.ll_detail = (LinearLayout) findViewById(R.id.ll_detail);
			this.ll_content = (LinearLayout) findViewById(R.id.ll_content);

			// 中部菜单3个文本
			this.tv_title = (TextView) findViewById(R.id.tv_title);
			this.tv_detail = (TextView) findViewById(R.id.tv_detail);
			this.tv_content = (TextView) findViewById(R.id.tv_content);

			// 中间内容区域ViewPager
			this.viewPager = (ViewPager) findViewById(R.id.vp_content);

			// 适配器
			View title_01 = View.inflate(PickPackActivity.this,
					R.layout.activity_title, null);
			detail_02 = View.inflate(PickPackActivity.this,
					R.layout.activity_detail, null);
			View content_03 = View.inflate(PickPackActivity.this,
					R.layout.activity_content, null);
			views = new ArrayList<View>();
			views.add(title_01);
			views.add(detail_02);
			views.add(content_03);
			this.adapter = new ContentAdapter(views);
			viewPager.setAdapter(adapter);
			
			
			productName=(EditText) detail_02.findViewById(R.id.productName);
			bianmaText=(EditText) detail_02.findViewById(R.id.biamma);
			txmText=(EditText) detail_02.findViewById(R.id.tiaoxingma);
			total=(EditText) detail_02.findViewById(R.id.zongshu);

			//生单类型
			billTypeText=(EditText) title_01.findViewById(R.id.billtype);
			billTypeText.setText("销货单生成销售出库单");
			warehouseText=(EditText)title_01.findViewById(R.id.departmen);
			//单号
			String DocumentNumber = "";
			String Document = "PSWGRKD";
			String Number = "";
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String datetime = df.format(new Date());
			String str="";
			SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
			db.execSQL("create table if not exists DocumentNumber(_id integer primary key autoincrement,Number integer not null)");
			Cursor cursor= db.rawQuery("SELECT * from UploadCompleteData where Number like 'PCCPRKD-%"+datetime+"%' ORDER BY _id DESC LIMIT 1",null);
			if (cursor!=null) {
					while (cursor.moveToNext()) {
						str=cursor.getString(cursor.getColumnIndex("Number"));
					}
					cursor.close();
			}
			if (str=="") {
				str="0000";
			}
		    db.close();
		    str = str.equals(null) ? "00000" : str.substring(str.length() - 4);
			int num=Integer.parseInt(str);
			num++;
		    Number=new DecimalFormat("0000").format(num);
			DocumentNumber = Document + "-" + datetime + "-" + Number;
			numberText=(EditText)title_01.findViewById(R.id.number);
			numberText.setText(DocumentNumber);
			numberText.setCursorVisible(false);      
			numberText.setFocusable(false);         
			numberText.setFocusableInTouchMode(false);   
			
			//源单编码
			saledeliveryPreferences=getSharedPreferences("saledeliverydatabase", MODE_PRIVATE);
			sourceVoucherCodeText=(EditText) title_01.findViewById(R.id.sourceVoucherCode);
			sourceVoucherCodeText.setText(saledeliveryPreferences.getString("code", ""));
			sourceVoucherCodeText.setCursorVisible(false);      
			sourceVoucherCodeText.setFocusable(false);         
			sourceVoucherCodeText.setFocusableInTouchMode(false);    
			
			//日期
			date = (EditText) title_01.findViewById(R.id.dates);
			date.setInputType(InputType.TYPE_NULL);
			date.setText(datetime);
			date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						Calendar c = Calendar.getInstance(Locale.CHINA);
						new DatePickerDialog(PickPackActivity.this,
								new DatePickerDialog.OnDateSetListener() {
									@Override
									public void onDateSet(DatePicker view,
											int year, int monthOfYear,
											int dayOfMonth) {
										date.setText(year + "/" + (monthOfYear + 1)
												+ "/" + dayOfMonth);
									}
								}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
										.get(Calendar.DAY_OF_MONTH)).show();
					}
				}
			});
			date.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Calendar c = Calendar.getInstance(Locale.CHINA);
					new DatePickerDialog(PickPackActivity.this,
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view, int year,
										int monthOfYear, int dayOfMonth) {
									date.setText(year + "/" + (monthOfYear + 1)
											+ "/" + dayOfMonth);
								}
							}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c
									.get(Calendar.DAY_OF_MONTH)).show();
				}
			});
			
			//客户
			customerText=(EditText) title_01.findViewById(R.id.customer);
			customerText.setText(saledeliveryPreferences.getString("name", ""));
			customerText.setCursorVisible(false);      
			customerText.setFocusable(false);         
			customerText.setFocusableInTouchMode(false);
			
			//经手人     部门
			spinner_person=(Spinner) title_01.findViewById(R.id.sp_person);
			deportmentText=(EditText) title_01.findViewById(R.id.deportment);
			deportmentText.setCursorVisible(false);      
			deportmentText.setFocusable(false);         
			deportmentText.setFocusableInTouchMode(false);
			
			
			
			data_list_person=getPerson();
			personAdapter=new ArrayAdapter<String>(PickPackActivity.this, android.R.layout.simple_spinner_item, data_list_person);
			this.spinner_person.setAdapter(personAdapter);	
			this.spinner_person.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view,
						int position, long id) {
					SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
					String sql_iddepartment="select * from AA_Person where name='"+spinner_person.getSelectedItem()+"'";
					Cursor cursor=db.rawQuery(sql_iddepartment, null);
					String department="0";
					if (cursor!=null) {
						while (cursor.moveToNext()) {
							department=cursor.getString(cursor.getColumnIndex("iddepartment"));
						}
					}
					cursor.close();
					String sql_department="select * from AA_Department where PartnerId='"+department+"'";
					Cursor cursor_department=db.rawQuery(sql_department, null);
					if (cursor_department!=null) {
						while(cursor_department.moveToNext()){
							deportmentText.setText(cursor_department.getString(cursor_department.getColumnIndex("name")));
						}
					}
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent) {
				}
			});
			
			//制单人
			loginsharedPreferences = getSharedPreferences("loginUser", MODE_PRIVATE);
			this.zdrText = (EditText) title_01.findViewById(R.id.zdpeople);
			zdrText.setText(loginsharedPreferences.getString("loginUserName", ""));
			zdrText.setCursorVisible(false);      
			zdrText.setFocusable(false);         
			zdrText.setFocusableInTouchMode(false);
			
			//出库类别
			outinhouseText=(EditText) title_01.findViewById(R.id.outhousetype);
			outinhouseText.setText("销售出库");
			outinhouseText.setCursorVisible(false);      
			outinhouseText.setFocusable(false);         
			outinhouseText.setFocusableInTouchMode(false);
			
			data_list_warehouse=getWareHouse();
			spinner_warehouse=(Spinner) detail_02.findViewById(R.id.cangku);
			warehouseAdapter=new ArrayAdapter<String>(PickPackActivity.this, android.R.layout.simple_spinner_item, data_list_warehouse);
			this.spinner_warehouse.setAdapter(warehouseAdapter);	
			
			list_product_content=(ListView) content_03.findViewById(R.id.list_product_content);
			SimpleAdapter simpleAdapter=new SimpleAdapter(PickPackActivity.this, initData(),R.layout.layout_content_list, new String[]{"product","code","total","count"},new int[]{R.id.tv_product,R.id.tv_code,R.id.tv_total,R.id.tv_count});
			list_product_content.setAdapter(simpleAdapter);
			myPreferences=getSharedPreferences("mydatabase", MODE_PRIVATE);
			upload=(Button) title_01.findViewById(R.id.upload);
			upload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//判断是否扫描足够销货单中的组件
					final String sourceVoucherCode=sourceVoucherCodeText.getText().toString();
					int temp_count=0;
					int scan_count=0;
					SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
					String sql_temp="select * from SaleDeliveryTemp where sourceVoucherCode='"+sourceVoucherCode+"'";
					Cursor cursor_temp=db.rawQuery(sql_temp, null);
					if (cursor_temp.getCount()>0) {
						while (cursor_temp.moveToNext()) {	
							temp_count+=Integer.parseInt(cursor_temp.getString(cursor_temp.getColumnIndex("quantity")));
						}
					}
					cursor_temp.close();
					String sql_scan="SELECT count(*) totals from SaleDeliveryScanManager WHERE sourcevouchercode='"+sourceVoucherCode+"'";
					Cursor cursor_scan= db.rawQuery(sql_scan, null);
					if (cursor_scan.getCount()>0) {
						while (cursor_scan.moveToNext()) {
								scan_count=Integer.parseInt(cursor_scan.getString(cursor_scan.getColumnIndex("totals")).toString());
						}
					}
					int warehouseid=0;
					String sql_wearhouse_select="select * from AA_Warehouse where name='"+spinner_warehouse.getSelectedItem().toString()+"'";
				    Cursor cursor_wearhouse=db.rawQuery(sql_wearhouse_select, null);
					if (cursor_wearhouse.getCount()>0) {
						while (cursor_wearhouse.moveToNext()) {
							warehouseid=Integer.parseInt(cursor_wearhouse.getString(cursor_wearhouse.getColumnIndex("PartnerId")));
						}
					}
					final int idwarehouse=warehouseid;
					if (scan_count<temp_count) {
						Toast.makeText(PickPackActivity.this, "您还有组件未扫全!", Toast.LENGTH_SHORT).show();
						return;
					}else {
						final List<Map<String, Object>> list_map=new ArrayList<Map<String,Object>>();
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
									Log.d("info","address="+address+",port="+port+",dbusername="+dbusername+",dbpwd="+dbpwd+",dbname="+dbname);
									Connection conn =DataBaseUtil.getSQLConnection(address,port,dbusername, dbpwd, dbname);
									String sql = "select * from SA_SaleDelivery where code='"+sourceVoucherCode+"'";
									Statement stmt = conn.createStatement();
									ResultSet rs = stmt.executeQuery(sql);
									while (rs.next())
									{
										String code=getNumber();
										String str="";
										if (code=="") {
											str="000000";
										}else {
											str=code;
										}
										str = str.equals(null) ? "00000" : str.substring(str.length() - 4);
										int n=Integer.parseInt(str);
										n++;
										SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
										String datetime = df.format(new Date());
										code="IO-" + datetime + "-" + new DecimalFormat("0000").format(n);
										int	flag=InserSQL(code,rs.getString("code"), rs.getString("SourceVoucherCode"), rs.getString("maker"),rs.getString("updatedBy"), rs.getString("idbusinesstype"), rs.getString("iddepartment"), rs.getString("idcustomer"), rs.getString("idsettlecustomer"), rs.getString("idclerk"), rs.getString("idrdstyle"),Integer.toString(idwarehouse),  rs.getString("makerid"), rs.getString("ID"));
										Log.d("info","flag="+flag);
										final String codes=rs.getString("code");
										int idrdrecord=0;
										if(flag>0)
										{
											//获取刚插入的销售出库单id
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
														String sql = "select top 1 id from ST_RDRecord order by id desc ";
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
											Thread thread= new Thread(run);
											thread.start();
											try {
												thread.join();
											} catch (InterruptedException e) {
												e.printStackTrace();
											}
											int id=0;
											for (int i = 0; i < list.size(); i++) {
												Map<String, Object> map=new HashMap<String, Object>();
												map=list.get(i);
												id=Integer.parseInt(map.get("id").toString());
											}
											idrdrecord=id;
										}
										final int idSaleDeliveryDTO=rs.getInt("id");
										final int idrdrcords=idrdrecord;

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
													String sql = "select * from SA_SaleDelivery_b where idSaleDeliveryDTO="+idSaleDeliveryDTO+"";
													Log.d("info", "sql="+sql);
													Statement stmt = conn.createStatement();
													ResultSet rs = stmt.executeQuery(sql);
													while (rs.next())
													{
														Log.d("info", "bb");
														SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
									 					String datetime = df.format(new Date());
														int flags=InserRDRecord_b(rs.getString("quantity"), rs.getString("compositionQuantity"), rs.getString("baseQuantity"), codes, rs.getString("saleOrderCode"), rs.getString("updatedBy"),rs.getString("inventoryBarCode"),codes,Integer.toString(65),rs.getString("idinventory"),rs.getString("idbaseunit"),rs.getString("idunit"),Integer.toString(idwarehouse),Integer.toString(idSaleDeliveryDTO),rs.getString("id"), Integer.toString(idSaleDeliveryDTO), rs.getString("id"), rs.getString("sourceVoucherId"),rs.getString("sourceVoucherDetailId"), rs.getString("sourceVoucherDetailId"), Integer.toString(104), Integer.toString(104), Integer.toString(idrdrcords), datetime, datetime);
														Log.d("info", "flags="+flags);
														Map<String,Object> map=new HashMap<String, Object>();
														map.put("flags", flags);
														list_map.add(map);
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
						int total=0;
						for (int i = 0; i < list_map.size(); i++) {
							Map<String, Object> map=new HashMap<String, Object>();
							map=list_map.get(i);
							total=Integer.parseInt(map.get("flags").toString());
						}
						if (total>0) {
							Toast.makeText(PickPackActivity.this,"上传完成！", Toast.LENGTH_SHORT).show();
							String sql_saledelivery_update="update SaleDelivery set state=1 where code='"+sourceVoucherCodeText.getText()+"'";
							db.execSQL(sql_saledelivery_update);
							Intent intent=new Intent(PickPackActivity.this, CheckBillActivity.class);
							startActivity(intent);
							
						}
					}

				}
			});
			
		}

		private void findViews() {
			imageViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
			imageViewLeft.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					 Intent inte = new Intent(PickPackActivity.this, IndexActivity.class);			
					 startActivity(inte);
				}
			});
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			restartBotton();
			// 当前view被选择的时候,改变底部菜单图片，文字颜色
			switch (arg0) {
			case 0:
				tv_title.setTextColor(0xff1C86EE);
				break;
			case 1:
				tv_detail.setTextColor(0xff1C86EE);
				break;
			case 2:
				tv_content.setTextColor(0xff1C86EE);
				loadData();
				break;
			default:
				break;
			}
		}
		public void loadData(){
			SimpleAdapter simpleAdapter=new SimpleAdapter(PickPackActivity.this, initData(),R.layout.layout_content_list, new String[]{"product","code","count","total"},new int[]{R.id.tv_product,R.id.tv_code,R.id.tv_count,R.id.tv_total});
			list_product_content.setAdapter(simpleAdapter);
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			restartBotton();
			// 在每次点击后将所有的底部按钮(ImageView,TextView)颜色改为灰色，然后根据点击着色
			// ImageView和TetxView置为绿色，页面随之跳转
			switch (v.getId()) {
			case R.id.ll_title:
				tv_title.setTextColor(0xff1C86EE);
				viewPager.setCurrentItem(0);
				break;
			case R.id.ll_detail:
				tv_detail.setTextColor(0xff1C86EE);
				viewPager.setCurrentItem(1);
				break;
			case R.id.ll_content:
				tv_content.setTextColor(0xff1C86EE);
				viewPager.setCurrentItem(2);
				break;
			default:
				break;
			}
		}

		private void restartBotton() {
			// TextView置为白色		
			tv_title.setTextColor(0xff000000);
			tv_detail.setTextColor(0xff000000);
			tv_content.setTextColor(0xff000000);
		}
		public List<Map<String, Object>> initData(){
			List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
			String sql="SELECT * FROM SaleDeliveryTemp where sourceVoucherCode='"+sourceVoucherCodeText.getText()+"'";
			SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
			Cursor cursor=db.rawQuery(sql, null);
			Log.d("info", "cursor_count="+cursor.getCount());
			if (cursor!=null) {
				while(cursor.moveToNext()) {
					Map<String, Object> map=new HashMap<String, Object>();
					String code=cursor.getString(cursor.getColumnIndex("code"));
					String count=cursor.getString(cursor.getColumnIndex("quantity"));
					String sql_product="select * from AA_Inventory where code='"+code+"'";
					String productName="";
					Cursor proCursor=db.rawQuery(sql_product, null);
					if (proCursor!=null) {
						if(proCursor.moveToNext()) {
							productName=proCursor.getString(proCursor.getColumnIndex("name"));
						}
					}
					proCursor.close();
					map.put("product", productName);
					map.put("code", code);
					map.put("count", count);
					int total=0;
					String sql_total="select count(*) totals  from SaleDeliveryScanManager where code='"+code+"' and sourcevouchercode='"+sourceVoucherCodeText.getText()+"' GROUP BY code";
					Cursor cursor_total=db.rawQuery(sql_total, null);
					if (cursor_total.getCount()>0) {
						while (cursor_total.moveToNext()) {
							total=Integer.parseInt(cursor_total.getString(cursor_total.getColumnIndex("totals")).toString());
						}
					}
					map.put("total", total);
					list.add(map);
				}
			}
			cursor.close();
			db.close();
			Log.d("info","list_count="+list.size());
			return list;
		}
		
		public List<String> getWareHouse(){
			List<String> list=new ArrayList<String>();
			SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
			String sql="select * from AA_Warehouse";
			Cursor cursor=db.rawQuery(sql, null);
			if (cursor!=null) {
				while(cursor.moveToNext()){
					list.add(cursor.getString(cursor.getColumnIndex("name")));
				}
				
			}
			cursor.close();
			db.close();
			return list;
		}
		
		public List<String> getPerson(){
			List<String> list=new ArrayList<String>();
			SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
			String sql="select * from AA_Person";
			Cursor cursor=db.rawQuery(sql, null);
			if (cursor!=null) {
				while(cursor.moveToNext()){
					list.add(cursor.getString(cursor.getColumnIndex("name")));
				}
			}
			cursor.close();
			db.close();
			return list;
		}
		
		public int InserSQL(String code,String sourcevouchercode,String saleOrderCode,String make,String updateby,String busitype,String department,String idpartner,String idsettleCustomer ,String idclerk,String idrdstyle,String warehouse,String makeid,String sourceVoucherId){
			final String codes=code;
			final String sourcevouchercodes=sourcevouchercode;
			final String saleOrderCodes=saleOrderCode;
			final String makes=make;
			final String updatebys=updateby;
			final String busitypes=busitype;
			final String departments=department;
			final String idpartners=idpartner;
			final String idsettleCustomers=idsettleCustomer;
			final String idclerks=idclerk;
			final String idrdstyles=idrdstyle;
			final String warehouses=warehouse;
			final String makeids=makeid;
			final String sourceVoucherIds=sourceVoucherId;
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
						String sql = "insert into ST_RDRecord(code,sourceVoucherCode,saleOrderCode,rdDirectionFlag,isCostAccount,isMergedFlow,isAutoGenerate,maker,iscarriedforwardout,iscarriedforwardin,ismodifiedcode,accountingperiod,accountingyear,updatedBy,VoucherYear,VoucherPeriod,exchangeRate,idbusitype,idcurrency,iddepartment,IdMarketingOrgan,idpartner,idsettleCustomer,idclerk,idrdstyle,idwarehouse,deliveryState,voucherState,makerid,sourceVoucherId,idvouchertype,voucherdate,madedate,createdtime,updated,DataSource) values('"+codes+"','"+sourcevouchercodes+"','"+saleOrderCodes+"',0,0,0,0,'"+makes+"',0,0,0,"+month+","+year+",'"+updatebys+"',"+year+","+month+",1,"+busitypes+",4,"+departments+",1,"+idpartners+","+idsettleCustomers+","+idclerks+","+idrdstyles+","+warehouses+",420,181,"+makeids+","+sourceVoucherIds+",19,'"+datetime+"','"+datetime+"','"+datetime+"','"+datetime+"',1531)";					
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
		public int InserRDRecord_b(String quantity,String compositionQuantity,String baseQuantity,String sourceVoucherCode,String saleOrderCode,String updatedBy,String InvBarCode,String SourceVoucherCodeByMergedFlow ,String idbusiTypeByMergedFlow,String idinventory,String idbaseunit,String idunit,String idwarehouse,String sourceVoucherId,String sourceVoucherDetailId,String SourceVoucherIdByMergedFlow,String SourceVoucherDetailIdByMergedFlow,String saleOrderId,String saleOrderDetailId,String SourceOrderDetailId,String idsourcevouchertype,String idsourceVoucherTypeByMergedFlow,String idRDRecordDTO,String createdtime,String updated){
			final String quantitys=quantity;
			final String compositionQuantitys=compositionQuantity;
			final String baseQuantitys=baseQuantity;
			final String sourceVoucherCodes=sourceVoucherCode;
			final String saleOrderCodes=saleOrderCode;
			final String updatedBys=updatedBy;
			final String invBarCodes=InvBarCode;
			final String SourceVoucherCodeByMergedFlows=SourceVoucherCodeByMergedFlow;
			final String idbusiTypeByMergedFlows=idbusiTypeByMergedFlow;
			final String idinventorys=idinventory;
			final String idbaseunits=idbaseunit;
			final String idunits=idunit;
			final String idwarehouses=idwarehouse;
			final String sourceVoucherIds=sourceVoucherId;
			final String sourceVoucherDetailIds=sourceVoucherDetailId;
			final String sourceVoucherIdByMergedFlows=SourceVoucherIdByMergedFlow;
			final String SourceVoucherDetailIdByMergedFlows=SourceVoucherDetailIdByMergedFlow;
			final String saleOrderIds=saleOrderId;
			final String saleOrderDetailIds=saleOrderDetailId;
			final String SourceOrderDetailIds=SourceOrderDetailId;
			final String idsourcevouchertypes=idsourcevouchertype;
			final String idsourceVoucherTypeByMergedFlows=idsourceVoucherTypeByMergedFlow;
			final String idRDRecordDTOs=idRDRecordDTO;
			final String createdtimes=createdtime;
			final String updateds=updated;
			
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
						String sql = "insert into ST_RDRecord_b(code,quantity,compositionQuantity,baseQuantity,price,basePrice,amount," +
								"sourceVoucherCode,saleOrderCode,updatedBy,InvBarCode,SourceVoucherCodeByMergedFlow,IsPresent," +
								"idbusiTypeByMergedFlow,idinventory,idbaseunit,idunit,idwarehouse,sourceVoucherId," +
								"sourceVoucherDetailId,SourceVoucherIdByMergedFlow,SourceVoucherDetailIdByMergedFlow," +
								"saleOrderId,saleOrderDetailId,SourceOrderDetailId,idsourcevouchertype," +
								"idsourceVoucherTypeByMergedFlow,idRDRecordDTO,createdtime,updated,DataSource) " +
								"values('0001',"+quantitys+",'"+compositionQuantitys+"',"+baseQuantitys+",0,0,0,'"
								+sourceVoucherCodes+"','"+saleOrderCodes+"','"+updatedBys+"','"+invBarCodes+"','"+SourceVoucherCodeByMergedFlows+"',0,"
								+idbusiTypeByMergedFlows+","+idinventorys+","+idbaseunits+","+idunits+","+idwarehouses+","+sourceVoucherIds+","
								+sourceVoucherDetailIds+","+sourceVoucherIdByMergedFlows+","+SourceVoucherDetailIdByMergedFlows+","
								+saleOrderIds+","+saleOrderDetailIds+","+SourceOrderDetailIds+","+idsourcevouchertypes+","
								+idsourceVoucherTypeByMergedFlows+","+idRDRecordDTOs+",'"
								+createdtimes+"','"+updateds+"',1531)";	
						Log.d("info", "sql_ST_RDRecord_b"+sql);
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
						String sql = "select top 1 code from ST_RDRecord where code like '%IO-" + datetime+ "%' order by code desc ";
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
				String string="";
			}
			return number;
		}
}
