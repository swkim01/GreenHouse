package deu.sw.greenhouse.ar;

import deu.sw.greenhouse.R;
import jp.androidgroup.nyartoolkit.utils.gl.AndGLView;

public class LEDButton extends Circle{

	public LEDButton(AndGLView i_context, IGLPlane plane, float i_size_x,
			float i_size_y, float i_size_z, int textSize) {
		super(i_context, plane, i_size_x, i_size_y, i_size_z, textSize);
		// TODO Auto-generated constructor stub
		setBitmap(R.drawable.buttonoff, R.drawable.buttonon);
		setTag("led");
	}
	
	public void draw(float i_x, float i_y, float i_z)
	{
		Boolean state;
		state = this.getButtonState();
		setCanvasBitmap(state);
		super.draw(i_x, i_y, i_z, "");
	}
}