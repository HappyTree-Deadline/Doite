package com.example.doite.services;

import java.util.Calendar;

import com.example.doite.MainActivity;
import com.example.doite.NewTaskActivity;
import com.example.doite.R;
import com.example.doite.SQLHelper;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.Toast;

public class ServicesDemo extends IntentService {

	private static final String TAG = "MyIntentService";
	private SQLiteDatabase db ;
    public ServicesDemo()
    {
        super("MyIntentService");
    }
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    @Override
    public void onCreate()
    {
    	db = new SQLHelper(this).getReadableDatabase();
        // Log.i(TAG, "MyIntentService==>>onCreate==>>线程ID："+Thread.currentThread().getId());
        super.onCreate();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Log.i(TAG, "MyIntentService==>>onStartCommand==>>线程ID："+Thread.currentThread().getId());
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        
    	while (true) {
			Cursor cursor = db.query("event", new String[]{"_id","ename","edate","etime"}, null, null, null, null, null);
			
			int noti = 0;
			while (cursor.moveToNext()) {
				int id = cursor.getInt(0);
				String name = cursor.getString(1);
				String date = cursor.getString(2);
				String time = cursor.getString(3);
				
				//Toast.makeText(NewTaskActivity.this,String.valueOf(id)	, Toast.LENGTH_LONG).show();
				//noti += "name = " + name + "|";
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
				if (date.equals(year+"/"+month+"/"+day)) {
					noti ++;
				}
			}
			if (noti <= 0) {				
				continue;
			}
			
	    	NotificationCompat.Builder mBuilder =
	    	        new NotificationCompat.Builder(this)
	    	        .setSmallIcon(R.drawable.ic_launcher)
	    	        .setContentTitle("有待完成信息")
	    	        .setContentText("今天有"+noti+"条要完成的任务");
	    	
	    	Intent resultIntent = new Intent(this, MainActivity.class);


	    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
	    	
	    	stackBuilder.addParentStack(MainActivity.class);
	    	
	    	stackBuilder.addNextIntent(resultIntent);
	    	PendingIntent resultPendingIntent =
	    	        stackBuilder.getPendingIntent(
	    	            0,
	    	            PendingIntent.FLAG_UPDATE_CURRENT
	    	        );
	    	mBuilder.setContentIntent(resultPendingIntent);
	    	NotificationManager mNotificationManager =
	    		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	    		// mId allows you to update the notification later on.
	    		mNotificationManager.notify(11111, mBuilder.build());
	    		
	    	try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
    @Override
    public void onDestroy()
    {
        // Log.i(TAG, "MyIntentService==>>onDestory==>>线程ID："+Thread.currentThread().getId());
        super.onDestroy();
    }



}
