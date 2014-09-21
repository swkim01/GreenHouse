package deu.sw.greenhouse.ar;

import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;
import deu.sw.greenhouse.R;

public class InfoCircleIlluminance extends InfoCircle{
	
	public String infoText;
	
	public InfoCircleIlluminance(AndGLView i_context, IGLPlane plane,
			float i_size_x, float i_size_y, float i_size_z, int textSize) {
		super(i_context, plane, i_size_x, i_size_y, i_size_z, textSize);
		infoText = "";
		setBitmap(R.drawable.roughnessoff, R.drawable.roughnesson);
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
