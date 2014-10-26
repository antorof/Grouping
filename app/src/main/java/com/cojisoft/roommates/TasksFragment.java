package com.cojisoft.roommates;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.cojisoft.models.ModelBase;

public class TasksFragment extends BaseFragment
{
	/**
	 * Devuelve una instancia de TaskFragment
	 * @return
	 */
	public static TasksFragment newInstance()
	{
		return new TasksFragment();
	}
	
	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) 
	{
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.groceries_menu, menu);
		return true;
	}

	@Override
	protected Cursor getItems(SQLiteDatabase db, boolean special) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createList(View view, SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ModelBase createItemFromCursor(Cursor cursor, Boolean special) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addItemToAdapter(ModelBase item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteItemFromAdapter(ModelBase item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteItem(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void toggleSpecialItem(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void doneCurrentSelectedItems() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void sortAndNotifyAdapter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createToastDelete(int size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createToastToggle(int size) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createToastDone(int size) {
		// TODO Auto-generated method stub
		
	}

}
