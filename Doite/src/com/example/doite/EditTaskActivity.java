package com.example.doite;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

public class EditTaskActivity extends Activity implements OnDateSetListener {

	private SQLiteDatabase db;
	private EditText name;
	private EditText date;
	private TimePicker time;
	private Button submit;
	
	private Button cancel;
	private SQLHelper helper = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		helper = new SQLHelper(this);
		db = helper.getReadableDatabase();
		setContentView(R.layout.edit_task);
		
		final int id = getIntent().getIntExtra("id", 0);
		
		name = (EditText)findViewById(R.id.event_name);
		date = (EditText)findViewById(R.id.event_date);
		time = (TimePicker)findViewById(R.id.event_time);
		submit = (Button)findViewById(R.id.submit);
		cancel = (Button)findViewById(R.id.cancel);
		
		Cursor cursor = db.query("event", new String[]{"_id","ename","edate","etime"}, "_id=?", new String[]{String.valueOf(id)}, null, null, null);
		cursor.moveToNext();
		String nameStr = cursor.getString(1);
		String dateStr = cursor.getString(2);
		String timeStr = cursor.getString(3);
		
		//Log.i("aaaaa", name + "|" + time);
		name.setText(nameStr);
		date.setText(dateStr);
		
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ContentValues values = new ContentValues();
				db = helper.getWritableDatabase();
				values.put("ename", name.getText().toString());
				values.put("edate", date.getText().toString());
				values.put("etime", time.getCurrentHour() + ":" + time.getCurrentMinute());
				db.update("event", values, "_id=?", new String[]{String.valueOf(id)});
				finish();
			}
		});
		
		
		cancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		date.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Calendar calendar = Calendar.getInstance();
				DatePickerDialog d = new DatePickerDialog(
						EditTaskActivity.this,
						EditTaskActivity.this,
				                calendar.get(Calendar.YEAR),
				                calendar.get(Calendar.MONTH),
				                calendar.get(Calendar.DAY_OF_MONTH));
				d.show();
			}
		});
		
	}
	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		// TODO Auto-generated method stub
		date.setText(String.valueOf(year) + "/" + String.valueOf(monthOfYear) + "/" + String.valueOf(dayOfMonth));
		
	}
}
