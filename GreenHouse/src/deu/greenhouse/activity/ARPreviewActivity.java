package deu.greenhouse.activity;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;











import deu.greenhouse.ar.IGLPlane;
import deu.greenhouse.ar.InfoCircle;
import deu.greenhouse.ar.InfoCircleHumidity;
import deu.greenhouse.ar.InfoCircleIlluminance;
import deu.greenhouse.ar.InfoCircleTemperature;
//import jp.androidgroup.nyartoolkit.R;
import jp.androidgroup.nyartoolkit.markersystem.NyARAndMarkerSystem;
import jp.androidgroup.nyartoolkit.markersystem.NyARAndSensor;
import jp.androidgroup.nyartoolkit.sketch.AndSketch;
import jp.androidgroup.nyartoolkit.utils.camera.CameraPreview;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLDebugDump;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
import jp.nyatla.nyartoolkit.and.R;
import jp.nyatla.nyartoolkit.core.types.NyARDoublePoint3d;
import jp.nyatla.nyartoolkit.core.types.matrix.NyARDoubleMatrix44;
import jp.nyatla.nyartoolkit.markersystem.NyARMarkerSystemConfig;
import android.content.res.AssetManager;
import android.hardware.Camera;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

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
	CameraPreview _camera_preview;
	AndGLView _glv;
	Camera.Size _cap_size;
	int screen_w,screen_h;
	public ArrayList<InfoCircle> _infoCircle = new ArrayList<InfoCircle>();
	
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
	InfoCircleIlluminance roughness;
	InfoCircleTemperature temperature;
	
	public void setupGL(GL10 gl)
	{
		try {
			AssetManager assetMng = getResources().getAssets();
			//create sensor controller.
			this._ss=new NyARAndSensor(this._camera_preview,this._cap_size.width,this._cap_size.height,30);
			//create marker system
			this._ms=new NyARAndMarkerSystem(new NyARMarkerSystemConfig(this._cap_size.width,this._cap_size.height));
			this._mid=this._ms.addARMarker(assetMng.open("AR/data/hiro.pat"),16,25,80);
			this._ss.start();
			//setup openGL Camera Frustum
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadMatrixf(this._ms.getGlProjectionMatrix(),0);
			this._debug=new AndGLDebugDump(this._glv);
			
			this._planeCoord = new NyARDoubleMatrix44();
			this._planeCoord.setTranslate(0, 0, 0);
			//this._planeCoord.rotateX(Math.PI*90/180);
			double dcoord[] = new double[16];
			
			this._planeCoord.getValueT(dcoord);
			
			toFloatValues(dcoord, coord);
			
			this.humidity = new InfoCircleHumidity(this._glv, this, 128, 128, 0, 20);		
			this.roughness = new InfoCircleIlluminance(this._glv, this, 128, 128, 0, 20);
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

					this.temperature.draw(0, 270, -60);
					this.humidity.draw(-70, 150, -60);
					this.roughness.draw(70, 150, -60);
					
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
}
