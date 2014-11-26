package deu.sw.greenhouse.ar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import jp.androidgroup.nyartoolkit.utils.gl.AndGLHelper;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLUtils;
import android.util.Log;
import deu.sw.greenhouse.activity.ARPreviewActivity;
import deu.sw.greenhouse.graph.GraphToBitmap;
import deu.sw.greenhouse.graph.TimeValue;


/**
 * 그래프를 표시
 * 
 */
public class GraphSquare implements AndGLView.IGLViewEventListener
{
	private GL10 gl;
	AndGLView i_context;
	private FloatBuffer vertexBuffer;
	private FloatBuffer texBuffer;
	private ByteBuffer indexBuffer;
	private byte[] index;
	private Paint paint;
	int[] textureIDs;
	private Canvas canvas;
	private Canvas graphCanvas;
	private boolean state;
	float[] vertices;
	float[] texCoords;
	private int tag;
	private int _width;
	private int _height;
	private float trans_x;
	private float trans_y;
	private float trans_z;
	private String yLine_S;
	private IGLPlane _plane;
	
	private Bitmap testbitmap;
	private Bitmap bitmapBack;
	private ARPreviewActivity.GraphClickEventListener _click;
	//private Paint pBaseLine = new Paint();//그래프 틀 선
	
	
	public GraphSquare(AndGLView i_context, IGLPlane plane, String yLine_S, float i_size_x, float i_size_y, float i_size_z)
	{
		i_context._evl.add(this);
		this.i_context = i_context;
		this.state = false;
		this.yLine_S = yLine_S;
		this.paint = new Paint();
		this.paint.setAntiAlias(true);
		
		this.textureIDs = new int[1];
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

		this.texCoords = new float[] { //면에 대한 텍스처 좌표									//
				   0.0f, 1.0f,       //좌측 하단												   //
				   1.0f, 1.0f,      //우측 하단												  //
				   0.0f, 0.0f,     //좌측 상단												 //
				   1.0f, 0.0f     //우측 상단	   												//
				   };																		   //		
		this.texBuffer=AndGLHelper.makeFloatBuffer(texCoords);
		
		//pBaseLine = new Paint();//그래프 틀 선
	//	pBaseLine.setFlags(Paint.ANTI_ALIAS_FLAG);
		//pBaseLine.setAntiAlias(true); //text anti alias
	//	pBaseLine.setFilterBitmap(true); // bitmap anti alias
	//	pBaseLine.setColor(Color.GRAY);
	//	pBaseLine.setStrokeWidth(3);
	//	
		this._plane = plane;
		plane.registerComponent(this);
	}
	
	public void setTag(int tag)
	{
		this.tag = tag;
	}
	
	public int getTag()
	{
		return this.tag;
	}
	
	public int getWidth()
	{
		return this._width;
	}	
	
	public int getHeight()
	{
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
	
	public boolean getState()
	{
		return this.state;
	}
	
	public void setState(boolean state)
	{
		this.state = state;
	}
	
	public void setOnClickListener(ARPreviewActivity.GraphClickEventListener click) {
		this._click = click;
	}
	
	public ARPreviewActivity.GraphClickEventListener getClickListener() {
		return this._click;
	}
	
	
	public void setGraph(int y_MaxValue, int color, ArrayList<TimeValue> timeValue)
	{
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inPurgeable = true;
		option.inDither = true;
		option.inTempStorage = new byte[16*1024];
		
		testbitmap = new GraphToBitmap(this.yLine_S, y_MaxValue, color, timeValue).CreateGraph();

		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		bitmapBack = Bitmap.createBitmap(800, 360, config);
		bitmapBack.eraseColor(Color.TRANSPARENT);
		canvas = new Canvas(bitmapBack);
		canvas.drawColor(Color.TRANSPARENT);
	}
	
	
	public void draw(float i_x, float i_y, float i_z)
	{
		//if(GraphToggle == )
		this.trans_x = i_x;
		this.trans_y = i_y;
		this.trans_z = i_z;
		
		canvas.drawBitmap(testbitmap, 0, 0, paint);
		//beginDrawing
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_DEPTH_TEST);
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
		gl.glVertexPointer( 3, GL10.GL_FLOAT, 0, vertexBuffer);			
		
		
		
		
		textureIDs[0] = loadBitmapIntoOpenGL(gl, bitmapBack);
		gl.glPushMatrix();
		gl.glTranslatef(i_x,i_y,i_z);
		gl.glDrawElements(GL10.GL_TRIANGLES, index.length, GL10.GL_UNSIGNED_BYTE, indexBuffer);
		gl.glPopMatrix();

		//endDrawing
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		
	}
	
	private int loadBitmapIntoOpenGL(GL10 gl, Bitmap bitmap) {
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
	
	public Bitmap resizeBitmapImage(Bitmap source, int maxWidth, int maxHeight)
	{
	    int width = source.getWidth();
	    int height = source.getHeight();
	    int newWidth = width;
	    int newHeight = height;
	    float rate = 0.0f;
	 
	    if(width > height)
	    {
	        if(maxWidth < width)
	        {
	            rate = maxWidth / (float) width;
	            newHeight = (int) (height * rate);
	            newWidth = maxWidth;
	        }
	    }
	    else
	    {
	        if(maxHeight < height)
	        {
	            rate = maxWidth / (float) height;
	            newWidth = (int) (width * rate);
	            newHeight = maxWidth;
	        }
	    }
	 
	    return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
	}

	@Override
	public void onGlChanged(GL10 i_gl, int i_width, int i_height) {
		if(this.gl!=null){
			this.gl.glDeleteTextures(1, textureIDs, 0);
		}
		this.gl=i_gl;
		
		gl.glGenTextures(1, textureIDs, 0);
	}

	@Override
	public void onGlMayBeStop() {
		this.gl.glDeleteTextures(1, textureIDs, 0);
		this.bitmapBack = null;
		this.gl=null;
	}
	
}