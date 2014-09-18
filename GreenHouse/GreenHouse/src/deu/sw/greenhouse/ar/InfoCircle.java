package deu.sw.greenhouse.ar;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import deu.sw.greenhouse.activity.ARPreviewActivity;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLHelper;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLUtils;
import android.util.Log;


/**
 * 입방체의 표시 클래스
 * 
 */
public class InfoCircle implements AndGLView.IGLViewEventListener
{
	private static final String TAG = "InfoBox";
	
	private GL10 gl;
	AndGLView i_context;
	private FloatBuffer vertexBuffer;
	private FloatBuffer texBuffer;
	
	
	private IGLPlane _plane;
	private ARPreviewActivity.InfoCircleClickEventListener _click;
	
	
	
	
	private Paint textPaint;
	
	int[] textureIDs;
	//Canvas canvas;
	private Bitmap bitmapOff;
	private Bitmap bitmapOn;
	private Bitmap bitmapBack;
	private Canvas canvas;
	float[] vertices;
	float[] texCoords;
	
	private int _width;
	private int _height;
	private float trans_x;
	private float trans_y;
	private float trans_z;
	private Boolean state = false;
	
	protected InfoCircle(AndGLView i_context, IGLPlane plane, float i_size_x, float i_size_y, float i_size_z, int textSize)
	{
		i_context._evl.add(this);
		this.i_context = i_context;
		this.textPaint=new Paint();
		this.textPaint.setTextSize(textSize);
		this.textPaint.setAntiAlias(true);
		this.textPaint.setARGB(0xff, 0xff0, 0xff, 0xff);
		this.textureIDs = new int[1]; //1텍스처-ID 에 대한 배열
		this._width 	= (int)i_size_x;
		this._height	= (int)i_size_y;
		
		
		this.vertices = new float[] {
					-i_size_x/2, -i_size_y/2, i_size_z,
			    	 i_size_x/2, -i_size_y/2, i_size_z,
			    	-i_size_x/2,  i_size_y/2, i_size_z,
			    	 i_size_x/2,  i_size_y/2, i_size_z };
		this.vertexBuffer=AndGLHelper.makeFloatBuffer(vertices);
																									  //
																									 //
		this.texCoords = new float[] { //면에 대한 텍스처 좌표									//
				   0.0f, 1.0f,       //좌측 하단												   //
				   1.0f, 1.0f,      //우측 하단												  //
				   0.0f, 0.0f,     //좌측 상단												 //
				   1.0f, 0.0f     //우측 상단	   												//
				   };																		   //
		this.texBuffer=AndGLHelper.makeFloatBuffer(texCoords);
		
		this.bitmapOn = null;
		this.bitmapOff = null;
		
		this._plane = plane;
		plane.registerComponent(this);
	}
	
	public void setBitmap(int resourceId1, int resourceId2)
	{
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inPurgeable = true;
		option.inDither = true;
		option.inTempStorage = new byte[16*1024];
		bitmapOff = BitmapFactory.decodeResource(i_context.getResources(), resourceId1, option);
		bitmapOn = BitmapFactory.decodeResource(i_context.getResources(), resourceId2, option);
		
		int imgSize_w = bitmapOff.getWidth();//279;
		int imgSize_h = bitmapOff.getHeight();//168;
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		bitmapBack = Bitmap.createBitmap(imgSize_w, imgSize_h, config);
		bitmapBack.eraseColor(Color.TRANSPARENT);
		canvas = new Canvas(bitmapBack);
		
	}
	
	/////////////////////test//////////////////
	public void setBitmap(Bitmap bitmap1, Bitmap bitmap2)
	{
		bitmapOff 	= bitmap1;
		bitmapOn	= bitmap2;

		int imgSize_w = bitmapOff.getWidth();
		int imgSize_h = bitmapOff.getHeight();
		
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		bitmapBack = Bitmap.createBitmap(imgSize_w, imgSize_h, config);
		bitmapBack.eraseColor(Color.TRANSPARENT);
		canvas = new Canvas(bitmapBack);
	}
	
	
	//////////////////////test/////////////////
	
	
	public void setCanvasBitmap(Boolean state) 
	{
		if(state == false)
			canvas.drawBitmap(bitmapOff, 0, 0, textPaint);
		else
			canvas.drawBitmap(bitmapOn, 0, 0, textPaint);
	}
	
	
	/**
	 * This function changes the matrix mode to MODEL_VIEW , and change some parameter.
	 * @param i_x
	 * @param i_y
	 * @param i_z
	 */
	protected void draw(float i_x,float i_y,float i_z, String text)
	{
		this.trans_x = i_x;
		this.trans_y = i_y;
		this.trans_z = i_z;
		beginDrawing(gl);
			
			loadBitmap(i_context, gl, text);
			//canvas = new Canvas(bitmap);
			
			gl.glPushMatrix();
			gl.glTranslatef(i_x,i_y,i_z);
			//gl.glScalef(1.0f, 1.0f, 1.0f);//규모를 축소
			//gl.glRotatef(90, 1.0f, 0.0f, 0.0f); //축회전 1,1,1
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);
			gl.glPopMatrix();

			//drawText(gl);
			
		endDrawing(gl);
	}
	
	
	public int getWidth() {
		return this._width;
	}
	
	public int getHeight() {
		return this._height;
	}
	
	public float getTrans_X() {
		return this.trans_x;
	}
	
	public float getTrans_Y() {
		return this.trans_y;
	}
	
	public float getTrans_Z() {
		return this.trans_z;
	}
	
	@Override
	public void onGlChanged(GL10 i_gl, int i_width, int i_height)
	{
		if(this.gl!=null){
			this.gl.glDeleteTextures(1, textureIDs, 0);
		}
		this.gl=i_gl;
		//this._screen_width = i_width;
		//this._screen_height = i_height;
		
		gl.glGenTextures(1, textureIDs, 0);
	}
	
	@Override
	public void onGlMayBeStop()
	{
		this.gl.glDeleteTextures(1, textureIDs, 0);
		this.bitmapBack = null;
		this.bitmapBack.recycle();
		this.gl=null;
	}
	
	protected void loadBitmap(AndGLView i_context, GL10 gl, String text) {
		String infoText = text;

		int imgSize_w = bitmapOff.getWidth();//279;
		int imgSize_h = bitmapOff.getHeight();//168;
		int text_w;
		int text_h;

		String text919 = new String("29.11L%");
		text_w = (int)Math.ceil(this.textPaint.measureText(infoText));//텍스트의 넓이
		text_h = (int)Math.ceil(this.textPaint.ascent()+this.textPaint.descent());//텍스트 위아래 중심점부터 위 아래
		canvas.drawText(infoText, (int)Math.ceil(imgSize_w/2)-(text_w/2), (int)Math.ceil(imgSize_h/2)-(text_h/2), textPaint);
		
		textureIDs[0] = loadBitmapIntoOpenGL(gl, bitmapBack);
	}
	
	protected int loadBitmapIntoOpenGL(GL10 gl, Bitmap bitmap) {
		int textureName = -1;
		
		if(gl != null) {
			gl.glGenTextures(1, textureIDs, 0);
			
			textureName = textureIDs[0];
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);
		}
		
		gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
		
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			Log.e("Box", "비트맵 못읽어옴" + error);
		}
		
		return textureName;
	}
	
	//그리기 시작
	public void beginDrawing(GL10 gl)
	{
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
		gl.glVertexPointer( 3, GL10.GL_FLOAT, 0, vertexBuffer);	
		gl.glMatrixMode(GL10.GL_MODELVIEW);	
	}
	
	//그리기 종료
	public void endDrawing(GL10 gl)
	{
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
//		bitmapBack.recycle();
//		bitmapBack = null;
//		bitmap2.recycle();
//		bitmap2 = null;

		//canvas = null;
	}
	

	public Boolean getButtonState() {
		return this.state;
	}
	
	public void setButtonState(Boolean state) {
		this.state = state; 
	}
	
	public void setOnClickListener(ARPreviewActivity.InfoCircleClickEventListener click) {
		this._click = click;
	}
	
	public ARPreviewActivity.InfoCircleClickEventListener getClickListener() {
		return this._click;
	}

	public String ValtoString(double i_val)
	{
		String o_val = Double.toString(i_val);
		return o_val;
	}
	
	public String ValtoString(String i_val)
	{
		String o_val = i_val;
		return o_val;
	}
	
	public int GetBitmapForResourceId(int resourceId)
	{
		return resourceId;
	}
	
}