package deu.sw.greenhouse.activity;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import jp.androidgroup.nyartoolkit.markersystem.NyARAndMarkerSystem;
import jp.androidgroup.nyartoolkit.markersystem.NyARAndSensor;
import jp.androidgroup.nyartoolkit.sketch.AndSketch;
import jp.androidgroup.nyartoolkit.utils.camera.CameraPreview;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
import jp.nyatla.nyartoolkit.core.NyARException;
import jp.nyatla.nyartoolkit.core.types.NyARDoublePoint3d;
import jp.nyatla.nyartoolkit.core.types.matrix.NyARDoubleMatrix44;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystemConfig;
import android.R.integer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;
import deu.sw.greenhouse.R;
import deu.sw.greenhouse.ar.AirFanButton;
import deu.sw.greenhouse.ar.Circle;
import deu.sw.greenhouse.ar.GraphSquare;
import deu.sw.greenhouse.ar.IGLPlane;
import deu.sw.greenhouse.ar.InfoCircleHumidity;
import deu.sw.greenhouse.ar.InfoCircleIlluminance;
import deu.sw.greenhouse.ar.InfoCircleSoilHumidity;
import deu.sw.greenhouse.ar.InfoCircleTemperature;
import deu.sw.greenhouse.ar.LEDButton;
import deu.sw.greenhouse.graph.TimeValue;
import deu.sw.greenhouse.mqtt.PushService;
import deu.sw.greenhouse.mqtt.PushService.LocalBinder;


public class ARPreviewActivity extends AndSketch implements AndGLView.IGLFunctionEvent, IGLPlane
{
	public interface InfoCircleClickEventListener {
		public void onClick(Circle infoCircle);
	}
	
	public interface GraphClickEventListener {
		public void onClick(GraphSquare graph);
	}
	
	//private static final int SEND_TIMEVALUE = 0;
	//private SendMassgeHandler mMainHandler = null;
	private PushService mService;
	private boolean mBind = false; //서비스 연결여부 (바인딩 여부)
	
	CameraPreview _camera_preview;
	AndGLView _glv;
	Camera.Size _cap_size;
	int screen_w,screen_h;
	
	public ArrayList<Circle> _circle = new ArrayList<Circle>();
	public ArrayList<GraphSquare> _graph = new ArrayList<GraphSquare>();
	public ArrayList<TimeValue> timeValueHumidity = new ArrayList<TimeValue>();
	public ArrayList<TimeValue> timeValueIlluminance = new ArrayList<TimeValue>();
	public ArrayList<TimeValue> timeValueTemperature = new ArrayList<TimeValue>();
	
	private String mDeviceID;
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDeviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
    	Editor editor = getSharedPreferences(PushService.TAG, MODE_PRIVATE).edit();
    	editor.putString(PushService.PREF_DEVICE_ID, mDeviceID);
    	editor.commit();
    	//this._glv=new AndGLView(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		SharedPreferences p = getSharedPreferences(PushService.TAG, MODE_PRIVATE);
		boolean started = p.getBoolean(PushService.PREF_STARTED, false);
//		
//		screen_w=this.getWindowManager().getDefaultDisplay().getWidth();
//		screen_h=this.getWindowManager().getDefaultDisplay().getHeight();
//		
//		FrameLayout fr=((FrameLayout)this.findViewById(R.id.sketchLayout));
//
//		this._camera_preview=new CameraPreview(this);
//		this._cap_size=this._camera_preview.getRecommendPreviewSize((int)((screen_w/2)*0.9),(int)((screen_h/2)*0.9));
//		
//		//camera
//		fr.addView(this._camera_preview, 0, new LayoutParams(screen_w,screen_h));
//		//GLview
//		this._glv=new AndGLView(this);
//		fr.addView(this._glv, 0,new LayoutParams(screen_w,screen_h));
	}
	
	public void setGraphValue(ArrayList<TimeValue> timevalue, String graphId) {
		if (graphId.equals("humi")) {
			this.timeValueHumidity = timevalue;
		}
		else if (graphId.equals("illu")) {
			this.timeValueIlluminance = timevalue;
		}
		else if (graphId.equals("temp")) {
			this.timeValueTemperature = timevalue;
		}
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public synchronized void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mService.registerCallback(mCallback);
			//mService.truebound();
			mBind = true;
			setupCreateGraph();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBind = false;
		}
		
		private PushService.ICallback mCallback = new PushService.ICallback() {
			@Override
			public void sendData(ArrayList<TimeValue> timevalue, String graphId) {
				setGraphValue(timevalue, graphId);
			}
			
//			@Override
//			public void boundToTrue() {
//				setbound(true);
//			}
		};
	};
//    public void setbound(Boolean t)
//    {
//    	this.mBind = t;
//		Log.i("ssss", "mBind = true");
//    }
	
	@Override
	public void onStop() {
		super.onStop();
		
		if(mBind) {
			unbindService(mConnection);
			mBind = false;
		}
		StopPushService();
		this._glv=null;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		screen_w=this.getWindowManager().getDefaultDisplay().getWidth();
		screen_h=this.getWindowManager().getDefaultDisplay().getHeight();

		FrameLayout fr=((FrameLayout)this.findViewById(R.id.sketchLayout));
		//camera
		this._camera_preview=new CameraPreview(this);
		this._cap_size=this._camera_preview.getRecommendPreviewSize((int)((screen_w/2)*0.9),(int)((screen_h/2)*0.9));
		fr.addView(this._camera_preview, 0, new LayoutParams(screen_w,screen_h));
		//GLview
		this._glv=new AndGLView(this);
		fr.addView(this._glv, 0,new LayoutParams(screen_w,screen_h));
		//_glv.bringToFront();//갤럭시 s2에서 이걸쓰면 시작시 도형이 나오고 그래프후 안나옴
							// 안쓰면 시작시 도형이 안나오고 그래프후 도형이 나옴
	}

	NyARAndSensor _ss;
	NyARAndMarkerSystem _ms;
	private int _mid;
	
	NyARDoubleMatrix44 _planeCoord;
	float coord[] = new float[16];
	
	InfoCircleHumidity humidity;
	InfoCircleIlluminance illuminance;
	InfoCircleTemperature temperature;
	InfoCircleSoilHumidity soilhumidity;
	
	LEDButton ledButton;
	AirFanButton airFanButton;
	GraphSquare humidityGraph;
	GraphSquare illuminanceGraph;
	GraphSquare temperatureGraph;
	
	public void bindservice() {
		StartPushService();
		Intent intent = new Intent(this, PushService.class);
		this.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	private InfoCircleClickEventListener iccel = new InfoCircleClickEventListener() {
		@Override
		public void onClick(Circle infoCircle) {
			Boolean state = infoCircle.getButtonState();
			String tag = infoCircle.getTag();
			String subTag = infoCircle.getSubTag();
			Log.i("test", "tag="+tag+", state="+state);
			if (state == true) {
				if (mBind && tag.equals("actuator")) {
					if (subTag.equals("led")) {
						mService.setLED(false);
					}
					else if (subTag.equals("airfan")) {
						mService.setAirFan(false);
					}
				}
				infoCircle.setButtonState(false);
			}
			else {
				if (tag.equals("info")) {
					Log.i("test", "subtag="+subTag);
					requestGraphData(subTag);
					setGraphData(subTag);
					
					for (Circle circle : _circle) {
						if (circle.getTag().equals("info"))
							circle.setButtonState(false);
					}
				}
				else if (mBind && tag.equals("actuator")) {
					if (subTag.equals("led")) {
						mService.setLED(true);
					}
					else if (subTag.equals("airfan")) {
						mService.setAirFan(true);
					}
				}
				infoCircle.setButtonState(true);
			}
		}
	};
	
	private GraphClickEventListener gcel = new GraphClickEventListener() {
		
		@Override
		public void onClick(GraphSquare graph) {
			// TODO Auto-generated method stub
			if (graph.getState()) { 			
				Log.i("test", "그래프 이밴트 발생");
				startDetailGraphActivity(graph.getTag());
			}
		}
	};
	
	public void setupGL(GL10 gl) {
		try {
			AssetManager assetMng = getResources().getAssets();
			//create sensor controller.
			this._ss=new NyARAndSensor(this._camera_preview,this._cap_size.width,this._cap_size.height,30);
			//create marker system
			this._ms=new NyARAndMarkerSystem(new NyARMarkerSystemConfig(this._cap_size.width,this._cap_size.height));
			//this._mid=this._ms.addARMarker(assetMng.open("AR/data/deusw16.pat"),16,25,80);
			this._mid=this._ms.addARMarker(assetMng.open("AR/data/marker16_2.pat"),16,25,80);
			this._ss.start();
			//setup openGL Camera Frustum
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadMatrixf(this._ms.getGlProjectionMatrix(),0);
			//this._debug=new AndGLDebugDump(this._glv);
			this._planeCoord = new NyARDoubleMatrix44();
			this._planeCoord.setTranslate(-100, 0, 0);
			this._planeCoord.rotateX(Math.PI*45/180);
			double dcoord[] = new double[16];
			
			this._planeCoord.getValueT(dcoord);
			
			toFloatValues(dcoord, coord);
			
			this.humidity = new InfoCircleHumidity(this._glv, this, 128, 128, 0, 20);		
			this.illuminance = new InfoCircleIlluminance(this._glv, this, 128, 128, 0, 20);
			this.temperature = new InfoCircleTemperature(this._glv, this, 128, 128, 0, 20);
			this.soilhumidity = new InfoCircleSoilHumidity(this._glv, this, 128, 128, 0, 20);
			this.ledButton = new LEDButton(this._glv, this, 64+50, 64+50, 0, 0);
			this.airFanButton = new AirFanButton(this._glv, this, 64+50, 64+50, 0, 0);
			this.humidityGraph = new GraphSquare(this._glv, this, "%", 256+128+32, 256, 0);
			this.humidityGraph.setTag(HUMIDITY);
			this.illuminanceGraph = new GraphSquare(this._glv, this, "L%", 256+128+32, 256, 0);
			this.illuminanceGraph.setTag(ILLUMINANCE);
			this.temperatureGraph = new GraphSquare(this._glv, this, "℃", 256+128+32, 256, 0);
			this.temperatureGraph.setTag(TEMPERATURE);
			bindservice();
			//setupCreateGraph();
			for (Circle circle : _circle) {
				circle.setOnClickListener(iccel);
			}
			for (GraphSquare graph : _graph) {
				graph.setOnClickListener(gcel);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.finish();
		}
	}
	//AndGLDebugDump _debug=null;

	public void toFloatValues(double[] i_val, float[] o_val) {
		for (int i=0; i < 4; i++) {
			for (int j=0; j < 4; j++) {
				o_val[i*4+j] = (float) i_val[i*4+j];
			}
		}
	}
	
	/**
	 * 継承したクラスで表示したいものを実装してください
	 * @param gl
	 */
	public void drawGL(GL10 gl)	{
		try{
			//背景塗り潰し色の指定
			gl.glClearColor(0,0,0,0);
	        //背景塗り潰し
	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
	       
//	        if(ex!=null){
//	        	_debug.draw(ex);
//	        	return;						
//	        }													
			synchronized(this._ss) {		
				this._ms.update(this._ss);	
				if (this._ms.isExistMarker(this._mid)) {
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					
					gl.glLoadMatrixf(this._ms.getGlMarkerMatrix(this._mid),0);
					
					gl.glMultMatrixf(coord, 0);
					
					this.temperature.draw(0, 270-50, 0);
					this.temperature.setText(getSensorValueToString("temp"));
					this.humidity.draw(-70, 150-50, 0);
					this.humidity.setText(getSensorValueToString("humi"));
					this.illuminance.draw(70, 150-50, 0);
					this.illuminance.setText(getSensorValueToString("illu"));
					this.soilhumidity.draw(170, 250, 0);
					//this.soilhumidity.setText(getSensorValueToString("soil"));
					this.ledButton.draw(250, 150-50, 0);
					this.airFanButton.draw(250, 200, 0);
					
					if (humidity.getButtonState()) {
						this.humidityGraph.draw(20, -120, 20);
						setGraphState(true, false, false);
					}
					else if (illuminance.getButtonState()) {
						this.illuminanceGraph.draw(20, -120, 20);
						setGraphState(false, true, false);
					}
					else if (temperature.getButtonState()) {
						this.temperatureGraph.draw(20, -120, 20);
						setGraphState(false, false, true);
					}
					else {
						setGraphState(false, false, false);
					}
					
				}
			}
		}
		catch (Exception e) {
			ex=e;
		}
	}
	
	public void setGraphState(Boolean humi, Boolean illu, Boolean temp) {
		this.humidityGraph.setState(humi);
		this.temperatureGraph.setState(temp);
		this.illuminanceGraph.setState(illu);
	}
	
	private final int HUMIDITY = 1;
	private final int ILLUMINANCE = 2;
	private final int TEMPERATURE = 3;
	
	public void startDetailGraphActivity(int tag) {
		ArrayList<TimeValue> tv = null;
		int color = 0;
		String value_Y = null;
		int value_Max = 0;
		switch (tag) {
		case HUMIDITY:
			tv=timeValueHumidity;
			color=Color.BLUE;
			value_Y="%";
			value_Max=100;
			break;
			
		case ILLUMINANCE:
			tv=timeValueIlluminance;
			color=Color.YELLOW;
			value_Y="L%";
			value_Max=1000;			
			break;
			
		case TEMPERATURE:
			tv=timeValueTemperature;
			color=Color.RED;
			value_Y="℃";
			value_Max=50;
			break;

		default:
			break;
		}

		Intent intent = new Intent(this, DetailGraphActivity.class);
		intent.putParcelableArrayListExtra("graph",  tv);
		intent.putExtra("color", color);
		intent.putExtra("value_Y", value_Y);
		intent.putExtra("value_Max", value_Max);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
		finish();
	}
	
//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) { // 백 버튼
//	 		Intent intent = new Intent(this, GreenHouseMainActivity.class);
//	 		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			//finish();
//		}   
//		return true;
//
//	}
	
	@Override
	public void registerComponent(Circle circle) {
		// TODO Auto-generated method stub
		this._circle.add(circle);
	}
	
	@Override
	public void registerComponent(GraphSquare graph) {
		// TODO Auto-generated method stub
		this._graph.add(graph);
	}

	
	private int tx=0;
	private int ty=0;
	private NyARDoubleMatrix44 planeMat = new NyARDoubleMatrix44();
    public boolean onTouchEvent(MotionEvent event) {
    	int action = event.getAction();
    	
    	switch (action) {//화면 터치가 눌러졋을경우
    	case MotionEvent.ACTION_DOWN :
    	    //화면 크기 → OpenGL 크기로 변환
    		if (event.getX() < screen_w) {
    			tx = (int)(event.getX()*this._cap_size.width/screen_w);
    			ty = (int)(event.getY()*this._cap_size.height/screen_h);
    			executeSpriteClick(tx, ty);
    		}
    		break;
    	}
		return true;
    }
	
	Exception ex=null;

	public void executeSpriteClick(int tx, int ty) {
		for (Circle circle : _circle) {
			NyARDoublePoint3d p=new NyARDoublePoint3d();
			int cw = circle.getWidth();
			int ch = circle.getHeight();
			float trans_x = circle.getTrans_X();
			float trans_y = circle.getTrans_Y();
			float trans_z = circle.getTrans_Z();

			double r;
			try {
				planeMat.mul(this._ms.getMarkerMatrix(this._mid), this._planeCoord);
				planeMat.translate(0, 0, trans_z);
				this._ms.getFrustum().unProjectOnMatrix(tx, ty, planeMat, p);
				
				r = Math.sqrt((p.x-trans_x) * (p.x-trans_x) + (p.y-trans_y) * (p.y-trans_y));
				
				if (r <= cw / 2) {
					//Log.i("test", "확인용원");
					InfoCircleClickEventListener listener = circle.getClickListener();
					if (listener != null) {
						listener.onClick(circle);
					}
				}
			} catch (Exception e) {
				
			}
		}
		////////////////////////////////////////
		for (GraphSquare graph : _graph) {
			NyARDoublePoint3d p=new NyARDoublePoint3d();
			int sw = graph.getWidth();
			int sh = graph.getHeight();
			float trans_x = graph.getTrans_X();
			float trans_y = graph.getTrans_Y();
			float trans_z = graph.getTrans_Z();

			try {
				planeMat.mul(this._ms.getMarkerMatrix(this._mid), this._planeCoord);
				planeMat.translate(0, 0, trans_z);
				this._ms.getFrustum().unProjectOnMatrix(tx, ty, planeMat,p);
				
				if (p.x > (-sw/2+trans_x) && p.x < (sw/2+trans_x) && p.y > (-sh/2+trans_y) && p.y < (sh/2+trans_y)) {
					GraphClickEventListener listener = graph.getClickListener();
					if (listener != null) {
						listener.onClick(graph);
					}
				}
			} catch (Exception e) {
				
			}
		}
	}
	
	public void StartPushService() {
		PushService.actionStart(getApplicationContext());
	}
	
	public void StopPushService() {
		PushService.actionStop(getApplicationContext());
	}
	
	public String getSensorValueToString(String name) {
		String sensorValue = null;
		String returnValue = null;
		
		if (mBind) {
			sensorValue = mService.getSensorValueToString(name);
		}
		if (name.equals("soil")) {
			if (sensorValue != null && sensorValue.length() != 0) {
				int val = Integer.parseInt(sensorValue);
				if (val > 300) {
					returnValue = "충분";
				} else if(val <= 300) {
					returnValue = "부족";
				}
			}
			else {
				return returnValue = " ";
			}
			return returnValue;
		}
		return sensorValue;
	}
	
	public void requestGraphData(String s) {
		if (mBind && s.equals("Humidity")) {
			Log.i("test", "request Humidity");
			mService.requestToGetGraphData("humi");
			mService.requestGraphData("humi");
		}
		else if(mBind && s.equals("Illuminance")) {
			Log.i("test", "request Illuminance");
			mService.requestToGetGraphData("illu");
			mService.requestGraphData("illu");
		}
		else if(mBind && s.equals("Temperature")) {
			Log.i("test", "request Temperature");
			mService.requestToGetGraphData("temp");
			mService.requestGraphData("temp");
		}
	}
	
	public void setGraphData(String s) {
		if (s.equals("Humidity")) {
			this.humidityGraph.setGraph(100, Color.BLUE, this.timeValueHumidity);
		}
		else if(s.equals("Illuminance")) {
			Log.i("test", "setGraphData="+s);
			this.illuminanceGraph.setGraph(1000, Color.YELLOW, this.timeValueIlluminance);
			Log.i("test", "setGraphData="+s);
		}
		else if(s.equals("Temperature")) {
			this.temperatureGraph.setGraph(50, Color.RED, this.timeValueTemperature);
		}
	}
	
	public void setupCreateGraph() {	
		requestGraphData(humidity.getSubTag());
		requestGraphData(illuminance.getSubTag());
		requestGraphData(temperature.getSubTag());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// XML로 옵션메뉴 추가 하기
//		getMenuInflater().inflate(R.menu.activity_main, menu);

//		// Java Code로 옵션메뉴 추가 하기
//		menu.add(0, ONE, Menu.NONE, "ONE").setIcon(android.R.drawable.ic_menu_rotate);
//		menu.add(0, TWO, Menu.NONE, "TWO").setIcon(android.R.drawable.ic_menu_add);
//		menu.add(0, THREE, Menu.NONE, "THREE").setIcon(android.R.drawable.ic_menu_agenda);
//		menu.add(0, FOUR, Menu.NONE, "FOUR");
//		menu.add(0, FIVE, Menu.NONE, "FIVE");
		
		// Menu에 SubMenu 추가
		SubMenu subMenu = menu.addSubMenu("LED 밝기설정");
		
		subMenu.add(1, 1, Menu.NONE, "LED 밝기 1단계");
		subMenu.add(1, 2, Menu.NONE, "LED 밝기 2단계");
		subMenu.add(1, 3, Menu.NONE, "LED 밝기 3단계");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Toast.makeText(this, "LED 밝기 1단계", Toast.LENGTH_SHORT).show();
			if (ledButton.getButtonState())	{
				//mService.setLED(true);
				mService.setLEDPower(1);
			}
			break;
		case 2:
			Toast.makeText(this, "LED 밝기 2단계", Toast.LENGTH_SHORT).show();
			if (ledButton.getButtonState()) {
				//mService.setLED(true);
				mService.setLEDPower(4);
			}
			break;
		case 3:
			Toast.makeText(this, "LED 밝기 3단계", Toast.LENGTH_SHORT).show();
			if (ledButton.getButtonState()) {
				//mService.setLED(true);
				mService.setLEDPower(9);
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
