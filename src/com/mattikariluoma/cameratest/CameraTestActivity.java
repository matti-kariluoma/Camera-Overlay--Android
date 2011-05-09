package com.mattikariluoma.cameratest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.content.Context;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.FrameLayout;

public class CameraTestActivity extends Activity
{
  private static final String TAG = "CameraTest";
  VersionedCamera mCamera;
  VersionedPreview mPreview;
  Button mTakePicture;

  /// Called when the activity is first created
  @Override
  public void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
    setContentView(R.layout.main);

    mPreview = VersionedPreview.newInstance(this);
   
    ((FrameLayout) findViewById(R.id.mPreview)).addView(mPreview); 

    mTakePicture = (Button) findViewById(R.id.mTakePicture);
    mTakePicture.setOnClickListener(mTakePictureAction);

    Log.d(TAG, "onCreate");
  }
  
  @Override
  protected void onResume() 
  {
    super.onResume();
    
    //Grabs default camera, i.e. first rear-facing
    //mCamera = Camera.open();
    mCamera = VersionedCamera.newInstance();
    mPreview.setCamera(mCamera);
  }

  @Override
  protected void onPause() 
  {
    super.onPause();

    if (mCamera != null)
    {
      mCamera.release();
    }
    mPreview.setCamera(null);
    mCamera = null;
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.options_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
    switch (item.getItemId()) 
    {
      case R.id.switch_cam:
    
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(this.getString(R.string.switch_cam))
          .setNeutralButton("Close", null);
        AlertDialog alert = builder.create();
        alert.show();
        return true;
        
      case R.id.change_settings:
      
         startActivity(new Intent(CameraTestActivity.this, ChangeSettingsActivity.class));

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  /// Handles when mTakePicture is clicked
  private OnClickListener mTakePictureAction = new OnClickListener() 
  {
    @Override
    public void onClick(View v) 
    { 
      if (mCamera != null)
        mCamera.takePicture();
      Log.d(TAG, "onClick");
    }
  };
  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) 
  {
    switch (keyCode) 
    {
      case KeyEvent.KEYCODE_CAMERA:
      
        if (mCamera != null)
          mCamera.takePicture();
        return true;
        
      default:
        Log.d(TAG, "keyCode "+(new Integer(keyCode)).toString()+" pressed");
        return false;
    }
  } 
  
}


