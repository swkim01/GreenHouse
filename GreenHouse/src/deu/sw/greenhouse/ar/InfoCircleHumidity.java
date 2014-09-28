package deu.sw.greenhouse.ar;

import java.util.ArrayList;

import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
import android.graphics.Color;
import deu.sw.greenhouse.R;
import deu.sw.greenhouse.graph.TimeValue;

public class InfoCircleHumidity extends Circle{
	
	public String infoText;
	public InfoCircleHumidity(AndGLView i_context, IGLPlane plane,
			float i_size_x, float i_size_y, float i_size_z, int textSize) {	
		super(i_context, plane, i_size_x, i_size_y, i_size_z, textSize);
		infoText = "";
		setBitmap(R.drawable.humidityoff, R.drawable.humidityon);
		setTag("info");
		setSubTag("Humidity");
	}
	
	public void draw(float i_x,float i_y,float i_z)
	{
		Boolean state;
		state = this.getButtonState();
		setCanvasBitmap(state);
		
		super.draw(i_x, i_y, i_z, infoText);
	}
	
	public void setText(String text)
	{
		this.infoText = text;
	}
}
