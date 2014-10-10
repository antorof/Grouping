package com.example.roommates;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.example.roommates.Utils.UIUtils;

import database.ScheduleContract;
import database.ScheduleContract.User;
import database.ScheduleDatabase;
import database.ScheduleDatabase.Tables;

public class RoommatesFragment extends Fragment
{
	ListView lista;
	SparseBooleanArray checkedList = new SparseBooleanArray();
	
	ImageButton mAddButton;
	
	public static RoommatesFragment newInstance()
	{
		return new RoommatesFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
	    return inflater.inflate(R.layout.fragment_roommates, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) 
	{
		lista = (ListView) view.findViewById(R.id.list_roommates);
		
		UIUtils.addHeaderFooterDivider(lista, getActivity());
		Cursor cursor = getUsers();
		
		final UserCursorAdapter adapter = new UserCursorAdapter(
		        getActivity(), R.layout.list_item_roommates, cursor, 0 );

		lista.setAdapter(adapter);
		lista.setClickable(true);
		lista.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				ImageView userIcon = (ImageView) view.findViewById(R.id.user_icon);
				ImageView selectedIcon = (ImageView) view.findViewById(R.id.user_icon_selected);
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.user_check_box);
				checkBox.toggle();
				
				if(checkBox.isChecked())
				{
					userIcon.setVisibility(View.INVISIBLE);
					selectedIcon.setVisibility(View.VISIBLE);
					view.setBackgroundColor(getResources().getColor(R.color.orange));
				}else
				{
					userIcon.setVisibility(View.VISIBLE);
					selectedIcon.setVisibility(View.INVISIBLE);
					view.setBackgroundColor(getResources().getColor(R.color.backgroundListItem));
				}
			}
		});
		
		// Traer al frente el botón para que no aparezca escondido trás la lista
		mAddButton = (ImageButton) view.findViewById(R.id.add_button);
		mAddButton.bringToFront();
		mAddButton.setOnClickListener( new OnClickListener() 
		{
            @Override
            public void onClick(View view) 
            {
            	ScheduleDatabase mDBHelper = new ScheduleDatabase(getActivity());
        		SQLiteDatabase db = mDBHelper.getWritableDatabase();
        		
            	int size = checkedList.size();
            	int i=0;
            	while(i<size)
            	{
            		// La lista empieza en el id 1, por lo tanto es necesario un +1
            		// Obtenemos el id del item que este posicionado en
            		long idItem = lista.getItemIdAtPosition(checkedList.keyAt(i)+1);
            		int numRowsDeleted = db.delete(Tables.USERS, BaseColumns._ID+"=?", 
        					new String[]{String.valueOf(idItem)});
            		Cursor cursor = getUsers();
            		adapter.swapCursor(cursor);
            		adapter.notifyDataSetChanged();
            		if(numRowsDeleted == 0)
            			i++;
            		else
            			size -= numRowsDeleted;
            	}
            	db.close();
            }
        });
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
//	    if (!getActivity().isFinishing()) {
//	        getLoaderManager().restartLoader(HashtagsQuery.TOKEN, null, this);
//	    }
	}
	
	private Cursor getUsers()
	{
		ScheduleDatabase mDBHelper = new ScheduleDatabase(getActivity());
		SQLiteDatabase db = mDBHelper.getReadableDatabase();
	
		return db.query(Tables.USERS, UsersQuery.PROJECTION, null, null, null, null, null /*order*/);
	}
	
	public class UserCursorAdapter extends ResourceCursorAdapter {

	    public UserCursorAdapter(Context context, int layout, Cursor c, int flags) 
	    {
	        super(context, layout, c, flags);
	    }

	    @Override
	    public void bindView(View view, Context context, final Cursor cursor) 
	    {
	    	view.setId(cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)));
	    	
			TextView textName = (TextView) view.findViewById(R.id.user_name);
			TextView textGroceries = (TextView) view.findViewById(R.id.user_subtitle_groceries);
			TextView textTasks = (TextView) view.findViewById(R.id.user_subtitle_tasks);
			TextView textBills = (TextView) view.findViewById(R.id.user_subtitle_bills);
			ImageView image = (ImageView) view.findViewById(R.id.user_icon);
			CheckBox checkbox = (CheckBox) view.findViewById(R.id.user_check_box);

			textName.setText(cursor.getString(cursor.getColumnIndex(ScheduleContract.User.USER_NAME)));
			textGroceries.setText(cursor.getString(cursor.getColumnIndex(ScheduleContract.User.USER_GROCERIES)));
			textTasks.setText(cursor.getString(cursor.getColumnIndex(ScheduleContract.User.USER_TASKS)));
			textBills.setText(cursor.getString(cursor.getColumnIndex(ScheduleContract.User.USER_BILLS)) + "€");
			
			String color = cursor.getString(cursor.getColumnIndex(ScheduleContract.User.USER_COLOR));
			image.setColorFilter(UIUtils.getColor(getActivity(), color));
			
			checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) 
				{
					int key = cursor.getPosition();
					if(checkedList.get(key))
						checkedList.delete(key);
					else
						checkedList.append(key, true);
				}
			});
	    }
	}
	
	private interface UsersQuery 
	{
	    String[] PROJECTION = {
	    			ScheduleContract.User._ID,
	    		 	ScheduleContract.User.USER_NAME,
	    		 	ScheduleContract.User.USER_GROCERIES,
	    		 	ScheduleContract.User.USER_TASKS,
	    		 	ScheduleContract.User.USER_BILLS,
	    		 	ScheduleContract.User.USER_LETTER,
	    		 	ScheduleContract.User.USER_COLOR,
	    };
	}
}
