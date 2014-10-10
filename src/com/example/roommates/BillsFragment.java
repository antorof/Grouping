package com.example.roommates;

import java.util.ArrayList;

import models.ModelBase;
import models.ModelProduct;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.roommates.Utils.SortByDate;
import com.example.roommates.Utils.UIUtils;

import database.ScheduleContract.Product;
import database.ScheduleContract.User;
import database.ScheduleDatabase;
import database.ScheduleDatabase.Tables;


/**
 * Fragmento con una lista de productos. Permite la multiselección de los diferentes items
 * de la lista así como su eliminación y modificar el estado un producto entre urgente
 * y no urgente.
 * @author DavidGSola
 *
 */
public class BillsFragment extends BaseFragment
{

	/**
	 * Adapter de la lista
	 */
	protected GroceriesAdapter mAdapter = null;
	
	/**
	 * Devuelve una instancia de BillsFragment
	 * @return
	 */
	public static BillsFragment newInstance()
	{
		return new BillsFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
	    return inflater.inflate(R.layout.fragment_groceries, container, false);
	}

	@Override
	protected void createList(View view, SQLiteDatabase db)
	{
		lista = (ListView) view.findViewById(R.id.list_groceries);
		
		// Añadir un header y un footer a la lista (esto hace que los
		// elementos de la lista empiecen en la posición 1 ya que el header
		// es la posición 0.
		UIUtils.addHeaderFooterDivider(lista, getActivity());
		
		productos = new ArrayList<ModelProduct>();
		
		// La consulta a la BD varía de si es una lista urgente o no
		Cursor cursor = getItems(db, isSpecialList);
		while(cursor.moveToNext())
			productos.add((ModelProduct)createItemFromCursor(cursor, isSpecialList));
		
		cursor.close();
		
		mAdapter = new GroceriesAdapter(getActivity(), productos, db);
		mAdapter.sort(new SortByDate());

		lista.setAdapter(mAdapter);
		lista.setClickable(true);
		lista.setOnItemClickListener(new OnItemClickListener() 
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				final ImageView productPurchaserIcon = (ImageView) view.findViewById(R.id.product_purchaser);
				final ImageView selectedIcon = (ImageView) view.findViewById(R.id.product_purchaser_selected);
				CheckBox checkBox = (CheckBox) view.findViewById(R.id.product_check_box);

				// Cambiamos el estado del checkbox. Dependiendo del nuevo estado se
				// modifica el diseño del item para que el usuario conozca el estado
				checkBox.toggle();
				
				animateIcons(productPurchaserIcon, selectedIcon, checkBox.isChecked(), view);
			}
		});
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) 
	{
		final ScheduleDatabase mDBHelper = new ScheduleDatabase(getActivity());		
		final SQLiteDatabase db = mDBHelper.getReadableDatabase();

		init(view);
		createList(view, db);
	}
	
	@Override
	protected Cursor getItems(SQLiteDatabase db, boolean special) 
	{
		if(special)
			return db.query(Tables.PRODUCTS, GroceriesQuery.PROJECTION, 
					Product.PRODUCT_URGENT+">0", null, null, null, null /*order*/);
		else
			return db.query(Tables.PRODUCTS, GroceriesQuery.PROJECTION, 
					Product.PRODUCT_URGENT+"=0", null, null, null, null /*order*/);
	}
	
	@Override
	protected ModelBase createItemFromCursor(Cursor cursor, Boolean special) 
	{
		String productName = cursor.getString(cursor.getColumnIndexOrThrow(Product.PRODUCT_NAME));
		String productSubname = cursor.getString(cursor.getColumnIndexOrThrow(Product.PRODUCT_SUBNAME));
		int productDay = cursor.getInt(cursor.getColumnIndexOrThrow(Product.PRODUCT_DAY));
		int productMonth = cursor.getInt(cursor.getColumnIndexOrThrow(Product.PRODUCT_MONTH));
		int productPurchaserId = cursor.getInt(cursor.getColumnIndexOrThrow(Product.PRODUCT_PURCHASER_ID));
		int productId = cursor.getInt(cursor.getColumnIndexOrThrow(Product._ID));
		
		return new ModelProduct(productName, productSubname, productDay, productMonth, productPurchaserId, special, productId);
	
	}

	@Override
	protected void deleteItem(String id) 
	{
		ScheduleDatabase mDBHelper = new ScheduleDatabase(getActivity());
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		db.delete(Tables.PRODUCTS, Product._ID+"=?", 
				new String[]{id});
		
		db.close();
			
	}

	@Override
	protected void toggleSpecialItem(String id) 
	{
		ScheduleDatabase mDBHelper = new ScheduleDatabase(getActivity());
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Product.PRODUCT_URGENT, isSpecialList ? 0 : 1);
		
		db.update(Tables.PRODUCTS, values, Product._ID+"=?", 
				new String[]{id});
			
		db.close();
	}

	@Override
	protected void addItemToAdapter(ModelBase item) 
	{
		mAdapter.add((ModelProduct) item);
	}

	@Override
	protected void deleteItemFromAdapter(ModelBase item) 
	{
		mAdapter.remove((ModelProduct) item);
	}
	
	@Override
	protected void doneCurrentSelectedItems()
	{
		ScheduleDatabase mDBHelper = new ScheduleDatabase(getActivity());
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
    	int size = checkedList.size();
    	int i=0;
		ArrayList<ModelProduct> deleteFromAdapter = new ArrayList<ModelProduct>();

		Cursor cursorUser = db.query(Tables.USERS, UserQuery.PROJECTION, 
				null, null, null, null, null /*order*/);
		
    	while(i<size)
    	{
    		// La lista empieza en el id 1, por lo tanto es necesario un +1 
    		View rowView = lista.getChildAt(checkedList.keyAt(i)+1);

    		cursorUser.moveToPosition(-1);
    		Cursor cursorProduct = db.query(Tables.PRODUCTS, GroceriesQuery.PROJECTION, 
					Product._ID+"="+rowView.getTag(), null, null, null, null /*order*/);
    			
    		if(cursorProduct.moveToFirst())
    		{
    			int previousPurchaserId = cursorProduct.getInt(cursorProduct.getColumnIndexOrThrow(Product.PRODUCT_PURCHASER_ID));
    			int nextPurchaserId = -1;
    			boolean found = false;

        		while(cursorUser.moveToNext() && !found)
        		{
        			if(cursorUser.getInt(cursorUser.getColumnIndexOrThrow(User._ID)) == previousPurchaserId)
        			{
        				found = true;
        				// Si lo encontramos hay que coger el siguiente
        				if(!cursorUser.moveToNext())
        					cursorUser.moveToFirst();
        				nextPurchaserId = cursorUser.getInt(cursorUser.getColumnIndexOrThrow(User._ID));
        			}
        		}
        		
	    		ContentValues values = new ContentValues();
	    		values.put(Product.PRODUCT_URGENT, 0);
	    		values.put(Product.PRODUCT_PURCHASER_ID, nextPurchaserId);
	    		values.put(Product.PRODUCT_DAY, UIUtils.getDay());
	    		values.put(Product.PRODUCT_MONTH, UIUtils.getMonth());
	    		
	    		db.update(Tables.PRODUCTS, values, Product._ID+"=?", 
						new String[]{String.valueOf(rowView.getTag())});
	    		
	    		ModelProduct mp = (ModelProduct) lista.getItemAtPosition((checkedList.keyAt(i)+1));

    			deleteFromAdapter.add(mp);
	    		mp.purchaser_id = nextPurchaserId;
	    		mp.special = false;
	    		mp.day = UIUtils.getDay();
	    		mp.month = UIUtils.getMonth();
	    	
	    		// Si no nos encontramos en una lista urgente hay que añadir este 
	    		// elemento al actuar adapter
    			if(!isSpecialList)
    				mAdapter.add(mp);
    		}
    		cursorProduct.close();
    		i++;
       	}

		cursorUser.close();
    	checkedList.clear();
    	
    	for(ModelProduct p : deleteFromAdapter)
    	{
    		if(isSpecialList)
    			mCallback.OnListItemMoved(this, p);
    		mAdapter.remove(p);
    	}

    	mAdapter.sort(new SortByDate());
		mAdapter.notifyDataSetChanged();
    	db.close();
    	
    	String mensaje = isSpecialList ? getResources().getString(R.string.toast_deleted_urgent) :
										getResources().getString(R.string.toast_added_urgent);
    	UIUtils.crearToast(deleteFromAdapter.size() + " " + mensaje, getActivity());
	}
	
	@Override
	protected void sortAndNotifyAdapter() 
	{
    	mAdapter.sort(new SortByDate());
		mAdapter.notifyDataSetChanged();	
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) 
	{
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.groceries_menu, menu);
		return true;
	}

	
	/**
	 * Adapter para una lista de {@link ModelProduct}
	 * @author DavidGSola
	 *
	 */
	public class GroceriesAdapter extends ArrayAdapter<ModelProduct>
	{
		private final Context context;
		private final SQLiteDatabase db;
		
		public GroceriesAdapter(Context context, ArrayList<ModelProduct> values, SQLiteDatabase db) 
		{
			super(context, R.layout.list_item_groceries, values);
			this.context = context;
			this.db = db;
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{	
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_item_groceries, parent, false);

			ModelProduct product = getItem(position);
			
			rowView.setTag(product.id);

			TextView textName = (TextView) rowView.findViewById(R.id.product_name);
			TextView textSubame = (TextView) rowView.findViewById(R.id.product_subname);
			TextView textDate = (TextView) rowView.findViewById(R.id.product_date);
			ImageView image = (ImageView) rowView.findViewById(R.id.product_purchaser);
			CheckBox checkbox = (CheckBox) rowView.findViewById(R.id.product_check_box);
			
			textName.setText(product.name);
			textSubame.setText(product.subname);
			textDate.setText(UIUtils.formatDate(product.day, product.month));

			// Obtener el Usuario que corresponda con el purchaser_id
			Cursor cursor = db.query(
					Tables.USERS, 
					UserQuery.PROJECTION, User._ID+"=?", 
					new String[]{String.valueOf(product.purchaser_id)}, 
					null, null, null);
			
			// Lo movemos al primer elemento ya que después de hacer un query el cursor 
			// apunta al elemento -1
			cursor.moveToFirst();
			if(cursor.getCount() != 0)
			{
				String color = cursor.getString(cursor.getColumnIndexOrThrow(User.USER_COLOR));
				image.setColorFilter(UIUtils.getColor(getActivity(), color));
			}
			
			checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
			{
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) 
				{
					// Si ya existe lo eliminamos pues lo estamos deseleccionando
					// Si no lo añadimos ya que estamos seleccionandolo
					if(checkedList.get(position))
						checkedList.delete(position);
					else
						checkedList.append(position, true);
					
					// Si se han deseleccionado todos los items se finaliza la barra de acción contextual
					if(checkedList.size() == 0 && mActionMode != null)
						mActionMode.finish();
					else if(mActionMode == null) // Si no ha sido inicializa aún se inicia la barra de acción
						mActionMode = getActivity().startActionMode(BillsFragment.this);
				}
			});
			
			return rowView;
		}

	}
	
	
	private interface GroceriesQuery 
	{
	    String[] PROJECTION = {
	    			Product._ID,
	    		 	Product.PRODUCT_NAME,
	    		 	Product.PRODUCT_SUBNAME,
	    		 	Product.PRODUCT_DAY,
	    		 	Product.PRODUCT_MONTH,
	    		 	Product.PRODUCT_PURCHASER_ID,
	    		 	Product.PRODUCT_URGENT,
	    };
	}

	private interface UserQuery
	{
		String[] PROJECTION = {
	    			User._ID,
	    		 	User.USER_COLOR,
	    		 	User.USER_LETTER,
		};
	}
}
