package com.paad.todolist;

import java.sql.Date;
import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

public class Todo_ListActivity extends Activity {
	 private ArrayList<ToDoItem> todoItems;
	  private ListView myListView;
	  private EditText myEditText; 
	  private ToDoItemAdapter aa;
	  private ToDoDBAdapter toDoDBAdapter;
	  private Cursor toDoListCursor;

	  private static final String TEXT_ENTRY_KEY="TEXT_ENTRY_KEY";
	  private static final String ADDING_ITEM_KEY="ADDING_ITEM_KEY";
	  private static final String SELECTED_INDEX_KEY="SELECTED_INDEX_KEY";
	  
	  
	  private boolean addingNew = false;
	  
	  // Define the new menu item identifiers
	  static final private int ADD_NEW_TODO = Menu.FIRST;
	  static final private int REMOVE_TODO = Menu.FIRST + 1;

	  /** Called when the activity is first created. */
	  @Override
	  public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);

	    // Inflate your view
	    setContentView(R.layout.main);
	    
	    // Get references to UI widgets
	    myListView = (ListView)findViewById(R.id.myListView);
	    myEditText = (EditText)findViewById(R.id.myEditText);
	  
	    // Create the Todo List ArrayList and Adapter and assign is to the ListView
	    todoItems = new ArrayList<ToDoItem>();
	    aa = new ToDoItemAdapter(getApplicationContext(), R.layout.todolist_item, todoItems);    
	    myListView.setAdapter(aa);

	    
	

	    
	    // Assign the KeyListener to the DPad button to add new items
	    myEditText.setOnKeyListener(new OnKeyListener() {
	      public boolean onKey(View v, int keyCode, KeyEvent event) {
	        if (event.getAction() == KeyEvent.ACTION_DOWN)
	          if (keyCode == KeyEvent.KEYCODE_ENTER) {
	            // Add a new todo item and clear the input box.
	        	ToDoItem newItem=new ToDoItem(myEditText.getText().toString());
	        	toDoDBAdapter.insertTask(newItem);
	        	updateArray();
	            //todoItems.add(0, newItem);
	            myEditText.setText("");
	            aa.notifyDataSetChanged();
	            cancelAdd();
	            return true; 
	          }
	        return false;
	      }
	    });
	    
	    registerForContextMenu(myListView);
	    restoreUIState();
	    
	    toDoDBAdapter = new ToDoDBAdapter(this);
	    // Open or create the database
	    toDoDBAdapter.open();
	    
	    populateTodoList();

	  }
	  private void populateTodoList() {
		// TODO Auto-generated method stub
		toDoListCursor=toDoDBAdapter.getAllToDoItemsCursor();
		startManagingCursor(toDoListCursor);
		
		updateArray();
	}
	private void updateArray() {
		// TODO Auto-generated method stub
		toDoListCursor.requery();

		  todoItems.clear();
		    
		  if (toDoListCursor.moveToFirst())
		    do { 
		      String task = toDoListCursor.getString(ToDoDBAdapter.TASK_COLUMN);
		      long created = toDoListCursor.getLong(ToDoDBAdapter.CREATION_DATE_COLUMN);

		      ToDoItem newItem = new ToDoItem(task, new Date(created));
		      todoItems.add(0, newItem);
		    } while(toDoListCursor.moveToNext());
		  
		  aa.notifyDataSetChanged();

	}
	@Override
	protected void onPause(){
		  super.onPause();
		  SharedPreferences uiState=getPreferences(0);
		  SharedPreferences.Editor editor=uiState.edit();
		  //添加ui状态首选项的值
		  editor.putString(TEXT_ENTRY_KEY, myEditText.getText().toString());
		  editor.putBoolean(ADDING_ITEM_KEY, addingNew);
		  editor.commit();
		  
	  }
	  
	  
	  
	  
	  @Override
	  public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);      
	    
	    // Create and add new menu items.  
	    MenuItem itemAdd = menu.add(0, ADD_NEW_TODO, Menu.NONE, R.string.add_new);
	    MenuItem itemRem = menu.add(0, REMOVE_TODO, Menu.NONE, R.string.remove);

	    // Assign icons
	    itemAdd.setIcon(R.drawable.add_new_item);
	    itemRem.setIcon(R.drawable.remove_item);

	    // Allocate shortcuts to each of them.
	    itemAdd.setShortcut('0', 'a');
	    itemRem.setShortcut('1', 'r');

	    return true;
	  }
	  
	  @Override
		public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {	  
		  super.onCreateContextMenu(menu, v, menuInfo);
		  
	    menu.setHeaderTitle("Selected To Do Item");
		  menu.add(0, REMOVE_TODO, Menu.NONE, R.string.remove);   	    
		}

	  @Override
	  public boolean onPrepareOptionsMenu(Menu menu) {
	    super.onPrepareOptionsMenu(menu);

	    int idx = myListView.getSelectedItemPosition();

	    String removeTitle = getString(addingNew ? R.string.cancel : R.string.remove);

	    MenuItem removeItem = menu.findItem(REMOVE_TODO);
	    removeItem.setTitle(removeTitle);
	    removeItem.setVisible(addingNew || idx > -1);

	    return true;    
	  }

	  /** Process the options menu item selection */
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    super.onOptionsItemSelected(item);

	    int index = myListView.getSelectedItemPosition();

	    switch (item.getItemId()) {
	      case (REMOVE_TODO): {
	        if (addingNew) {
	          // If adding a new item, cancel this action 
	          cancelAdd();
	        } 
	        else {
	          // If not adding a new item, remove the currently selected one.
	          removeItem(index);
	        }
	        return true;
	      }
	      case (ADD_NEW_TODO): {
	        // Add a new item
	        addNewItem();
	        return true; 
	      }
	    }

	    return false;
	  }

	  /** Process the context menu item selection */
	  @Override
	  public boolean onContextItemSelected(MenuItem item) {  
	    super.onContextItemSelected(item);
	    switch (item.getItemId()) {
	      case (REMOVE_TODO): {
	        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();    	  	  
	        int index = menuInfo.position;
	        removeItem(index);
	        return true;
	      }
	    }
	    return false;
	  }

	  /** Cancel the action of adding a new todo list item */
	  private void cancelAdd() {
		  addingNew = false;
		  myEditText.setVisibility(View.GONE);
	  }

	  /** Present the text entry box for adding a new todo item */
	  private void addNewItem() {
		  addingNew = true;	  
		  myEditText.setVisibility(View.VISIBLE);
		  myEditText.requestFocus();
	  }

	  /** Remove the specified item from the todo list */
	  private void removeItem(int _index) {
	    //todoItems.remove(_index);
	    //aa.notifyDataSetChanged();
		 toDoDBAdapter.removeTask(todoItems.size()-_index);
		 updateArray();
	  }
	  
	  private void restoreUIState(){
		  SharedPreferences settings=getPreferences(Activity.MODE_PRIVATE);
		  
		  String text=settings.getString(TEXT_ENTRY_KEY, "");
		  Boolean adding=settings.getBoolean(ADDING_ITEM_KEY, false);
		  if(adding)
		  {
			  addNewItem();
			  myEditText.setText(text);
		  }
		  
	  }
	  @Override
	  public void onSaveInstanceState(Bundle savedInstanceState) {
	    savedInstanceState.putInt(SELECTED_INDEX_KEY, myListView.getSelectedItemPosition());

	    super.onSaveInstanceState(savedInstanceState);
	  }


	  @Override
	  public void onRestoreInstanceState(Bundle savedInstanceState) {
	    int pos = -1;

	    if (savedInstanceState != null)
	      if (savedInstanceState.containsKey(SELECTED_INDEX_KEY))
	        pos = savedInstanceState.getInt(SELECTED_INDEX_KEY, -1);

	    myListView.setSelection(pos);
	  }
	  
	  
	  @Override
	  public void onDestroy() {      
	    // Close the database
	    toDoDBAdapter.close();
	    
	    super.onDestroy();
	  }

	  
	}