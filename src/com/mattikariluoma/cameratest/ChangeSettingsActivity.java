package com.mattikariluoma.cameratest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.ListActivity;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.content.Context;
import android.view.View;

import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ChangeSettingsActivity extends ListActivity
{
  private static final String TAG = "CameraTest.ChangeSettings";

  /// Called when the activity is first created
  @Override
  public void onCreate(Bundle savedInstanceState) 
  {
    super.onCreate(savedInstanceState);

    String[] entries = getResources().getStringArray(R.array.settings_array);
    setListAdapter(new ArrayAdapter<String>(this, R.layout.settings, entries));
    
    ListView lv = getListView();
    lv.setTextFilterEnabled(true); // allows user to start typing to filter choices
    lv.setOnItemClickListener(itemClickedAction);

    Log.d(TAG, "onCreate");
  }
  
  @Override
  protected void onResume() 
  {
    super.onResume();
  }

  @Override
  protected void onPause() 
  {
    super.onPause();
  }
  
  /// Handles when an entry is clicked
  private OnItemClickListener itemClickedAction = new OnItemClickListener() 
  {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) 
    {
      Log.d(TAG, "onClick");
      //startActivity(new Intent(CameraTestActivity.this, ChangeTestActivity.class)); // only needed if we ended the previous activity
      finish(); //close this activity and go back to the last
    }
  };

}


