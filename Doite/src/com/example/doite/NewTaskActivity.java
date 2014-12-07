package com.example.doite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


public class NewTaskActivity extends Activity implements OnDateSetListener {

	private EditText name = null;
	private EditText date = null;
	private TimePicker time =null;
	private Button submit = null;
	private Button cancel = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_task);
		name = (EditText)findViewById(R.id.editText1);
	
		submit = (Button)findViewById(R.id.button2);
		date = (EditText)findViewById(R.id.editText2);
		time = (TimePicker)findViewById(R.id.timePicker1);
	
		cancel = (Button)findViewById(R.id.button1);
		
		date.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				final Calendar calendar = Calendar.getInstance();
				DatePickerDialog d = new DatePickerDialog(
						NewTaskActivity.this,
						NewTaskActivity.this,
				                calendar.get(Calendar.YEAR),
				                calendar.get(Calendar.MONTH),
				                calendar.get(Calendar.DAY_OF_MONTH));
				d.show();
			}
		});
		
		submit.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SQLHelper helper  = new SQLHelper(NewTaskActivity.this);
				SQLiteDatabase db = helper.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put("ename", name.getText().toString());
				values.put("edate", date.getText().toString());
				values.put("etime", time.getCurrentHour() + ":" + time.getCurrentMinute());
				db.insert("event", null, values);
				db.close();
				
				Toast.makeText(NewTaskActivity.this, "success", Toast.LENGTH_LONG).show();
				finish();
			}
		});
		
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String str = String.valueOf(time.getCurrentHour()) + time.getCurrentMinute();
				Toast.makeText(NewTaskActivity.this, str, Toast.LENGTH_LONG).show();
				// TODO Auto-generated method stub
				//finish();
				/*
				SQLHelper helper = new SQLHelper(NewTaskActivity.this);
				SQLiteDatabase db = helper.getReadableDatabase();
				Cursor cursor = db.query("event", new String[]{"_id","ename","etime"}, null, null, null, null, null);
				
				while (cursor.moveToNext()) {
					int id = cursor.getInt(0);
					String name = cursor.getString(1);
					String time = cursor.getString(2);
					Toast.makeText(NewTaskActivity.this,String.valueOf(id)	, Toast.LENGTH_LONG).show();
						
				}
				*/
				

			}
		});
	}
	
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		//Log.i("0000",String.valueOf(year) + "|" + String.valueOf(monthOfYear) + "|" + String.valueOf(dayOfMonth));
		date.setText(String.valueOf(year) + "/" + String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth));
	}
	
}
