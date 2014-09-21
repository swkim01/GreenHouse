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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    public MyGLRenderer(Context context) {
		super();
		// TODO Auto-generated constructor stub
		this.context = context;
		this.mSurface = ((OpenGLES20Activity) context).getSurfaces();
	}

    private Context context;
	private static final String TAG = "MyGLRenderer";
    private PlaneShader mPlane;
    private Surface[]  mSurface = null;
    private SurfaceTexture[] surfaceTexture = new SurfaceTexture[OpenGLES20Activity.NUM_TEXTURES];

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    private float mAngle;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
    	
    	final String extensions = GLES20.glGetString( GLES20.GL_EXTENSIONS );
    	Log.d( "GLES20Ext", extensions );

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //mTriangle = new Triangle();
        //mSquare   = new Square();
        mPlane   = new PlaneShader(context);
    	for (int i=0; i < OpenGLES20Activity.NUM_TEXTURES; i++)
    		surfaceTexture[i] = null;

    }
    
    float[] scratch = new float[16];
    float[] transMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 unused) {
    	for (int i=0; i < OpenGLES20Activity.NUM_TEXTURES; i++) {
    		if (surfaceTexture[i] != null) {
            	synchronized (this) {
            		surfaceTexture[i].updateTexImage(); // update texture
            	}
    		}
    	}
       // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -10, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Create a rotation for the plane views

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);

        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, 1.0f);
        
    	for (int i=0; i < OpenGLES20Activity.NUM_TEXTURES; i++) {
     
	    	GLTexture texture = ((OpenGLES20Activity) context).getTexture(i);

            // Combine the rotation matrix with the projection and camera view
            // Note that the mMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            Matrix.multiplyMM(transMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            
    		Matrix.translateM(scratch, 0, transMatrix, 0, texture.getOffsetX(), texture.getOffsetY(), texture.getOffsetZ());


            // Draw triangle
            //mTriangle.draw(scratch);
        		mPlane.draw(scratch, i);
    	}
    }
    
    int glSurfaceTex;

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
    	for (int i=0; i < OpenGLES20Activity.NUM_TEXTURES; i++) {
    		mSurface[i] = null;
    	    surfaceTexture[i] = null;
    	    	
    	    GLTexture texture = ((OpenGLES20Activity) context).getTexture(i);
    	    glSurfaceTex = Engine_CreateSurfaceTexture(i, texture.getWidth(), texture.getHeight());
    	    if ( glSurfaceTex > 0 ) {
    	    	surfaceTexture[i] = new SurfaceTexture( glSurfaceTex );
    	    	surfaceTexture[i].setDefaultBufferSize( texture.getWidth(), texture.getHeight() );
    	    	mSurface[i] = new Surface( surfaceTexture[i] );
    	    	((OpenGLES20Activity) context).setTextureID(i, glSurfaceTex);
    	    }
    	}
    	
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 20);
    }
    
    int[] glTextureID = new int[OpenGLES20Activity.NUM_TEXTURES];
    
    int Engine_CreateSurfaceTexture(int id, int width, int height ) {
    	
    	GLES20.glGenTextures( 1, glTextureID, id);
    	if ( glTextureID[id] > 0 ) {
    		GLES20.glBindTexture( GLES11Ext.GL_TEXTURE_EXTERNAL_OES, glTextureID[id] );
    		
    		// Notice the use of GL_TEXTURE_2D for texture creation
    		//GLES20.glTexImage2D( GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, 0 );
    		
    		GLES20.glTexParameteri( GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
    		GLES20.glTexParameteri( GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );
    		
    		GLES20.glTexParameteri( GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST );
    		GLES20.glTexParameteri( GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST );
    		
    		GLES20.glBindTexture( GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0 );
    	}
    	
    	return glTextureID[id];
    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

}