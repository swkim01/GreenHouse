package deu.sw.greenhouse.graph;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;

public class GraphToBitmap {
	Paint pBaseLine;
	Paint pBaseLineX;
	Paint pCircle;
	Paint pMarkText;
	Paint pValueLine;
	int color;
	Path path;
	int _x = 30;
	int _y = 20;
	int maxValue;
	String yLine_S;
	ArrayList<TimeValue> timeValue;
	
	public GraphToBitmap(String yLine_S, int y_MaxValue, int color, ArrayList<TimeValue> timeValue) {
		this.color = color;
		this.maxValue = y_MaxValue;
		this.timeValue = timeValue;
		setPaint();
		this.yLine_S = yLine_S;
		//CreateGraph();
	}
	
	private void setPaint() {
		pBaseLine = new Paint();//그래프 틀 선
		pBaseLine.setFlags(Paint.ANTI_ALIAS_FLAG);
		pBaseLine.setAntiAlias(true); //text anti alias
		pBaseLine.setFilterBitmap(true); // bitmap anti alias
		pBaseLine.setColor(Color.GRAY);
		pBaseLine.setStrokeWidth(3);
		
		pBaseLineX = new Paint();//x축 점선
		pBaseLineX.setFlags(Paint.ANTI_ALIAS_FLAG);
		pBaseLineX.setAntiAlias(true); //text anti alias
		pBaseLineX.setFilterBitmap(true); // bitmap anti alias
		pBaseLineX.setColor(0xffcccccc);
		pBaseLineX.setStrokeWidth(3);
		pBaseLineX.setStyle(Style.STROKE);
		pBaseLineX.setPathEffect(new DashPathEffect(new float[] {10,5}, 0));
		
		pCircle = new Paint();
		pCircle.setFlags(Paint.ANTI_ALIAS_FLAG);
		pCircle.setAntiAlias(true); //text anti alias
		pCircle.setFilterBitmap(true); // bitmap anti alias
		pCircle.setColor(color);
		pCircle.setStrokeWidth(3);
		pCircle.setStyle(Style.FILL_AND_STROKE);
		
		pMarkText = new Paint();
		pMarkText.setFlags(Paint.ANTI_ALIAS_FLAG);
		pMarkText.setAntiAlias(true); //text anti alias
		pMarkText.setColor(Color.BLACK); 
		pMarkText.setTextSize(20);
		
		pValueLine = new Paint();
		pValueLine.setStyle(Paint.Style.STROKE);
		pValueLine.setAntiAlias(true);
		pValueLine.setStrokeWidth(3);
		pValueLine.setColor(color);
		
		path = new Path();
	}
	
	public Bitmap CreateGraph() {
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inPurgeable = true;
		option.inDither = true;
		option.inTempStorage = new byte[16*1024];
		
		Bitmap bitmap = Bitmap.createBitmap(720,330,Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.TRANSPARENT);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.argb(5, 250, 244, 192));
		
		for (int i=0; i<5; i++) { //y축 베이스
			int max 				= (int)(this.maxValue*((float)(5-i)/5));
			int maxValueWidth		= (int)Math.ceil(this.pMarkText.measureText(String.valueOf(max)));
			int maxValueHeight	= (int)Math.ceil(this.pMarkText.ascent()+this.pMarkText.descent());
			canvas.drawLine(100-_x, 60+((250/5)*i)-_y, 720-_x, 60+((250/5)*i-_y), pBaseLineX);
			canvas.drawLine(100-_x, 60+((250/5)*i)-_y, 90-_x, 60+((250/5)*i-_y), pBaseLine);
			canvas.drawText(String.valueOf(max), 80-maxValueWidth-_x, 60+((250/5)*i)-maxValueHeight/2-_y, pMarkText);
		}
			canvas.drawLine(100-_x, 50-_y, 100-_x, 310-_y, pBaseLine);//y축 베이스
		//////////////////////////////////////////////////////////////////////////
			canvas.drawLine(100-_x, 310-_y, 720-_x, 310-_y, pBaseLine);//x축 베이스
			
		for(int i=1; i<9; i++) {
			canvas.drawLine(100+(600*i/8)-_x, 310-_y, 100+(600*i/8)-_x, 320-_y, pBaseLine);
		}
//		/////////////////////////////////////////////////////////////////////////////	
//		
		//////////////////////////////////////////////////////////ddddddddddddd
//		for(int i=1; i<9; i++)
//		{
//			String timeText = String.valueOf(timeValue.get(3*i-1).getTime());
//			int textWidth = (int)Math.ceil(this.pMarkText.measureText(timeText));
//			canvas.drawText(timeText, 100+(600*i/8)-(textWidth/2)-_x, 340-_y, pMarkText);
//		}
		//////////////////////////////////////////////////////////fffffffffffffff
		
		//String timeText = String.valueOf(timeValue.get(3*1-1).getTime());
		//int timeText =8;
		String timeText = "8";
		int textWidth = (int)Math.ceil(this.pMarkText.measureText(timeText));
		canvas.drawText(timeText, 100+(600*1/8)-(textWidth/2)-_x, 340-_y, pMarkText);
		
		timeText = "11";
		textWidth = (int)Math.ceil(this.pMarkText.measureText(timeText));
		canvas.drawText(timeText, 100+(600*2/8)-(textWidth/2)-_x, 340-_y, pMarkText);
		
		timeText = "14";
		textWidth = (int)Math.ceil(this.pMarkText.measureText(timeText));
		canvas.drawText(timeText, 100+(600*3/8)-(textWidth/2)-_x, 340-_y, pMarkText);
		
		timeText = "17";
		textWidth = (int)Math.ceil(this.pMarkText.measureText(timeText));
		canvas.drawText(timeText, 100+(600*4/8)-(textWidth/2)-_x, 340-_y, pMarkText);
		
		timeText = "20";
		textWidth = (int)Math.ceil(this.pMarkText.measureText(timeText));
		canvas.drawText(timeText, 100+(600*5/8)-(textWidth/2)-_x, 340-_y, pMarkText);
		
		timeText = "23";
		textWidth = (int)Math.ceil(this.pMarkText.measureText(timeText));
		canvas.drawText(timeText, 100+(600*6/8)-(textWidth/2)-_x, 340-_y, pMarkText);
		
		timeText = "2";
		textWidth = (int)Math.ceil(this.pMarkText.measureText(timeText));
		canvas.drawText(timeText, 100+(600*7/8)-(textWidth/2)-_x, 340-_y, pMarkText);
		
		timeText = "5";
		textWidth = (int)Math.ceil(this.pMarkText.measureText(timeText));
		canvas.drawText(timeText, 100+(600*8/8)-(textWidth/2)-_x, 340-_y, pMarkText);

		canvas.drawText(yLine_S, 100-_x-((int)Math.ceil(this.pMarkText.measureText("L%"))/2), 40-_y, pMarkText);
		canvas.drawText("h", 730-_x, 310-_y-((int)Math.ceil(this.pMarkText.ascent())+(int)Math.ceil(this.pMarkText.descent()))/2, pMarkText);
		
		int size = timeValue.size();
		if (size == 0) return bitmap;
		
		path.moveTo(100+((float)600*1/24)-_x, 310-_y-(timeValue.get(0).getValue()*((float)250/maxValue)));
		canvas.drawCircle(100+((float)600*1/24)-_x, 310-_y-(timeValue.get(0).getValue()*((float)250/maxValue)), 4, pCircle);

		for (int i=1; i<size; i++) {
			path.lineTo(125+(int)((float)600*i/24)-_x, 310-_y-(timeValue.get(i).getValue()*((float)250/maxValue)));
			canvas.drawCircle(125+((float)600*i/24)-_x, 310-_y-(timeValue.get(i).getValue()*((float)250/maxValue)), 4, pCircle);
		}
		canvas.drawPath(path, pValueLine);
		
		return bitmap;
	}

}
