package com.mattikariluoma.cameratest;

import java.io.IOException;
import java.util.List;

import android.util.Log;
import android.content.Context;
import android.os.Build;


import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;

import android.view.View;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.opengl.GLSurfaceView;

public abstract class VersionedPreview extends ViewGroup
{
  private static final String TAG = "CameraTest.VersionedPreview";
  private static SurfaceHolder mHolder;
  private static SurfaceView mSurfaceView;
  private static VersionedCamera mCamera;
  private static Size mPreviewSize = null;
  private static List<Size> mSupportedPreviewSizes = null;
  
  public VersionedPreview(Context context)
  {
    super(context);
  }
  
  public static VersionedPreview newInstance(Context context) 
  {
    final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
    VersionedPreview detector = null;
    if (sdkVersion < Build.VERSION_CODES.DONUT)
      detector = new CupcakeDetected(context);
    else if (sdkVersion < Build.VERSION_CODES.ECLAIR)
      detector = new DonutDetected(context);
    else if (sdkVersion < Build.VERSION_CODES.FROYO)
      detector = new EclairDetected(context);
    else if (sdkVersion < Build.VERSION_CODES.GINGERBREAD)
      detector = new FroyoDetected(context);
    else 
      detector = new GingerbreadDetected(context);
    
    Log.d(TAG, "Created new " + detector.getClass());

    return detector;
  }

  public abstract void setCamera(VersionedCamera c);
  
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
  {
    final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
    final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    setMeasuredDimension(width, height);

    if (mSupportedPreviewSizes != null)
      mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
  }
  
  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b)
  {
    if (changed && getChildCount() > 0) 
    {
      final View child = getChildAt(0);

      final int width = r - l;
      final int height = b - t;
      int previewWidth = width;
      int previewHeight = height;
      
      if (mPreviewSize != null)
      {
        previewWidth = mPreviewSize.width;
        previewHeight = mPreviewSize.height;
      }

      // Center the child SurfaceView within the parent.
      if (width * previewHeight > height * previewWidth) 
      {
        final int scaledChildWidth = previewWidth * height / previewHeight;
        child.layout((width - scaledChildWidth) / 2, 0, (width + scaledChildWidth) / 2, height);
      } 
      else 
      {
        final int scaledChildHeight = previewHeight * width / previewWidth;
        child.layout(0, (height - scaledChildHeight) / 2, width, (height + scaledChildHeight) / 2);
      }
    }
  }
  
  private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) 
  {
    final double ASPECT_TOLERANCE = 0.1;
    double targetRatio = (double) w / h;
    if (sizes == null) return null;

    Size optimalSize = null;
    double minDiff = Double.MAX_VALUE;

    int targetHeight = h;

    // Try to find an size match aspect ratio and size
    for (Size size : sizes)
    {
      double ratio = (double) size.width / size.height;
      if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
        continue;
      if (Math.abs(size.height - targetHeight) < minDiff) 
      {
        optimalSize = size;
        minDiff = Math.abs(size.height - targetHeight);
      }
    }

    // Cannot find the one match the aspect ratio, ignore the requirement
    if (optimalSize == null) 
    {
      minDiff = Double.MAX_VALUE;
      for (Size size : sizes) 
        if (Math.abs(size.height - targetHeight) < minDiff) 
        {
          optimalSize = size;
          minDiff = Math.abs(size.height - targetHeight);
        }
    }
    
    return optimalSize;
  }

  private static class CupcakeDetected extends VersionedPreview
  {
    
    CupcakeDetected(Context context) 
    {
      super(context);

      mSurfaceView = new SurfaceView(context);
      addView(mSurfaceView);
      
      // Install a SurfaceHolder.Callback so we get notified when the
      // underlying surface is created and destroyed.
      mHolder = mSurfaceView.getHolder();
      mHolder.addCallback(surfaceChange);
      mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    @Override
    public void setCamera(VersionedCamera c)
    {
      mCamera = c;
      if (mCamera != null) 
      {
        mSupportedPreviewSizes = ((Camera.Parameters) mCamera.getParameters()).getSupportedPreviewSizes();
        requestLayout();
      }
    }

    /// Called when the holder is created/destroyed/changed
    private SurfaceHolder.Callback surfaceChange = new SurfaceHolder.Callback()
    {
      @Override
      public void surfaceCreated(SurfaceHolder holder) 
      {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        if (mCamera != null)
          try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(previewFrame);
          } catch (IOException e) {
            e.printStackTrace();
          }
      }
      
      @Override
      public void surfaceDestroyed(SurfaceHolder holder) 
      {
        if (mCamera != null)
          mCamera.stopPreview();
      }
      
      @Override
      public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) 
      {
        if (mCamera != null)
        {
          Camera.Parameters parameters = (Camera.Parameters) mCamera.getParameters();
          if (mPreviewSize != null)
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
          else
            parameters.setPreviewSize(w, h);
          
          mCamera.setParameters(parameters);
          mCamera.startPreview();
        }
      }
    };
    
    /// Called for each frame previewed
    private PreviewCallback previewFrame = new PreviewCallback()
    {
      @Override
      public void onPreviewFrame(byte[] data, Camera camera) 
      {
        Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
        CupcakeDetected.this.invalidate();
      }
    };
  }
  
  private static class DonutDetected extends VersionedPreview
  {
    
    DonutDetected(Context context) 
    {
      super(context);

      mSurfaceView = new SurfaceView(context);
      addView(mSurfaceView);
      
      // Install a SurfaceHolder.Callback so we get notified when the
      // underlying surface is created and destroyed.
      mHolder = mSurfaceView.getHolder();
      mHolder.addCallback(surfaceChange);
      mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    @Override
    public void setCamera(VersionedCamera c)
    {
      mCamera = c;
      if (mCamera != null) 
      {
        mSupportedPreviewSizes = ((Camera.Parameters) mCamera.getParameters()).getSupportedPreviewSizes();
        requestLayout();
      }
    }

    /// Called when the holder is created/destroyed/changed
    private SurfaceHolder.Callback surfaceChange = new SurfaceHolder.Callback()
    {
      @Override
      public void surfaceCreated(SurfaceHolder holder) 
      {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        if (mCamera != null)
          try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(previewFrame);
          } catch (IOException e) {
            e.printStackTrace();
          }
      }
      
      @Override
      public void surfaceDestroyed(SurfaceHolder holder) 
      {
        if (mCamera != null)
          mCamera.stopPreview();
      }
      
      @Override
      public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) 
      {
        if (mCamera != null)
        {
          Camera.Parameters parameters = (Camera.Parameters) mCamera.getParameters();
          if (mPreviewSize != null)
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
          else
            parameters.setPreviewSize(w, h);
          
          mCamera.setParameters(parameters);
          mCamera.startPreview();
        }
      }
    };
    
    /// Called for each frame previewed
    private PreviewCallback previewFrame = new PreviewCallback()
    {
      @Override
      public void onPreviewFrame(byte[] data, Camera camera) 
      {
        Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
        DonutDetected.this.invalidate();
      }
    };
  }
  
  private static class EclairDetected extends VersionedPreview
  {
    
    EclairDetected(Context context) 
    {
      super(context);

      mSurfaceView = new SurfaceView(context);
      addView(mSurfaceView);
      
      // Install a SurfaceHolder.Callback so we get notified when the
      // underlying surface is created and destroyed.
      mHolder = mSurfaceView.getHolder();
      mHolder.addCallback(surfaceChange);
      mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    @Override
    public void setCamera(VersionedCamera c)
    {
      mCamera = c;
      if (mCamera != null) 
      {
        mSupportedPreviewSizes = ((Camera.Parameters) mCamera.getParameters()).getSupportedPreviewSizes();
        requestLayout();
      }
    }

    /// Called when the holder is created/destroyed/changed
    private SurfaceHolder.Callback surfaceChange = new SurfaceHolder.Callback()
    {
      @Override
      public void surfaceCreated(SurfaceHolder holder) 
      {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        if (mCamera != null)
          try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(previewFrame);
          } catch (IOException e) {
            e.printStackTrace();
          }
      }
      
      @Override
      public void surfaceDestroyed(SurfaceHolder holder) 
      {
        if (mCamera != null)
          mCamera.stopPreview();
      }
      
      @Override
      public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) 
      {
        if (mCamera != null)
        {
          Camera.Parameters parameters = (Camera.Parameters) mCamera.getParameters();
          if (mPreviewSize != null)
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
          else
            parameters.setPreviewSize(w, h);
          
          mCamera.setParameters(parameters);
          mCamera.startPreview();
        }
      }
    };
    
    /// Called for each frame previewed
    private PreviewCallback previewFrame = new PreviewCallback()
    {
      @Override
      public void onPreviewFrame(byte[] data, Camera camera) 
      {
        Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
        EclairDetected.this.invalidate();
      }
    };
  }
  
  private static class FroyoDetected extends VersionedPreview
  {
    
    FroyoDetected(Context context) 
    {
      super(context);

      mSurfaceView = new SurfaceView(context);
      addView(mSurfaceView);
      
      // Install a SurfaceHolder.Callback so we get notified when the
      // underlying surface is created and destroyed.
      mHolder = mSurfaceView.getHolder();
      mHolder.addCallback(surfaceChange);
      mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    @Override
    public void setCamera(VersionedCamera c)
    {
      mCamera = c;
      if (mCamera != null) 
      {
        mSupportedPreviewSizes = ((Camera.Parameters) mCamera.getParameters()).getSupportedPreviewSizes();
        requestLayout();
      }
    }

    /// Called when the holder is created/destroyed/changed
    private SurfaceHolder.Callback surfaceChange = new SurfaceHolder.Callback()
    {
      @Override
      public void surfaceCreated(SurfaceHolder holder) 
      {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        if (mCamera != null)
          try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(previewFrame);
          } catch (IOException e) {
            e.printStackTrace();
          }
      }
      
      @Override
      public void surfaceDestroyed(SurfaceHolder holder) 
      {
        if (mCamera != null)
          mCamera.stopPreview();
      }
      
      @Override
      public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) 
      {
        if (mCamera != null)
        {
          Camera.Parameters parameters = (Camera.Parameters) mCamera.getParameters();
          if (mPreviewSize != null)
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
          else
            parameters.setPreviewSize(w, h);
          
          mCamera.setParameters(parameters);
          mCamera.startPreview();
        }
      }
    };
    
    /// Called for each frame previewed
    private PreviewCallback previewFrame = new PreviewCallback()
    {
      @Override
      public void onPreviewFrame(byte[] data, Camera camera) 
      {
        Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
        FroyoDetected.this.invalidate();
      }
    };
  }
  
  private static class GingerbreadDetected extends VersionedPreview
  {
    
    GingerbreadDetected(Context context) 
    {
      super(context);

      mSurfaceView = new SurfaceView(context);
      addView(mSurfaceView);
      
      // Install a SurfaceHolder.Callback so we get notified when the
      // underlying surface is created and destroyed.
      mHolder = mSurfaceView.getHolder();
      mHolder.addCallback(surfaceChange);
      mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    
    @Override
    public void setCamera(VersionedCamera c)
    {
      mCamera = c;
      if (mCamera != null) 
      {
        mSupportedPreviewSizes = ((Camera.Parameters) mCamera.getParameters()).getSupportedPreviewSizes();
        requestLayout();
      }
    }

    /// Called when the holder is created/destroyed/changed
    private SurfaceHolder.Callback surfaceChange = new SurfaceHolder.Callback()
    {
      @Override
      public void surfaceCreated(SurfaceHolder holder) 
      {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        if (mCamera != null)
          try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(previewFrame);
          } catch (IOException e) {
            e.printStackTrace();
          }
      }
      
      @Override
      public void surfaceDestroyed(SurfaceHolder holder) 
      {
        if (mCamera != null)
          mCamera.stopPreview();
      }
      
      @Override
      public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) 
      {
        if (mCamera != null)
        {
          Camera.Parameters parameters = (Camera.Parameters) mCamera.getParameters();
          if (mPreviewSize != null)
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
          else
            parameters.setPreviewSize(w, h);
          
          mCamera.setParameters(parameters);
          mCamera.startPreview();
        }
      }
    };
    
    /// Called for each frame previewed
    private PreviewCallback previewFrame = new PreviewCallback()
    {
      @Override
      public void onPreviewFrame(byte[] data, Camera camera) 
      {
        Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
        GingerbreadDetected.this.invalidate();
      }
    };
  }
  
}
