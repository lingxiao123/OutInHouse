package com.toyou.outstoragesystem;
import java.util.Calendar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;

public class TitleActivity extends Activity  {
	private EditText date;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		setContentView(R.layout.activity_title);
		date=(EditText) findViewById(R.id.date);
		date.setInputType(InputType.TYPE_NULL);   
		date.setOnFocusChangeListener(new View.OnFocusChangeListener() {  
            @Override  
            public void onFocusChange(View v, boolean hasFocus) {  
                if(hasFocus){  
                    Calendar c = Calendar.getInstance();  
                    new DatePickerDialog(TitleActivity.this, new DatePickerDialog.OnDateSetListener() {  
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
                Calendar c = Calendar.getInstance();  
                new DatePickerDialog(TitleActivity.this, new DatePickerDialog.OnDateSetListener() {  
                    @Override  
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {  
                    	date.setText(year+"/"+(monthOfYear+1)+"/"+dayOfMonth);  
                    }  
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();  
             }  
        });  
	}
}
