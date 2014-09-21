package com.example.android.opengl;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;

public class GLTexture {
	private Context mContext;
	private int width;
	private int height;
	private int mTextureID;
	private float[] mvpMatrix;
	private float[] offset = new float[3];
	
	public GLTexture(Context context) {
		this.mContext = context;
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public void setOffset(float x, float y, float z) {
		this.offset[0] = x;
		this.offset[1] = y;
		this.offset[2] = z;
	}
	
	public float getOffsetX() {
		return this.offset[0];
	}
	public float getOffsetY() {
		return this.offset[1];
	}
	public float getOffsetZ() {
		return this.offset[2];
	}

}
