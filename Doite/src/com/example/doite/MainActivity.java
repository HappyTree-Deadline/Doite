package com.example.doite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.doite.services.ServicesDemo;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnItemLongClickListener {

	private ListView list;
	private List<Map<String,Object>> data = null;
	public int MID; 
	private SQLiteDatabase db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		data = getData();
		list = (ListView)findViewById(R.id.listView1);
		list.setOnItemLongClickListener(this);
		
		list.setOnCreateContextMenuListener(new OnCreateContextMenuListener() { 
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
                menu.add(0, 0, 0, "删除"); 
                menu.add(0, 1, 0, "编辑"); 
                menu.add(0, 2, 0, "完成"); 
				
			} 
		}); 
		
		SQLHelper helper  = new SQLHelper(MainActivity.this);
	    db = helper.getWritableDatabase();

	    Intent intent = new Intent(this, ServicesDemo.class);
	    startService(intent);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		new MyThread().run();
		super.onResume();
	}
	
	public Handler myHandler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			data = (List<Map<String, Object>>) msg.obj;
			MyAdapter myAdatper = new MyAdapter();
			list.setAdapter(myAdatper);
		};
	};
	
	
	class MyThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Cursor cursor = db.query("event", new String[]{"_id","ename","edate","etime"}, null, null, null, null, null);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>> ();
			while (cursor.moveToNext()) {
				int id = cursor.getInt(0);
				String name = cursor.getString(1);
				String date = cursor.getString(2);
				String time = cursor.getString(3);
				//Toast.makeText(MainActivity.this, name + "|" + time	, Toast.LENGTH_LONG).show();
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("name", name);
				map.put("date", date);
				map.put("time", time);
				map.put("_id",id);
				list.add(map);
			}
			
			Message message = Message.obtain();
			message.what = 1;
			message.obj = list;
			myHandler.sendMessage(message);
		}
		
	}
	
	
	public boolean onContextItemSelected(MenuItem item) { 

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item 
                        .getMenuInfo(); 
      // MID = (int) info.id;// 这里的info.id对应的就是数据库中_id的值 

        int pos = (int)info.position;
        Map<String,Object> map = data.get(pos);
        Integer id = (Integer)map.get("_id");
        switch (item.getItemId()) { 
        case 0: 
            // 删除操作 
    		int rows = db.delete("event", "_id=?", new String[]{String.valueOf(id)});
    		if (rows == 1) {
    			new MyThread().run();
    			Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_LONG).show();
    		}
            break; 

        case 1: 
            // 编辑操作 
        	Intent intent = new Intent(this, EditTaskActivity.class);
        	intent.putExtra("id", id);
        	startActivity(intent);
            break; 

        case 2: 
            // 完成操作 
        	ContentValues values = new ContentValues();
        	values.put("eflag", 2);
        	int num = db.update("event", values, "_id=?", new String[]{String.valueOf(id)});
        	if (num > 0) {
        		Toast.makeText(this, "更新成功", Toast.LENGTH_LONG).show();
        		
        	}
            break; 

        default: 
            break; 
        } 

        return super.onContextItemSelected(item); 

} 

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this,NewTaskActivity.class);
			startActivity(intent);
			return true;
		} else if (id == R.id.action_total) {
			 
			Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM event", null); 
			cursor.moveToNext(); 
			float total = cursor.getInt(0); 
			
			Cursor cursor1 = db.rawQuery("SELECT COUNT(*) FROM event where eflag=2", null); 
			cursor1.moveToNext(); 
			float total1 = cursor1.getInt(0); 
			
			float per = (total1/total) * 100;
			
			Toast.makeText(this, String.valueOf(total1+"|" +total), Toast.LENGTH_LONG).show();
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("完成率"+String.valueOf(per)+" %")
		       .setTitle("总数统计").setPositiveButton("确定", new DialogInterface.OnClickListener() {
	               @Override
	               public void onClick(DialogInterface dialog, int id) {
	                   // User clicked OK, so save the mSelectedItems results somewhere
	                   // or return them to the component that opened the dialog
	                   dialog.dismiss();
	               }
	           });

			AlertDialog dialog = builder.create();
			dialog.show();
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	private List<Map<String,Object>> getData() {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//设置日期格式
		
		// new Date()为获取当前系统时间
		for (int $i = 0; $i < 10; $i ++) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("name", "mc" + $i);
			map.put("time", df.format(new Date()));
			
			list.add(map);
		}
		return list;
	}
	
	
	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.event_item, null);
//                AbsListView.LayoutParams lp = new AbsListView.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, 70); 
//                convertView.setLayoutParams(lp);
                holder = new ViewHolder();
                holder.eventName = (TextView)convertView.findViewById(R.id.eventname);
                holder.eventTime = (TextView)convertView.findViewById(R.id.eventtime);              
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            
            
            holder.eventName.setText(data.get(position).get("name").toString());
            holder.eventTime.setText(data.get(position).get("date").toString() + " " + data.get(position).get("time").toString());
            return convertView;
		}
		
	}
	
    public static class ViewHolder {
        public TextView eventName;
        public TextView eventTime;
    }

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		//Toast.makeText(this, "点击事件", Toast.LENGTH_LONG).show();
		return false;
	}
	
	
}
