package com.toyou.outstoragesystem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WareHousActivity extends Activity implements OnClickListener,
OnPageChangeListener {
	
	// 顶部左右侧按钮
		private ImageView imageViewLeft, imageViewRight;
		// 中部菜单3个Linearlayout
		private LinearLayout ll_title;
		private LinearLayout ll_detail;
		private LinearLayout ll_content;
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
	    private SoundPool soundPoolMusic;
	    private MediaPlayer mp = new MediaPlayer();
	    private int soundid;
	    private String barcodeStr;
	    private boolean isScaning = false;
	    private View detail_02;
	    
	    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {
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
	            //mp=MediaPlayer.create(context, R.raw.ok);
	            //mp.start();
	            //mp.release();
	            
	        }
	    };
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_warehous);
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
        showScanResult = (EditText)detail_02.findViewById(R.id.scan_result);     
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mScanManager != null) {
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
		View title_01 = View
				.inflate(WareHousActivity.this, R.layout.activity_warehouse_title, null);
		detail_02 = View.inflate(WareHousActivity.this, R.layout.activity_warehouse_detail,
				null);
		View content_03 = View.inflate(WareHousActivity.this, R.layout.activity_warehouse_content,
				null);
		date = (EditText)title_01.findViewById(R.id.date);
		date.setInputType(InputType.TYPE_NULL);   
		date.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
            @Override  
            public void onFocusChange(View v, boolean hasFocus) {  
                if(hasFocus){  
                    Calendar c = Calendar.getInstance(Locale.CHINA);  
                    new DatePickerDialog(WareHousActivity.this, new DatePickerDialog.OnDateSetListener() {  
                        @Override  
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  
                        	date.setText(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);  
                        }  
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();  
                }  
            }  
        });  
          
		date.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                Calendar c = Calendar.getInstance(Locale.CHINA);  
                new DatePickerDialog(WareHousActivity.this, new DatePickerDialog.OnDateSetListener() {  
                    @Override  
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  
                    	date.setText(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);  
                    }  
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();  
             }  
        });  


		views = new ArrayList<View>();
		views.add(title_01);
		views.add(detail_02);
		views.add(content_03);

		this.adapter = new ContentAdapter(views);
		viewPager.setAdapter(adapter);
	}

	private void findViews() {
		imageViewLeft = (ImageView) findViewById(R.id.imageViewLeft);
		imageViewRight = (ImageView) findViewById(R.id.imageViewRight);

		imageViewLeft.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Intent inte = new Intent(PickPackActivity.this, "");
				//startActivity(inte);
			}
		});
		imageViewRight.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Intent inte = new Intent(PickPackActivity.this, "");
				//startActivity(inte);
			}
		});
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		restartBotton();
		// 当前view被选择的时候,改变底部菜单图片，文字颜色
		switch (arg0) {
		case 0:
			tv_title.setTextColor(0xff1B940A);
			break;
		case 1:
			tv_detail.setTextColor(0xff1B940A);
			break;
		case 2:
			tv_content.setTextColor(0xff1B940A);
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		restartBotton();
		// 在每次点击后将所有的底部按钮(ImageView,TextView)颜色改为灰色，然后根据点击着色
		// ImageView和TetxView置为绿色，页面随之跳转
		switch (v.getId()) {
		case R.id.ll_title:
			tv_title.setTextColor(0xff1B940A);
			viewPager.setCurrentItem(0);
			break;
		case R.id.ll_detail:
			tv_detail.setTextColor(0xff1B940A);
			viewPager.setCurrentItem(1);
			break;
		case R.id.ll_content:
			tv_content.setTextColor(0xff1B940A);
			viewPager.setCurrentItem(2);
			break;
		default:
			break;
		}
	}
	private void restartBotton() {
		// TextView置为白色
		tv_title.setTextColor(0xffffffff);
		tv_detail.setTextColor(0xffffffff);
		tv_content.setTextColor(0xffffffff);
	}
}
