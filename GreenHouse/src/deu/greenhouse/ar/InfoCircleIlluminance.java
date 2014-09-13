package deu.greenhouse.ar;

import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
import jp.nyatla.nyartoolkit.and.R;

public class InfoCircleIlluminance extends InfoCircle{
	public InfoCircleIlluminance(AndGLView i_context, IGLPlane plane,
			float i_size_x, float i_size_y, float i_size_z, int textSize) {
		super(i_context, plane, i_size_x, i_size_y, i_size_z, textSize);
		// TODO Auto-generated constructor stub
	}
	
	public void draw(float i_x,float i_y,float i_z)
	{
		Boolean state;
		int id;
		String infoText;
		
		state = this.getButtonState();
		infoText = DoubleValtoString(32.67)+"L%";
		
		if(state == false) {
			id = GetBitmapForResourceId(R.drawable.roughnessoff);
		}
		else {
			id = GetBitmapForResourceId(R.drawable.roughnesson);
		}
		 
		
		super.draw(i_x, i_y, i_z, id, infoText);
	}
}
