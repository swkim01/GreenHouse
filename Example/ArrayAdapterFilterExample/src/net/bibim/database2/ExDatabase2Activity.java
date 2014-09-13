package net.bibim.database2;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class ExDatabase2Activity extends Activity {
	int year1;
	int month1;
	int day1;
	int hour1;
	int minute1;
	
	EditText editText1;
	Button btnDate;
	Button btnTime;
	Button btnSave;
    TextView textView1;
    TextView textView2;
	Intent intent;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        editText1=(EditText) findViewById(R.id.editText1);
        btnDate=(Button) findViewById(R.id.btnDate);
        btnTime=(Button) findViewById(R.id.btnTime);
        btnSave=(Button) findViewById(R.id.btnSave);
        textView1=(TextView) findViewById(R.id.textView1);
        textView2=(TextView) findViewById(R.id.textView2);
        
        Calendar cal=new GregorianCalendar();
        year1=cal.get(Calendar.YEAR);
        month1=cal.get(Calendar.MONTH);
        day1=cal.get(Calendar.DAY_OF_MONTH);
        hour1=cal.get(Calendar.HOUR_OF_DAY);
        minute1=cal.get(Calendar.MINUTE);
        
        UpdateNow();
        
        btnDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new DatePickerDialog(ExDatabase2Activity.this, mDateSetListener, year1, month1, day1).show();
				
			}
		});
        
        btnTime.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new TimePickerDialog(ExDatabase2Activity.this, mTimeSetListener, hour1, minute1, false).show();	
			}
		});
        
        btnSave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				intent=new Intent(ExDatabase2Activity.this, listViewActivity.class);
				intent.putExtra("edit1", editText1.getText().toString());
				intent.putExtra("text", textView1.getText().toString() + textView2.getText().toString());
				startActivityForResult(intent, 0);	
				editText1.setText("");
			    textView1.setText("");
			    textView2.setText("");
			}
		});
       
        
    }
    
    DatePickerDialog.OnDateSetListener mDateSetListener= new DatePickerDialog.OnDateSetListener() {
    	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
    		year1=year;
    		month1=monthOfYear;
    		day1=dayOfMonth;
    		UpdateNow();
    	}
	};
	

	TimePickerDialog.OnTimeSetListener mTimeSetListener=new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			hour1=hourOfDay;
			minute1=minute;
			UpdateNow();
		}
	};
	
	void UpdateNow(){
		textView1.setText(String.format(" %d.%d.%d ", year1, month1 + 1, day1));
		textView2.setText(String.format(" %d½Ã %dºÐ", hour1, minute1));
	}
}