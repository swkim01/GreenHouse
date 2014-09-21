/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.opengl;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

public class OpenGLES20Activity extends Activity {

	private GLSurfaceView mGLView;
	
	// Fixed values
	//public final static int TEXTURE_WIDTH     = ( 480 );
	//public final static int TEXTURE_HEIGHT    = ( 480 );
	
	// Variables
	final static int NUM_TEXTURES = 2;
	public Surface[] surface = new Surface[NUM_TEXTURES];
	public int[] textureID = new int[NUM_TEXTURES];
	public GLTexture[] mTexture = new GLTexture[NUM_TEXTURES];
	
	public Surface[] getSurfaces() {
		return this.surface;
	}
	
	public Surface getSurface(int index) {
		return this.surface[index];
	}
	
	public void setSurface(int index, Surface surface) {
		this.surface[index] = surface;
	}
	
	public int getTextureID(int index) {
		return this.textureID[index];
	}
	
	public void setTextureID(int index, int id) {
		this.textureID[index] = id;
	}
	
	public GLTexture getTexture(int index) {
		return this.mTexture[index];
	}
    
    class CustomWebView extends WebView {
    	
    	private int surfaceIndex;
    	
    	public CustomWebView( Context context, int index ) {
    		super( context ); // Call WebView's constructor
    		
    		setWebChromeClient( new WebChromeClient(){} );
    		setWebViewClient( new WebViewClient() );
    		
    		this.surfaceIndex = index;
    		
    		//setLayoutParams( new ViewGroup.LayoutParams( TEXTURE_WIDTH, TEXTURE_HEIGHT ) );
    	}
    	
    	@Override
    	protected void onDraw( Canvas canvas ) {
    		if ( surface[surfaceIndex] != null ) {
    			
    			// Requires a try/catch for .lockCanvas( null )
    			try {
    				final Canvas surfaceCanvas = surface[surfaceIndex].lockCanvas( null ); // Android canvas from surface
    				super.onDraw( surfaceCanvas ); // Call the WebView onDraw targetting the canvas
    				surface[surfaceIndex].unlockCanvasAndPost( surfaceCanvas ); // We're done with the canvas!
    			} catch ( OutOfResourcesException excp ) {
    				excp.printStackTrace();
    			}
    		}
    		// super.onDraw( canvas ); // <- Uncomment this if you want to show the original view
    	}
    	
    }
    
    public class CustomImageView extends ImageView {
    	
    	private int surfaceIndex;
    	
    	public CustomImageView(Context context, int index) {
    		super(context);
    		// TODO Auto-generated constructor stub
    		this.surfaceIndex = index;
    		
    		//setLayoutParams( new ViewGroup.LayoutParams( OpenGLES20Activity.TEXTURE_WIDTH, OpenGLES20Activity.TEXTURE_HEIGHT ) );
    	}

    	@Override
    	protected void onDraw(Canvas canvas) {
    		// TODO Auto-generated method stub
    		if ( surface[surfaceIndex] != null ) {
    			// Requires a try/catch for .lockCanvas( null )
    			try {
    				final Canvas surfaceCanvas = surface[surfaceIndex].lockCanvas( null ); // Android canvas from surface
    				super.onDraw( surfaceCanvas ); // Call the WebView onDraw targetting the canvas
    				surface[surfaceIndex].unlockCanvasAndPost( surfaceCanvas ); // We're done with the canvas!
    			} catch ( OutOfResourcesException excp ) {
    				excp.printStackTrace();
    			}
        		//super.onDraw( canvas ); // <- Uncomment this if you want to show the original view
        	}
    	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
        
        for (int i = 0; i < NUM_TEXTURES; i++) {
            mTexture[i] = new GLTexture((Context)this);
        }
        mTexture[0].setSize(240, 240); // for ImageView
        mTexture[0].setOffset(0.0f, 2.5f, 0.0f);
        mTexture[1].setSize(480, 480); // for WebView
        CustomImageView myImageView = new CustomImageView (this, 0);
        CustomWebView myWebView = new CustomWebView (this, 1);
        
		//myImageView.setBackgroundResource(R.drawable.temperatureon);
		myImageView.setImageResource(R.drawable.temperatureon);
        
        //myWebView.loadUrl("http://www.daum.net");
		myWebView.getSettings().setJavaScriptEnabled(true);
		ArrayList<Float> series = new ArrayList<Float>();
		for(int i=0; i<12; i++)
			series.add((float)Math.random()*20);
		createChart("examples/line-basic/index.htm", series);
        //myWebView.loadUrl("file:///android_asset/examples/line-basic/index.htm");
        myWebView.loadUrl("file:///"+ getPath() +"/"+"chart.htm");
        //myWebView.loadUrl("javascript:plot(" + "[3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]" + ")");
        
        setContentView(mGLView);
        addContentView(myImageView, new ViewGroup.LayoutParams( mTexture[0].getWidth(), mTexture[0].getHeight() ) );
        addContentView(myWebView, new ViewGroup.LayoutParams( mTexture[1].getWidth(), mTexture[1].getHeight() ) );
    }
    
	public static String getPath()
	{
		return Environment.getExternalStorageDirectory() + APP_PATH;
	}
    
	public static String APP_PATH = "/HIGHCHART";
    
    public void createChart(String fileName, ArrayList<Float> series){
		try {	
			InputStream is = null;			
			is = this.getAssets().open(fileName);
			
			StringBuffer sb = new StringBuffer();
			byte[] b = new byte[4096];
			for (int n; (n = is.read(b)) != -1;) {
				sb.append(new String(b, 0, n));
			}
			String source = sb.toString();
			String newHtm = source.replaceAll("#dataset1", setDatasetToString(series));
			
			final File dir = new File(getPath());
			if(!dir.exists()){
				if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
					dir.mkdirs();
				}
			}
			OutputStreamWriter osw = null;
			BufferedOutputStream buf = new BufferedOutputStream(new FileOutputStream(getPath()+"/chart.htm"));
			osw = new OutputStreamWriter(buf, "utf-8");
			osw.write(newHtm);
			osw.flush();
			buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public String setDatasetToString(ArrayList<Float> list){
		Iterator<Float> it = list.iterator();
		String tmp="";
		while(it.hasNext()){
			tmp += String.format("%.1f",it.next())+",";
		}
		
		if(tmp.length() < 1){
			tmp = "";
		}
		else{
			tmp = tmp.substring(0, tmp.length()-1); 
		}
		
		return tmp;
		
	}

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
}