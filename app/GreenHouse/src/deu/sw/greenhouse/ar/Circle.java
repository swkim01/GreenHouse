package deu.sw.greenhouse.ar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import jp.androidgroup.nyartoolkit.utils.gl.AndGLHelper;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.opengl.GLUtils;
import android.util.Log;
import deu.sw.greenhouse.activity.ARPreviewActivity;


/**
 * 정보 원의 표시
 * 
 */
public class Circle implements AndGLView.IGLViewEventListener
{
	private static final String TAG = "InfoBox";
	
	private GL10 gl;
	AndGLView i_context;
	private FloatBuffer vertexBuffer;
	private FloatBuffer texBuffer;
	private ByteBuffer indexBuffer;
	
	private IGLPlane _plane;
	private ARPreviewActivity.InfoCircleClickEventListener _click;
	private byte[] index;
	
	private Paint paint;
	
	int[] textureIDs;
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
	private String tag;
	private String subtag;
	
	protected Circle(AndGLView i_context, IGLPlane plane, float i_size_x, float i_size_y, float i_size_z, int textSize)
	{
		i_context._evl.add(this);
		this.i_context = i_context;
		
		this.paint=new Paint();
		this.paint.setTextSize(textSize);
		this.paint.setAntiAlias(true);
		this.paint.setARGB(0xff, 0xff0, 0xff, 0xff);
		
		this.textureIDs = new int[1]; //1텍스처-ID 에 대한 배열
		this._width 	= (int)i_size_x;
		this._height	= (int)i_size_y;
		
		
		this.vertices = new float[] {
					-i_size_x/2, -i_size_y/2, i_size_z,	//0.Left Bottom
			    	 i_size_x/2, -i_size_y/2, i_size_z,	//1.Right Bottom
			    	-i_size_x/2,  i_size_y/2, i_size_z,	//2.Left Top
			    	 i_size_x/2,  i_size_y/2, i_size_z };	//3.Right Top
		this.vertexBuffer=AndGLHelper.makeFloatBuffer(vertices);
								
		this.index = new byte[] {
				2, 3, 1,
				2, 1, 0
		};
		this.indexBuffer=AndGLHelper.makeByteBuffer(index);
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
		
		//gl.glTranslatef(0,270-50,0);
	}
	
	public void setBitmap(int resourceId1, int resourceId2)
	{
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inPurgeable = true;
		option.inDither = true;
		option.inTempStorage = new byte[16*1024];
		bitmapOff = BitmapFactory.decodeResource(i_context.getResources(), resourceId1, option);
		bitmapOn = BitmapFactory.decodeResource(i_context.getResources(), resourceId2, option);
		
		int imgSize_w = bitmapOff.getWidth();
		int imgSize_h = bitmapOff.getHeight();
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		bitmapBack = Bitmap.createBitmap(imgSize_w, imgSize_h, config);
		bitmapBack.eraseColor(Color.TRANSPARENT);
		canvas = new Canvas(bitmapBack);	
	}
	
	public void setTag(String tag)
	{
		this.tag = tag;
	}
	
	public void setSubTag(String subtag)
	{
		this.subtag = subtag;
	}
	
	public String getTag()
	{
		return this.tag;
	}
	
	public String getSubTag()
	{
		return this.subtag;
	}
	
	/**
	 * 현재 도형의 토글이 ON인지 OFF인지 에따라 비트맵을 다르게 그림
	 * @param state
	 */
	public void setCanvasBitmap(Boolean state) 
	{
		if(state == false)
			canvas.drawBitmap(bitmapOff, 0, 0, paint);
		else
			canvas.drawBitmap(bitmapOn, 0, 0, paint);
	}
	
	
	/**
	 * 메트릭스 모드를 MODEL_VIEW로 변경하고 그림을 그리는 작업을한다.
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
			setText(i_context, gl, text);
			gl.glPushMatrix();
			gl.glTranslatef(i_x,i_y,i_z);
			gl.glDrawElements(GL10.GL_TRIANGLES, index.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
			gl.glPopMatrix();
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
		
		gl.glGenTextures(1, textureIDs, 0);
	}
	
	@Override
	public void onGlMayBeStop()
	{
		this.gl.glDeleteTextures(1, textureIDs, 0);
		this.bitmapOff = null;
		//this.bitmapOff.recycle();
		this.bitmapOn = null;
		//this.bitmapOn.recycle();
		this.bitmapBack = null;
		//this.bitmapBack.recycle();
		this.gl=null;
	}
	
 	/**
 	 * 도형에 텍스트를 그린다. 
 	 * @param i_context
 	 * @param gl
 	 * @param text
 	 */
	protected void setText(AndGLView i_context, GL10 gl, String text) {
		String infoText = text;

		int imgSize_w = bitmapOff.getWidth();
		int imgSize_h = bitmapOff.getHeight();
		int text_w;
		int text_h;

		text_w = (int)Math.ceil(this.paint.measureText(infoText));
		text_h = (int)Math.ceil(this.paint.ascent()+this.paint.descent());
		canvas.drawText(infoText, (int)Math.ceil(imgSize_w/2)-(text_w/2), (int)Math.ceil(imgSize_h/2)-(text_h/2), paint);
		
		Paint pBaseLineX = new Paint();
		
		pBaseLineX = new Paint();//x축 점선
		pBaseLineX.setAntiAlias(true); //text anti alias
		pBaseLineX.setColor(Color.RED);
		pBaseLineX.setStrokeWidth(3);
		pBaseLineX.setStyle(Style.STROKE);
		
		
		textureIDs[0] = loadBitmapIntoOpenGL(gl, bitmapBack);
	}
	
	/**
	 * 텍스처를 Circle에 바인딩
	 * @param gl
	 * @param bitmap
	 * @return
	 */
	protected int loadBitmapIntoOpenGL(GL10 gl, Bitmap bitmap) {
		int textureName = -1;
		
		if(gl != null) {
			if(textureIDs == null)
			{
				gl.glGenTextures(1, textureIDs, 0);
			}
			
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
	
	/**
	 * 그리기 시작하기위한 설정
	 * @param gl
	 */
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
	}
	
	/**
	 * 그리기 완료후 버퍼 사용해제와 설정들
	 * @param gl
	 */
	public void endDrawing(GL10 gl)
	{
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_DEPTH_TEST);
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