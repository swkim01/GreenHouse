package deu.sw.greenhouse.activity;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

//import jp.androidgroup.nyartoolkit.R;
import jp.androidgroup.nyartoolkit.markersystem.NyARAndMarkerSystem;
import jp.androidgroup.nyartoolkit.markersystem.NyARAndSensor;
import jp.androidgroup.nyartoolkit.sketch.AndSketch;
import jp.androidgroup.nyartoolkit.utils.camera.CameraPreview;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLDebugDump;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
//import jp.nyatla.nyartoolkit.and.R;
import jp.nyatla.nyartoolkit.core.types.NyARDoublePoint3d;
import jp.nyatla.nyartoolkit.core.types.matrix.NyARDoubleMatrix44;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystemConfig;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import deu.sw.greenhouse.R;
import deu.sw.greenhouse.ar.IGLPlane;
import deu.sw.greenhouse.ar.InfoCircle;
import deu.sw.greenhouse.ar.InfoCircleHumidity;
import deu.sw.greenhouse.ar.InfoCircleIlluminance;
import deu.sw.greenhouse.ar.InfoCircleTemperature;
import deu.sw.greenhouse.mqtt.PushService;
import deu.sw.greenhouse.mqtt.PushService.LocalBinder;

/**
 * Hiroマーカの上にカラーキューブを表示します。
 * 定番のサンプルです。
 *
 */
public class ARPreviewActivity extends AndSketch implements AndGLView.IGLFunctionEvent, IGLPlane
{
	public interface InfoCircleClickEventListener {
		public void onClick(InfoCircle infoCircle);
	}
	
//	private final int SEND_PUSHSERVICE_VALUE_HUMIDITY = 0;
//	private final int SEND_PUSHSERVICE_VALUE_ILLUMINANCE = 1;
//	private final int SEND_PUSHSERVICE_VALUE_TEMPERATURE = 2;
	
	
	private PushService mService;
	private boolean mBound = false; //서비스 연결여부 (바운딩 여부)
	
	CameraPreview _camera_preview;
	AndGLView _glv;
	Camera.Size _cap_size;
	int screen_w,screen_h;
	
	public ArrayList<InfoCircle> _infoCircle = new ArrayList<InfoCircle>();
	
	///////////////////////////////mqtt////////////////
	private String mDeviceID;
	///////////////////////////////////////////////////////
	
	
	/////////////////////////test/////////////////////////////
	
	////////////////////////////test//////////////////////////
	//Bitmap bitmaptest;
	
	//View view;// = LayoutInflater.from(context).inflate(layoutID, null);
	//Do some stuff to the view, like add an ImageView, etc.
	//view.layout(0, 0, width, height);

	

	///////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////
//	
	@Override
	public void onDestroy()
	{
		//StopPushService();
		super.onDestroy();
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 메인 핸들러 생성
		//infoHandler = new SensorInfoHandler();
		mDeviceID = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
    	Editor editor = getSharedPreferences(PushService.TAG, MODE_PRIVATE).edit();
    	editor.putString(PushService.PREF_DEVICE_ID, mDeviceID);
    	editor.commit();
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
  	  	
  	  	SharedPreferences p = getSharedPreferences(PushService.TAG, MODE_PRIVATE);
  	  	boolean started = p.getBoolean(PushService.PREF_STARTED, false);
    }
	
	
    //ServiceConnection 인터페이스를 구현하는 객체를 생성한다.
    private ServiceConnection mConnection = new ServiceConnection() 
    {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			LocalBinder binder = (LocalBinder) service;
			mService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mBound = false;
		}
    };
    
    @Override
    public void onStop()
    {
    	super.onStop();
    	if(mBound) {
    		unbindService(mConnection);
    		mBound = false;
    	}
    	StopPushService();
    }
    
	
	/**
	 * onStartでは、Viewのセットアップをしてください。
	 */
	@Override
	public void onStart()
	{
		super.onStart();
		
		screen_w=this.getWindowManager().getDefaultDisplay().getWidth();
		screen_h=this.getWindowManager().getDefaultDisplay().getHeight();
		
		FrameLayout fr=((FrameLayout)this.findViewById(R.id.sketchLayout));
		
		///////////////////////////////mqtt////////////////
		//액티비티가 시작되면
		StartPushService();//서비스를 시작하고
		Intent intent = new Intent(this, PushService.class); //시작한 서비스에 연결한다.
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

		///////////////////////////////////////////////////////
		
		
		/////////////////////////////////test////////////////////////////////
		//////////////////////////////////////test/////////////////////////////
//		
//		FrameLayout frgraph=((FrameLayout)this.findViewById(R.id.graphLayout));
//		
//		frgraph.addview
//		this.view = LayoutInflater.from(getApplicationContext()).inflate(R.id.graphLayout, null);
		//this.view.layout(0, 0, screen_w, screen_h);
		//////////////////////////////////////////////////////////////////////////////
		
		
		
		//カメラの取得
		this._camera_preview=new CameraPreview(this);
		this._cap_size=this._camera_preview.getRecommendPreviewSize(screen_w,screen_h);
		
//		if(screen_w>640){
//			screen_w/=2;
//			screen_h/=2;
//		}
		
		//画面サイズの計算
		//camera
		fr.addView(this._camera_preview, 0, new LayoutParams(screen_w,screen_h));
		//GLview
		this._glv=new AndGLView(this);
		fr.addView(this._glv, 0,new LayoutParams(screen_w,screen_h));
		
		

		
		
	}

	NyARAndSensor _ss;
	NyARAndMarkerSystem _ms;
	private int _mid;
	
	NyARDoubleMatrix44 _planeCoord;
	float coord[] = new float[16];
	
	InfoCircleHumidity humidity;
	InfoCircleIlluminance illuminance;
	InfoCircleTemperature temperature;
	
	
	
	
	public void setupGL(GL10 gl)
	{
		try {
			AssetManager assetMng = getResources().getAssets();
			//create sensor controller.
			this._ss=new NyARAndSensor(this._camera_preview,this._cap_size.width,this._cap_size.height,30);
			//create marker system
			this._ms=new NyARAndMarkerSystem(new NyARMarkerSystemConfig(this._cap_size.width,this._cap_size.height));
			this._mid=this._ms.addARMarker(assetMng.open("AR/data/deusw16.pat"),16,25,80);
			this._ss.start();
			//setup openGL Camera Frustum
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadMatrixf(this._ms.getGlProjectionMatrix(),0);
			this._debug=new AndGLDebugDump(this._glv);
			
			this._planeCoord = new NyARDoubleMatrix44();
			this._planeCoord.setTranslate(-110, 0, 0);
			this._planeCoord.rotateX(Math.PI*90/180);
			double dcoord[] = new double[16];
			
			this._planeCoord.getValueT(dcoord);
			
			toFloatValues(dcoord, coord);
			
			/////////////////////////test/////////////////////////////
			////////////////////////////test//////////////////////////
			//bitmaptest = getGraphToBitmap();
			
			
			//StartPushService();
			///////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////
			
			
			
			
			this.humidity = new InfoCircleHumidity(this._glv, this, 128, 128, 0, 20);		
			//this.humidity.setBitmap(bitmaptest, bitmaptest);
			this.illuminance = new InfoCircleIlluminance(this._glv, this, 128, 128, 0, 20);
			this.temperature = new InfoCircleTemperature(this._glv, this, 128, 128, 0, 20);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.finish();
		}
	}
	AndGLDebugDump _debug=null;

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
	public void drawGL(GL10 gl)
	{
		try{
			//背景塗り潰し色の指定
			gl.glClearColor(0,0,0,0);
	        //背景塗り潰し
	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);
	       
	        if(ex!=null){
	        	_debug.draw(ex);
	        	return;						
	        }									
	      // fps.draw(0, 0);					
			synchronized(this._ss){		
				this._ms.update(this._ss);	
				if(this._ms.isExistMarker(this._mid)){
			    // this.text.draw("found"+this._ms.getConfidence(this._mid),0,16);
					gl.glMatrixMode(GL10.GL_MODELVIEW);
					gl.glLoadMatrixf(this._ms.getGlMarkerMatrix(this._mid),0);
					
					gl.glMultMatrixf(coord, 0);

					
					this.temperature.draw(0, 270-50, 0);
					this.temperature.setTextg(getSensorValueToString_Temp());

					this.humidity.draw(-70, 150-50, 0);
					this.humidity.setText(getSensorValueToString_Humi());
					
					this.illuminance.draw(70, 150-50, 0);
					this.illuminance.setText(getSensorValueToString_Illu());
					
					for (InfoCircle circle : _infoCircle) {
						circle.setOnClickListener(new InfoCircleClickEventListener() {
							@Override
							public void onClick(InfoCircle infoCircle) {
								Boolean state = infoCircle.getButtonState();
								if(state == true)
									infoCircle.setButtonState(false);
								else
									infoCircle.setButtonState(true);
							}
						});
					}
					
				}
		}
		}catch(Exception e)
		{
			ex=e;
		}
	}
	
	
	@Override
	public void registerComponent(InfoCircle circle) {
		// TODO Auto-generated method stub
		this._infoCircle.add(circle);
	}

	
	private int tx=0;
	private int ty=0;
	private NyARDoubleMatrix44 planeMat = new NyARDoubleMatrix44();
    public boolean onTouchEvent(MotionEvent event)
    {
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
		for (InfoCircle circle : _infoCircle) {
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
				this._ms.getFrustum().unProjectOnMatrix(tx, ty, planeMat,p);
				//Log.d("executeSpriteClick", "x="+p.x+" y="+p.y+" z="+p.z);
				
				r = Math.sqrt((p.x-trans_x) * (p.x-trans_x) + (p.y-trans_y) * (p.y-trans_y));
				
				if (r <= cw / 2) {	
					InfoCircleClickEventListener listener = circle.getClickListener();
					if (listener != null) {
						listener.onClick(circle);
						//Toast toast = Toast.makeText(getApplicationContext(), "x:"+(int)p.x + "\n" +"y:"+(int)p.y + "\n"+"z:"+p.z, Toast.LENGTH_SHORT);
						//toast.show();
					}
				}
			} catch (Exception e) {
				
			}

		}
		
	}
	
	
	///////////////////////test//////////////////////////////////
	
	
//	public Bitmap getGraphToBitmap()
//	{
//		CurveGraphVO vo = makeCurveGraphAllSetting();
//		//Bitmap bitmaptest = viewToBitmap(new CurveGraphView(getApplicationContext(), vo));
//		Bitmap bitmaptest = getViewBitmap(new CurveGraphView(this, vo));
//		return bitmaptest;
//	}
//	
//	public static Bitmap viewToBitmap(View view) {
//	    Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
//	    Canvas canvas = new Canvas(bitmap);
//	    if (view instanceof SurfaceView) {
//	        SurfaceView surfaceView = (SurfaceView) view;
//	        surfaceView.setZOrderOnTop(true);
//	        surfaceView.draw(canvas);
//	        surfaceView.setZOrderOnTop(false);
//	        return bitmap;
//	    } else {
//	        
//	//For ViewGroup & View
//	        view.draw(canvas);
//	        return bitmap;
//	    }
//	}
//	
//	
//	
//	private CurveGraphVO makeCurveGraphDefaultSetting() {
//		
//		String[] legendArr 	= {"1","2","3","4","5"};
//		float[] graph1 		= {500,100,300,200,100};
//		float[] graph2 		= {000,100,200,100,200};
//		float[] graph3 		= {200,500,300,400,000};
//		
//		List<CurveGraph> arrGraph 		= new ArrayList<CurveGraph>();
//		arrGraph.add(new CurveGraph("android", 0xaa66ff33, graph1));
//		arrGraph.add(new CurveGraph("ios", 0xaa00ffff, graph2));
//		arrGraph.add(new CurveGraph("tizen", 0xaaff0066, graph3));
//		
//		CurveGraphVO vo = new CurveGraphVO(legendArr, arrGraph);
//		return vo;
//	}
//	
//	
//	private CurveGraphVO makeCurveGraphAllSetting() {
//		//BASIC LAYOUT SETTING
//		//padding
//		int paddingBottom 	= CurveGraphVO.DEFAULT_PADDING;
//		int paddingTop 		= CurveGraphVO.DEFAULT_PADDING;
//		int paddingLeft 	= CurveGraphVO.DEFAULT_PADDING;
//		int paddingRight 	= CurveGraphVO.DEFAULT_PADDING;
//
//		//graph margin
//		int marginTop 		= CurveGraphVO.DEFAULT_MARGIN_TOP;
//		int marginRight 	= CurveGraphVO.DEFAULT_MARGIN_RIGHT;
//
//		//max value
//		int maxValue 		= CurveGraphVO.DEFAULT_MAX_VALUE;
//
//		//increment
//		int increment 		= CurveGraphVO.DEFAULT_INCREMENT;
//		
//		//GRAPH SETTING
//		String[] legendArr 	= {"일","이","삼","사","오"};
//		float[] graph1 		= {500,100,300,200,100};
//		float[] graph2 		= {000,100,200,100,200};
//		float[] graph3 		= {200,500,300,400,000};
//		
//		List<CurveGraph> arrGraph 		= new ArrayList<CurveGraph>();
//		//arrGraph.add(new CurveGraph("android", 0xaa66ff33, graph1, R.drawable.ic_launcher));
//		arrGraph.add(new CurveGraph("android", 0xaa66ff33, graph1));
//		arrGraph.add(new CurveGraph("ios", 0xaa00ffff, graph2));
//		arrGraph.add(new CurveGraph("tizen", 0xaaff0066, graph3));
//		
//		CurveGraphVO vo = new CurveGraphVO(
//				paddingBottom, paddingTop, paddingLeft, paddingRight,
//				marginTop, marginRight, maxValue, increment, legendArr, arrGraph);
//		
//		//set animation
//		vo.setAnimation(new GraphAnimation(GraphAnimation.CURVE_REGION_ANIMATION_2, GraphAnimation.DEFAULT_DURATION));
//		//set graph name box
//		vo.setGraphNameBox(new GraphNameBox());
//		//set draw graph region
//		vo.setDrawRegion(true);
//		
//		//use icon
////		arrGraph.add(new Graph(0xaa66ff33, graph1, R.drawable.icon1));
////		arrGraph.add(new Graph(0xaa00ffff, graph2, R.drawable.icon2));
////		arrGraph.add(new Graph(0xaaff0066, graph3, R.drawable.icon3));
//		
////		CurveGraphVO vo = new CurveGraphVO(
////				paddingBottom, paddingTop, paddingLeft, paddingRight,
////				marginTop, marginRight, maxValue, increment, legendArr, arrGraph, R.drawable.bg);
//		return vo;
//	}
//	
//	
//	
//	
//	
//	Bitmap getViewBitmap(View view)
//	{
//	    //Get the dimensions of the view so we can re-layout the view at its current size
//	    //and create a bitmap of the same size 
//	    int width = view.getWidth();
//	    int height = view.getHeight();
//
//	    int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
//	    int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
//
//	    //Cause the view to re-layout
//	    view.measure(measuredWidth, measuredHeight);
//	    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//
//	    //Create a bitmap backed Canvas to draw the view into
//	    Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//	    Canvas c = new Canvas(b);
//
//	    //Now that the view is laid out and we have a canvas, ask the view to draw itself into the canvas
//	    view.draw(c);
//
//	    return b;
//	}
//	
//	
//	
//	
//	
//	
//	
	
	
	
	
	
	//////////////////////////////////////////////////////////////////////
	////////////mqtt/////////////
	public void StartPushService()
	{
		PushService.actionStart(getApplicationContext());
	}
	
	public void StopPushService()
	{
		PushService.actionStop(getApplicationContext());
	}
	
	public String getSensorValueToString_Illu()
	{
		String sensorValue = null;
		
		if(mBound)
		{
			sensorValue = mService.getSensorValueToString_Illu();
		}	
		
		return sensorValue;
	}
	
	public String getSensorValueToString_Humi()
	{
		String sensorValue = null;
		
		if(mBound)
		{
			sensorValue = mService.getSensorValueToString_Humi();
		}	
		
		return sensorValue;
	}
	
	public String getSensorValueToString_Temp()
	{
		String sensorValue = null;
		
		if(mBound)
		{
			sensorValue = mService.getSensorValueToString_Temp();
		}	
		
		return sensorValue;
	}
	
	
	
	
	
	///////////////////////////
	
	
}
