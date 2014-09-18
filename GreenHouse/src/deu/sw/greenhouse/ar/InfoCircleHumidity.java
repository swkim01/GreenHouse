package deu.sw.greenhouse.ar;

import java.util.ArrayList;
import java.util.List;

import com.handstudio.android.hzgrapherlib.animation.GraphAnimation;
import com.handstudio.android.hzgrapherlib.graphview.CurveGraphView;
import com.handstudio.android.hzgrapherlib.vo.GraphNameBox;
import com.handstudio.android.hzgrapherlib.vo.curvegraph.CurveGraph;
import com.handstudio.android.hzgrapherlib.vo.curvegraph.CurveGraphVO;

import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
import jp.nyatla.nyartoolkit.and.R;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.SurfaceView;
import android.view.View;

public class InfoCircleHumidity extends InfoCircle{
	
	public String infoText;
		
	public InfoCircleHumidity(AndGLView i_context, IGLPlane plane,
			float i_size_x, float i_size_y, float i_size_z, int textSize) {	
		super(i_context, plane, i_size_x, i_size_y, i_size_z, textSize);
		infoText = "";
		setBitmap(R.drawable.humidityoff, R.drawable.humidityon);
	}
	
	public void draw(float i_x,float i_y,float i_z)
	{
		Boolean state;
		//String infoText;
		
		state = this.getButtonState();
		//infoText = ValtoString(12.5)+"%";
		
		setCanvasBitmap(state);

		super.draw(i_x, i_y, i_z, infoText);
	}
	
	public void setText(String text)
	{
		this.infoText = text;
	}
}
