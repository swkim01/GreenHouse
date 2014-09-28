package deu.sw.greenhouse.activity;



import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import deu.sw.greenhouse.R;
import deu.sw.greenhouse.graph.GraphToBitmap;
import deu.sw.greenhouse.graph.TimeValue;

public class DetailGraphActivity extends Activity {
	
	Intent in;
	ArrayList<TimeValue> tv;
	int color;
	String value_Y;
	int value_Max;
	
	GraphToBitmap graph;
	ImageView _img;
	Bitmap bitmap;
	ArrayList<TimeValue> timeValue;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupDefaultActivity();
		setContentView(R.layout.activity_detailgraph);
		
		in = getIntent();
		tv = in.getParcelableArrayListExtra("graph");
		color = in.getExtras().getInt("color");
		value_Y = in.getExtras().getString("value_Y");
		value_Max = in.getExtras().getInt("value_Max"); 
		
		//timeValue = new ArrayList<TimeValue>();
		//setval();
		graph = new GraphToBitmap(value_Y, value_Max, color, tv);
		
		bitmap = graph.CreateGraph();
		_img = (ImageView) findViewById(R.id.graphimg);
		_img.setImageBitmap(bitmap);
		
		

		
	}
//	
//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event){
//		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // 백 버튼
//	 		Intent intent = new Intent(this, ARPreviewActivity.class);
//	 		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			startActivity(intent);
//			//finish();
//		}   
//		return true;
//
//	}
	
	protected void setupDefaultActivity() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	
//	public void setval()
//	{
//		timeValue.add(new TimeValue(13, 509));//1
//		timeValue.add(new TimeValue(14, 300));//2
//		timeValue.add(new TimeValue(15, 209));//3
//		timeValue.add(new TimeValue(16, 650));//4
//		timeValue.add(new TimeValue(17, 770));//5
//		timeValue.add(new TimeValue(18, 880));//6
//		timeValue.add(new TimeValue(19, 700));//7
//		timeValue.add(new TimeValue(20, 502));//8
//		timeValue.add(new TimeValue(21, 400));//9
//		timeValue.add(new TimeValue(22, 450));//10
//		timeValue.add(new TimeValue(23, 200));//11
//		timeValue.add(new TimeValue(24, 600));
//		timeValue.add(new TimeValue(1, 501));
//		timeValue.add(new TimeValue(2, 455));
//		timeValue.add(new TimeValue(3, 678));
//		timeValue.add(new TimeValue(4, 812));
//		timeValue.add(new TimeValue(5, 214));
//		timeValue.add(new TimeValue(6, 542));
//		timeValue.add(new TimeValue(7, 854));
//		timeValue.add(new TimeValue(8, 215));
//		timeValue.add(new TimeValue(9, 541));
//		timeValue.add(new TimeValue(10, 845));
//		timeValue.add(new TimeValue(11, 422));
//		timeValue.add(new TimeValue(12, 875));
//
//	}

}