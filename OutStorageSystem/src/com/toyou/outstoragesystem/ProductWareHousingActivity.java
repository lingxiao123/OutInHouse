package com.toyou.outstoragesystem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
import android.widget.AdapterView.OnItemSelectedListener;
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

@SuppressLint("UseSparseArrays")
public class ProductWareHousingActivity extends Activity implements
		OnClickListener, OnPageChangeListener {
	// 顶部左右侧按钮
	private ImageView imageViewLeft, imageViewRight;
	// 中部菜单3个Linearlayout
	private LinearLayout ll_title;
	private LinearLayout ll_detail;
	private LinearLayout ll_content;
	private Spinner spinner;
	private Spinner spinner_warehouse;
	private Spinner spinner_warename;
	private Spinner spinner_person;
	private List<String> data_list;
	private List<String> data_list_warehouse;
	private List<String> data_list_warename;
	private List<String> data_list_person;
	private ArrayAdapter<String> arrayAdapter;
	private ArrayAdapter<String> arrayAdapter2;
	private ArrayAdapter<String> warenameAdapter;
	private ArrayAdapter<String> personAdapter;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private EditText zdrText;
	private EditText numberText;
	private EditText byhandText;
	// 中部菜单3个文本
	private TextView tv_title;
	private TextView tv_detail;
	private TextView tv_content;
	private EditText date;
	
	private Button btn_complte;
	
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
			String type=spinner.getSelectedItem().toString();
			if (type=="自制加工") {
				type="3";
			}
			if (type=="自制退货") {
				type="4";
			}
			SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
	        db.execSQL("create table if not exists ProductInfo(_id integer primary key autoincrement,Code text,BarCode text,Number text,BuisType text)");
	        db.execSQL("create table if not exists UploadData(_id integer primary key autoincrement,Code text,BarCode text,Number text,Department text,Person text,wareType text,buisesType text,ware text,author text)");
			if (!barcodeStr.equals(null)&&!barcodeStr.equals("")) {
				String sql_pruduct="select * from ProductInfo where BarCode='"+barcodeStr+"' and BuisType='"+type+"'";
				Cursor productCursor=db.rawQuery(sql_pruduct,null);
				Log.d("info", "productCursor is null="+productCursor.equals(null));
				if (productCursor!=null) {
					if (productCursor.moveToNext()) {
						String code=productCursor.getString(productCursor.getColumnIndex("BarCode"));
						code=code.substring(0, code.indexOf("D"));
						Log.d("info", "code="+code);
						String productInfo="select * from AA_Inventory where code='"+code+"'";
						Cursor productinfoCursor=db.rawQuery(productInfo, null);
						if (productinfoCursor!=null) {
							if (productinfoCursor.moveToNext()) {
								productName.setText(productinfoCursor.getString(productinfoCursor.getColumnIndex("name")));
								bianmaText.setText(barcodeStr);
								txmText.setText(productinfoCursor.getString(productinfoCursor.getColumnIndex("code")));
								String count="1";
								total.setText(count);
							}
						}
						productinfoCursor.close();
						playSound(5,0);
						
					}else {
						String code=barcodeStr.substring(0, barcodeStr.indexOf("D"));
						db.execSQL("insert into ProductInfo(Code,BarCode,Number,BuisType) values('"+code+"','"+barcodeStr+"','"+numberText.getText()+"','"+type+"')");
						playSound(1, 0);
						db.execSQL("insert into UploadData(Code,BarCode,Number,Department,Person,wareType,buisesType,ware,author) values('"+code+"','"+barcodeStr+"','"+numberText.getText()+"','"+warehouseText.getText()+"','"+spinner_person.getSelectedItem()+"','"+spinner_warehouse.getSelectedItem()+"','"+spinner.getSelectedItem()+"','"+spinner_warename.getSelectedItem()+"','"+zdrText.getText()+"')");
						loadData();
					}
					productCursor.close();
				}
			}
			db.close();
			/**Log.d("info", "barocodelen="+barocodelen);
			if(barocodelen>0){
				playSound(1, 0);
				
			}**/
		}
	};
	/**
	 * 語音提示
	 * */
	public void InitSound() {
        sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        spMap = new HashMap<Integer, Integer>();
        spMap.put(1, sp.load(this, R.raw.ok, 1));
        spMap.put(2, sp.load(this, R.raw.error, 1));
        spMap.put(3, sp.load(this, R.raw.ng, 1));
        spMap.put(4, sp.load(this, R.raw.noprudect, 1));
        spMap.put(5, sp.load(this, R.raw.norepat, 1));

    }
	/**
	 * 語音提示
	 * */
    public void playSound(int sound, int number) {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolumn = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float volumnRatio = volumnCurrent / audioMaxVolumn;
        //sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, number,  1f);
        sp.play(spMap.get(sound), 1, 1, 1, number,  1f);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warehous);
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
		View title_01 = View.inflate(ProductWareHousingActivity.this,
				R.layout.activity_warehouse_title, null);
		detail_02 = View.inflate(ProductWareHousingActivity.this,
				R.layout.activity_warehouse_detail, null);
		View content_03 = View.inflate(ProductWareHousingActivity.this,
				R.layout.activity_warehouse_content, null);
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
		date = (EditText) title_01.findViewById(R.id.date);
		date.setInputType(InputType.TYPE_NULL);
		date.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					Calendar c = Calendar.getInstance(Locale.CHINA);
					new DatePickerDialog(ProductWareHousingActivity.this,
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
		
		warehouseText=(EditText)title_01.findViewById(R.id.departmen);
		
		String DocumentNumber = "";
		String Document = "PCCPRKD";
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
	
		this.spinner = (Spinner) title_01.findViewById(R.id.sp_businesstype);
		this.spinner_warehouse = (Spinner) title_01
				.findViewById(R.id.sp_Warehoustype);
		this.spinner_warename=(Spinner) detail_02.findViewById(R.id.cangku);
		this.spinner_person=(Spinner) title_01.findViewById(R.id.sp_person);
		data_list = new ArrayList<String>();
		data_list.add("自制加工");
		data_list.add("自制退货");
		arrayAdapter = new ArrayAdapter<String>(
				ProductWareHousingActivity.this,
				android.R.layout.simple_spinner_item, data_list);
		this.spinner.setAdapter(arrayAdapter);

		data_list_warehouse = new ArrayList<String>();
		data_list_warehouse.add("自制产品入库");
		arrayAdapter2 = new ArrayAdapter<String>(
				ProductWareHousingActivity.this,
				android.R.layout.simple_spinner_item, data_list_warehouse);
		this.spinner_warehouse.setAdapter(arrayAdapter2);
		
		data_list_warename=getWareHouse();
		warenameAdapter=new ArrayAdapter<String>(ProductWareHousingActivity.this, android.R.layout.simple_spinner_item,data_list_warename);
		this.spinner_warename.setAdapter(warenameAdapter);
		this.spinner_warename.setSelection(15, true);
		
		data_list_person=getPerson();
		personAdapter=new ArrayAdapter<String>(ProductWareHousingActivity.this, android.R.layout.simple_spinner_item, data_list_person);
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
						warehouseText.setText(cursor_department.getString(cursor_department.getColumnIndex("name")));
					}
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		sharedPreferences = getSharedPreferences("loginUser", MODE_PRIVATE);
		this.zdrText = (EditText) title_01.findViewById(R.id.zdpeople);
		zdrText.setText(sharedPreferences.getString("loginUserName", ""));
		
		//byhandText=(EditText) title_01.findViewById(R.id.jspeople);
		//byhandText.setText(sharedPreferences.getString("loginUserName", ""));
		
		list_product_content=(ListView) content_03.findViewById(R.id.list_product_content);
		SimpleAdapter simpleAdapter=new SimpleAdapter(ProductWareHousingActivity.this, initData(),R.layout.layout_warehouse_detail_list, new String[]{"product","code","count"},new int[]{R.id.tv_product,R.id.tv_code,R.id.tv_count});
		list_product_content.setAdapter(simpleAdapter);

		date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance(Locale.CHINA);
				new DatePickerDialog(ProductWareHousingActivity.this,
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
		date.setText(datetime);
		btn_complte=(Button)title_01.findViewById(R.id.complete);
		btn_complte.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
		        db.execSQL("create table if not exists SaveData(_id integer primary key autoincrement,Code text,BarCode text,Number text,Department text,Person text,wareType text,buisesType text,ware text,author text)");
		        String sql="select * from UploadData";
		        Cursor cursor=db.rawQuery(sql, null);
		        if (cursor.getCount()>0) {
					while (cursor.moveToNext()) {
						String code=cursor.getString(cursor.getColumnIndex("Code"));
						String barcode=cursor.getString(cursor.getColumnIndex("BarCode"));
						String number=cursor.getString(cursor.getColumnIndex("Number"));
						String department=cursor.getString(cursor.getColumnIndex("Department"));
						String person=cursor.getString(cursor.getColumnIndex("Person"));
						String waretype=cursor.getString(cursor.getColumnIndex("wareType"));
						String buistype=cursor.getString(cursor.getColumnIndex("buisesType"));
						String ware=cursor.getString(cursor.getColumnIndex("ware"));
						String author=cursor.getString(cursor.getColumnIndex("author"));
						db.execSQL("insert into SaveData(Code,BarCode,Number,Department,Person,wareType,buisesType,ware,author) values('"+code+"','"+barcode+"','"+number+"','"+department+"','"+person+"','"+waretype+"','"+buistype+"','"+ware+"','"+author+"')");
					}
				}
		        db.execSQL("delete from UploadData");
				db.execSQL("insert into UploadCompleteData(Number,date,person,department,type,state) values('"+numberText.getText()+"','"+date.getText()+"','"+spinner_person.getSelectedItem()+"','"+warehouseText.getText()+"','"+spinner_warehouse.getSelectedItem()+"','未上传')");
				Toast.makeText(ProductWareHousingActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
				onCreate(null);
			}
		});
		
	}

	private void findViews() {
		imageViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
		imageViewRight = (ImageView) findViewById(R.id.imageViewRight);
		imageViewLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 Intent inte = new Intent(ProductWareHousingActivity.this, IndexActivity.class);			
				 startActivity(inte);
			}
		});
		imageViewRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent inte = new Intent(ProductWareHousingActivity.this,ProductPutListActivity.class);
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
		SimpleAdapter simpleAdapter=new SimpleAdapter(ProductWareHousingActivity.this, initData(),R.layout.layout_warehouse_detail_list, new String[]{"product","code","count"},new int[]{R.id.tv_product,R.id.tv_code,R.id.tv_count});
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
		String sql="SELECT Code,count(*) as Total FROM UploadData GROUP BY Code";
		SQLiteDatabase db=openOrCreateDatabase("OutStorage.db", MODE_PRIVATE, null);
        db.execSQL("create table if not exists UploadData(_id integer primary key autoincrement,Code text,BarCode text,Number text,Department text,Person text,wareType text,buisesType text,ware text,author text)");
		Cursor cursor=db.rawQuery(sql, null);
		Log.d("info", "cursor_count="+cursor.getCount());
		if (cursor!=null) {
			while(cursor.moveToNext()) {
				Map<String, Object> map=new HashMap<String, Object>();
				String code=cursor.getString(cursor.getColumnIndex("Code"));
				String count=cursor.getString(cursor.getColumnIndex("Total"));
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
}
