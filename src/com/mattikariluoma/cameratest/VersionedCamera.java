package com.mattikariluoma.cameratest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;
import android.content.Context;
import android.os.Build;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.CameraInfo;

import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class VersionedCamera
{
  private static final String TAG = "CameraTest.VersionedCamera";
  
  public static VersionedCamera newInstance() 
  {
    final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
    VersionedCamera detector = null;
    if (sdkVersion < Build.VERSION_CODES.DONUT)
      detector = new CupcakeDetected();
    else if (sdkVersion < Build.VERSION_CODES.ECLAIR)
      detector = new DonutDetected();
    else if (sdkVersion < Build.VERSION_CODES.FROYO)
      detector = new EclairDetected();
    else if (sdkVersion < Build.VERSION_CODES.GINGERBREAD)
      detector = new FroyoDetected();
    else 
      detector = new GingerbreadDetected();
    
    Log.d(TAG, "Created new " + detector.getClass());

    return detector;
  }

  public abstract void open();
  public abstract void startPreview();
  public abstract void stopPreview();
  public abstract void release();
  public abstract void takePicture();
  public abstract Object getParameters();
  public abstract void setParameters(Object p);
  public abstract void setPreviewDisplay(Object s) throws IOException;
  public abstract void setPreviewCallback(Object p) throws IOException;
  
  private static class CupcakeDetected extends VersionedCamera 
  {
    private Camera mCamera;
    
    public CupcakeDetected()
    {
      this.open();
    }
    
    @Override
    public void open()
    {
      mCamera = Camera.open();
    }
    
    @Override
    public void startPreview()
    {
      if (mCamera != null)
        mCamera.startPreview();
    }
    
    @Override
    public void stopPreview()
    {
      if (mCamera != null)
        mCamera.stopPreview();
    }
    
    @Override
    public void release()
    {
      try {
        setPreviewCallback(null);
      } catch(IOException ioe) {
      }
      if (mCamera != null)
        mCamera.release();
      mCamera = null;
    }
    
    @Override
    public void takePicture()
    {
      if (mCamera != null)
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }
    
    @Override
    public Object getParameters()
    {
      Object p = null;
      
      if (mCamera != null)
        p = mCamera.getParameters();
      
      return p;
    }
    
    @Override
    public void setParameters(Object p)
    {
      if (mCamera != null)
        mCamera.setParameters((Camera.Parameters) p);
    }
    
    @Override
    public void setPreviewDisplay(Object s) throws IOException
    {
      if (mCamera != null)
        mCamera.setPreviewDisplay((SurfaceHolder) s);
    }
    
    @Override
    public void setPreviewCallback(Object p) throws IOException
    {
      if (mCamera != null)
        mCamera.setPreviewCallback((PreviewCallback) p);
    }
         
    /// Called when shutter is opened
    private ShutterCallback shutterCallback = new ShutterCallback() 
    {
      @Override
      public void onShutter() 
      {
        Log.d(TAG, "onShutter");
      }
    };

    /// Handles data for raw picture
    private PictureCallback rawCallback = new PictureCallback()
    {
      @Override
      public void onPictureTaken(byte[] data, Camera camera) 
      {
        Log.d(TAG, "onPictureTaken - raw");
      }
    };

    /// Handles data for jpeg picture
    private PictureCallback jpegCallback = new PictureCallback()
    {
      @Override
      public void onPictureTaken(byte[] data, Camera camera) 
      {
        //immediately start the preview again
        ///@see http://developer.android.com/reference/android/hardware/Camera.html#takePicture%28android.hardware.Camera.ShutterCallback,%20android.hardware.Camera.PictureCallback,%20android.hardware.Camera.PictureCallback,%20android.hardware.Camera.PictureCallback%29
        if (mCamera != null)
          mCamera.startPreview();
          
        FileOutputStream outStream = null;
        try {
          // Write to SD Card
          outStream = new FileOutputStream(String.format("/sdcard/%d.jpg",
            System.currentTimeMillis()) );
          outStream.write(data);
          outStream.close();
          Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
        }
        Log.d(TAG, "onPictureTaken - jpeg");
      }
    };
  }
  
  private static class DonutDetected extends VersionedCamera
  {
    private Camera mCamera;
    
    public DonutDetected()
    {
      this.open();
    }
    
    @Override
    public void open()
    {
      mCamera = Camera.open();
    }
    
    @Override
    public void startPreview()
    {
      if (mCamera != null)
        mCamera.startPreview();
    }
    
    @Override
    public void stopPreview()
    {
      if (mCamera != null)
        mCamera.stopPreview();
    }
    
    @Override
    public void release()
    {
      try {
        setPreviewCallback(null);
      } catch(IOException ioe) {
      }
      if (mCamera != null)
        mCamera.release();
      mCamera = null;
    }
    
    @Override
    public void takePicture()
    {
      if (mCamera != null)
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }
    
    @Override
    public Object getParameters()
    {
      Object p = null;
      
      if (mCamera != null)
        p = mCamera.getParameters();
      
      return p;
    }
    
    @Override
    public void setParameters(Object p)
    {
      if (mCamera != null)
        mCamera.setParameters((Camera.Parameters) p);
    }
    
    @Override
    public void setPreviewDisplay(Object s) throws IOException
    {
      if (mCamera != null)
        mCamera.setPreviewDisplay((SurfaceHolder) s);
    }
    
    @Override
    public void setPreviewCallback(Object p) throws IOException
    {
      if (mCamera != null)
        mCamera.setPreviewCallback((PreviewCallback) p);
    }
    
    /// Called when shutter is opened
    private ShutterCallback shutterCallback = new ShutterCallback() 
    {
      @Override
      public void onShutter() 
      {
        Log.d(TAG, "onShutter");
      }
    };

    /// Handles data for raw picture
    private PictureCallback rawCallback = new PictureCallback()
    {
      @Override
      public void onPictureTaken(byte[] data, Camera camera) 
      {
        Log.d(TAG, "onPictureTaken - raw");
      }
    };

    /// Handles data for jpeg picture
    private PictureCallback jpegCallback = new PictureCallback()
    {
      @Override
      public void onPictureTaken(byte[] data, Camera camera) 
      {
        //immediately start the preview again
        ///@see http://developer.android.com/reference/android/hardware/Camera.html#takePicture%28android.hardware.Camera.ShutterCallback,%20android.hardware.Camera.PictureCallback,%20android.hardware.Camera.PictureCallback,%20android.hardware.Camera.PictureCallback%29
        if (mCamera != null)
          mCamera.startPreview();
          
        FileOutputStream outStream = null;
        try {
          // Write to SD Card
          outStream = new FileOutputStream(String.format("/sdcard/%d.jpg",
            System.currentTimeMillis()) );
          outStream.write(data);
          outStream.close();
          Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
        }
        Log.d(TAG, "onPictureTaken - jpeg");
      }
    };
  }
  
  private static class EclairDetected extends VersionedCamera 
  {
    private Camera mCamera;
    
    public EclairDetected()
    {
      this.open();
    }
    
    @Override
    public void open()
    {
      mCamera = Camera.open();
    }
    
    @Override
    public void startPreview()
    {
      if (mCamera != null)
        mCamera.startPreview();
    }
    
    @Override
    public void stopPreview()
    {
      if (mCamera != null)
        mCamera.stopPreview();
    }
    
    @Override
    public void release()
    {
      try {
        setPreviewCallback(null);
      } catch(IOException ioe) {
      }
      if (mCamera != null)
        mCamera.release();
      mCamera = null;
    }
    
    @Override
    public void takePicture()
    {
      if (mCamera != null)
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }
    
    @Override
    public Object getParameters()
    {
      Object p = null;
      
      if (mCamera != null)
        p = mCamera.getParameters();
      
      return p;
    }
    
    @Override
    public void setParameters(Object p)
    {
      if (mCamera != null)
        mCamera.setParameters((Camera.Parameters) p);
    }
    
    @Override
    public void setPreviewDisplay(Object s) throws IOException
    {
      if (mCamera != null)
        mCamera.setPreviewDisplay((SurfaceHolder) s);
    }
    
    @Override
    public void setPreviewCallback(Object p) throws IOException
    {
      if (mCamera != null)
        mCamera.setPreviewCallback((PreviewCallback) p);
    }
    
    /// Called when shutter is opened
    private ShutterCallback shutterCallback = new ShutterCallback() 
    {
      @Override
      public void onShutter() 
      {
        Log.d(TAG, "onShutter");
      }
    };

    /// Handles data for raw picture
    private PictureCallback rawCallback = new PictureCallback()
    {
      @Override
      public void onPictureTaken(byte[] data, Camera camera) 
      {
        Log.d(TAG, "onPictureTaken - raw");
      }
    };

    /// Handles data for jpeg picture
    private PictureCallback jpegCallback = new PictureCallback()
    {
      @Override
      public void onPictureTaken(byte[] data, Camera camera) 
      {
        //immediately start the preview again
        ///@see http://developer.android.com/reference/android/hardware/Camera.html#takePicture%28android.hardware.Camera.ShutterCallback,%20android.hardware.Camera.PictureCallback,%20android.hardware.Camera.PictureCallback,%20android.hardware.Camera.PictureCallback%29
        if (mCamera != null)
          mCamera.startPreview();
          
        FileOutputStream outStream = null;
        try {
          // Write to SD Card
          outStream = new FileOutputStream(String.format("/sdcard/%d.jpg",
            System.currentTimeMillis()) );
          outStream.write(data);
          outStream.close();
          Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
        }
        Log.d(TAG, "onPictureTaken - jpeg");
      }
    };
  }
  
  private static class FroyoDetected extends VersionedCamera 
  {
    private Camera mCamera;
    
    public FroyoDetected()
    {
      this.open();
    }
    
    @Override
    public void open()
    {
      mCamera = Camera.open();
    }
    
    @Override
    public void startPreview()
    {
      if (mCamera != null)
        mCamera.startPreview();
    }
    
    @Override
    public void stopPreview()
    {
      if (mCamera != null)
        mCamera.stopPreview();
    }
    
    @Override
    public void release()
    {
      try {
        setPreviewCallback(null);
      } catch(IOException ioe) {
      }
      if (mCamera != null)
        mCamera.release();
      mCamera = null;
    }
    
    @Override
    public void takePicture()
    {
      if (mCamera != null)
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }
    
    @Override
    public Object getParameters()
    {
      Object p = null;
      
      if (mCamera != null)
        p = mCamera.getParameters();
      
      return p;
    }
    
    @Override
    public void setParameters(Object p)
    {
      if (mCamera != null)
        mCamera.setParameters((Camera.Parameters) p);
    }
    
    @Override
    public void setPreviewDisplay(Object s) throws IOException
    {
      if (mCamera != null)
        mCamera.setPreviewDisplay((SurfaceHolder) s);
    }
    
    @Override
    public void setPreviewCallback(Object p) throws IOException
    {
      if (mCamera != null)
        mCamera.setPreviewCallback((PreviewCallback) p);
    }
    
    /// Called when shutter is opened
    private ShutterCallback shutterCallback = new ShutterCallback() 
    {
      @Override
      public void onShutter() 
      {
        Log.d(TAG, "onShutter");
      }
    };

    /// Handles data for raw picture
    private PictureCallback rawCallback = new PictureCallback()
    {
      @Override
      public void onPictureTaken(byte[] data, Camera camera) 
      {
        Log.d(TAG, "onPictureTaken - raw");
      }
    };

    /// Handles data for jpeg picture
    private PictureCallback jpegCallback = new PictureCallback()
    {
      @Override
      public void onPictureTaken(byte[] data, Camera camera) 
      {
        //immediately start the preview again
        ///@see http://developer.android.com/reference/android/hardware/Camera.html#takePicture%28android.hardware.Camera.ShutterCallback,%20android.hardware.Camera.PictureCallback,%20android.hardware.Camera.PictureCallback,%20android.hardware.Camera.PictureCallback%29
        if (mCamera != null)
          mCamera.startPreview();
          
        FileOutputStream outStream = null;
        try {
          // Write to SD Card
          outStream = new FileOutputStream(String.format("/sdcard/%d.jpg",
            System.currentTimeMillis()) );
          outStream.write(data);
          outStream.close();
          Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
        }
        Log.d(TAG, "onPictureTaken - jpeg");
      }
    };
  }
  
  private static class GingerbreadDetected extends VersionedCamera
  {
    private Camera mCamera;
    private int numberOfCameras;
    private int currentCamera;
    private int defaultCameraId;
    private boolean firstRun = true;
    
    public GingerbreadDetected()
    {
      if (firstRun)
        init();
      
      this.open(currentCamera);
    }
    
    private void init()
    {
      numberOfCameras = Camera.getNumberOfCameras();
      
      CameraInfo cameraInfo = new CameraInfo();
      for (int i = 0; i < numberOfCameras; i++) 
      {
        Camera.getCameraInfo(i, cameraInfo);
        if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK)
        {
          defaultCameraId = i;
          break;
        }
      }
      
      currentCamera = defaultCameraId;
      firstRun = false;
    }
    
    private void open(int i)
    {
      mCamera = Camera.open(i);
    }
    
    @Override
    public void open()
    {
      mCamera = Camera.open();
    }
    
    @Override
    public void startPreview()
    {
      if (mCamera != null)
        mCamera.startPreview();
    }
    
    @Override
    public void stopPreview()
    {
      if (mCamera != null)
        mCamera.stopPreview();
    }
    
    @Override
    public void release()
    {
      try {
        setPreviewCallback(null);
      } catch(IOException ioe) {
      }
      if (mCamera != null)
        mCamera.release();
      mCamera = null;
    }
    
    @Override
    public void takePicture()
    {
      if (mCamera != null)
        mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }
    
    @Override
    public Object getParameters()
    {
      Object p = null;
      
      if (mCamera != null)
        p = mCamera.getParameters();
      
      return p;
    }
    
    @Override
    public void setParameters(Object p)
    {
      if (mCamera != null)
        mCamera.setParameters((Camera.Parameters) p);
    }
    
    @Override
    public void setPreviewDisplay(Object s) throws IOException
    {
      if (mCamera != null)
        mCamera.setPreviewDisplay((SurfaceHolder) s);
    }
    
    @Override
    public void setPreviewCallback(Object p) throws IOException
    {
      if (mCamera != null)
        mCamera.setPreviewCallback((PreviewCallback) p);
    }
    
    /// Called when shutter is opened
    private ShutterCallback shutterCallback = new ShutterCallback() 
    {
      @Override
      public void onShutter() 
      {
        Log.d(TAG, "onShutter");
      }
    };

    /// Handles data for raw picture
    private PictureCallback rawCallback = new PictureCallback()
    {
      @Override
      public void onPictureTaken(byte[] data, Camera camera) 
      {
        Log.d(TAG, "onPictureTaken - raw");
      }
    };

    /// Handles data for jpeg picture
    private PictureCallback jpegCallback = new PictureCallback()
    {
      @Override
      public void onPictureTaken(byte[] data, Camera camera) 
      {
        //immediately start the preview again
        ///@see http://developer.android.com/reference/android/hardware/Camera.html#takePicture%28android.hardware.Camera.ShutterCallback,%20android.hardware.Camera.PictureCallback,%20android.hardware.Camera.PictureCallback,%20android.hardware.Camera.PictureCallback%29
        if (mCamera != null)
          mCamera.startPreview();
          
        FileOutputStream outStream = null;
        try {
          // Write to SD Card
          outStream = new FileOutputStream(String.format("/sdcard/%d.jpg",
            System.currentTimeMillis()) );
          outStream.write(data);
          outStream.close();
          Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        } finally {
        }
        Log.d(TAG, "onPictureTaken - jpeg");
      }
    };
  }
  
}
