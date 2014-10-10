package com.example.roommates;

import java.util.ArrayList;

import models.ModelProduct;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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
public class GroceriesFragment extends Fragment implements ActionMode.Callback
{
	public interface OnListChangedListener
	{
		public void OnListItemMoved(Fragment fragment, ModelProduct product);
	}
	
	public static final String ARG_SECTION_NUMBER 	= "section_number";
	public static final String ARG_URGENT_LIST 		= "urgent_list";
	private static final int ANIM_ROTATE_DURATION 	= 300;
	
	/**
	 * Callback para los cambios en la lista
	 */
	private OnListChangedListener mCallback;
	
	/**
	 * Handler para lanzar las animaciones
	 */
	private Handler mHandler;
	
	private boolean isUrgentList;
	
	/**
	 * Lista de los productos de la lista del fragment
	 */
	private ArrayList<ModelProduct> productos;
	
	/**
	 * View de la lista del fragment
	 */
	private ListView lista;
	
	/**
	 * Adapter de la lista
	 */
	private GroceriesAdapter mAdapter = null;
	
	/**
	 * Mantiene una referencia mediante la posición en la lista de los
	 * items que estén siendo seleccionados
	 */
	private SparseBooleanArray checkedList = new SparseBooleanArray();

	private ImageButton mAddButton;
	
	/**
	 * Referencia para mostrar la action bar contextual
	 */
	private ActionMode mActionMode = null;
	
	/**
	 * Devuelve una instancia de GroceriesFragment
	 * @return
	 */
	public static GroceriesFragment newInstance()
	{
		return new GroceriesFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
	    return inflater.inflate(R.layout.fragment_groceries, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) 
	{
		mHandler = new Handler();
		
		Bundle bundle = getArguments();
		isUrgentList = bundle.getBoolean(ARG_URGENT_LIST);
		lista = (ListView) view.findViewById(R.id.list_groceries);
		
		// Añadir un header y un footer a la lista (esto hace que los
		// elementos de la lista empiecen en la posición 1 ya que el header
		// es la posición 0.
		UIUtils.addHeaderFooterDivider(lista, getActivity());
		
		final ScheduleDatabase mDBHelper = new ScheduleDatabase(getActivity());		
		final SQLiteDatabase db = mDBHelper.getReadableDatabase();

		productos = new ArrayList<ModelProduct>();
		
		// La consulta a la BD varía de si es una lista urgente o no
		Cursor cursor = getProducts(db, isUrgentList);
		while(cursor.moveToNext())
			productos.add(createProductFromCursor(cursor, isUrgentList));
		
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
				 
				Animation rotate_out = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_item_out);
		        Animation rotate_in = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_item_in);
		        rotate_out.setDuration(ANIM_ROTATE_DURATION);
		        rotate_in.setDuration(ANIM_ROTATE_DURATION);
		            
				if(checkBox.isChecked())
				{
					productPurchaserIcon.startAnimation(rotate_out);
		            selectedIcon.startAnimation(rotate_in);
					
		            selectedIcon.setVisibility(View.VISIBLE);
					// Volvemos a hacerlo invisible una vez termina la animación
				    mHandler.postDelayed(new Runnable()
				    {
				        @Override
				        public void run() 
				        {
							productPurchaserIcon.setVisibility(View.INVISIBLE);
				        }
				    }, ANIM_ROTATE_DURATION);
				    
					view.setBackgroundColor(getResources().getColor(R.color.list_item_selected));
				}else
				{
		            productPurchaserIcon.startAnimation(rotate_in);
		            selectedIcon.startAnimation(rotate_out);
					
		            productPurchaserIcon.setVisibility(View.VISIBLE);
					// Volvemos a hacerlo invisible una vez termina la animación
				    mHandler.postDelayed(new Runnable()
				    {
				        @Override
				        public void run() 
				        {
				        	selectedIcon.setVisibility(View.INVISIBLE);
				        }
				    }, ANIM_ROTATE_DURATION);
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
                mDBHelper.onUpgrade(db, 1, 1);
                añadirValoresBD(mDBHelper);
            	mAdapter.sort(new SortByDate());
                mAdapter.notifyDataSetChanged();
            }
        });
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
	    super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onAttach(Activity activity)
	{
        super.onAttach(activity);
        
		try
		{
			 mCallback = (OnListChangedListener) activity;
		}catch(ClassCastException e)
		{
			throw new ClassCastException(activity.toString()
            + " must implement OnHeadlineSelectedListener");
		}
	}
	
	/**
	 * Realiza una consulta a la BD devolviendo un cursor a todos los productos
	 * (urgentes o no dependiendo del valor de urgent
	 * @param db
	 * @param urgent true: obtener productos urgentes
	 * 				 false: obtener productos no urgentes
	 * @return Cursor con la consulta realizada
	 */
	private Cursor getProducts(SQLiteDatabase db, boolean urgent)
	{
		if(urgent)
			return db.query(Tables.PRODUCTS, GroceriesQuery.PROJECTION, 
					Product.PRODUCT_URGENT+">0", null, null, null, null /*order*/);
		else
			return db.query(Tables.PRODUCTS, GroceriesQuery.PROJECTION, 
					Product.PRODUCT_URGENT+"=0", null, null, null, null /*order*/);
	}
	
	/**
	 * Devuelve un nuevo {@link ModelProducto} leído a través de un cursor
	 * @param cursor
	 * @return {@link ModelProduct} creado
	 */
	private ModelProduct createProductFromCursor(Cursor cursor, Boolean urgente)
	{
		String productName = cursor.getString(cursor.getColumnIndexOrThrow(Product.PRODUCT_NAME));
		String productSubname = cursor.getString(cursor.getColumnIndexOrThrow(Product.PRODUCT_SUBNAME));
		int productDay = cursor.getInt(cursor.getColumnIndexOrThrow(Product.PRODUCT_DAY));
		int productMonth = cursor.getInt(cursor.getColumnIndexOrThrow(Product.PRODUCT_MONTH));
		int productPurchaserId = cursor.getInt(cursor.getColumnIndexOrThrow(Product.PRODUCT_PURCHASER_ID));
		int productId = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID));
		
		return new ModelProduct(productName, productSubname, productDay, productMonth, productPurchaserId, urgente, productId);
	}

	/**
	 * Elimina los items que estén seleccionados actualmente
	 */
	private void deleteCurrentSelectedItems()
	{
		ScheduleDatabase mDBHelper = new ScheduleDatabase(getActivity());
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
    	int size = checkedList.size();
    	int i=0;
		ArrayList<ModelProduct> eliminar = new ArrayList<ModelProduct>();
		
    	while(i<size)
    	{
    		// La lista empieza en el id 1, por lo tanto es necesario un +1 
    		View rowView = lista.getChildAt(checkedList.keyAt(i)+1);
    		
    		db.delete(Tables.PRODUCTS, BaseColumns._ID+"=?", 
					new String[]{String.valueOf(rowView.getTag())});
    			
			// Añadimos a la lista eliminar los ModelProduct que vayamos a eliminar
    		// del listView
    		// Cuando se terminen de eliminar todos los productos de la base de datos,
    		// se eliminarán del adaptador utilizando esta lista creada. Esto se hace así
    		// para evitar que se vaya eliminando de acuerdo a la posición que ocupaban 
    		// en la lista ya que esta posición ira variendo conforme se vayan realizando
    		// eliminaciones.
    		eliminar.add((ModelProduct) lista.getItemAtPosition((checkedList.keyAt(i)+1)));
    		i++;
       	}
    	
    	checkedList.clear();
    	
    	for(ModelProduct p : eliminar)
    		mAdapter.remove(p);

    	mAdapter.sort(new SortByDate());
		mAdapter.notifyDataSetChanged();
    	db.close();
	}
	
	private void toggleUrgentCurrentSelectedItems()
	{
		ScheduleDatabase mDBHelper = new ScheduleDatabase(getActivity());
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
    	int size = checkedList.size();
    	int i=0;
		ArrayList<ModelProduct> eliminar = new ArrayList<ModelProduct>();
		
    	while(i<size)
    	{
    		// La lista empieza en el id 1, por lo tanto es necesario un +1 
    		View rowView = lista.getChildAt(checkedList.keyAt(i)+1);

    		ContentValues values = new ContentValues();
    		values.put(Product.PRODUCT_URGENT, isUrgentList ? 0 : 1);
    		
    		db.update(Tables.PRODUCTS, values, BaseColumns._ID+"=?", 
					new String[]{String.valueOf(rowView.getTag())});
    			
			// Añadimos a la lista eliminar los ModelProduct que vayamos a eliminar
    		// del listView
    		// Cuando se terminen de eliminar todos los productos de la base de datos,
    		// se eliminarán del adaptador utilizando esta lista creada. Esto se hace así
    		// para evitar que se vaya eliminando de acuerdo a la posición que ocupaban 
    		// en la lista ya que esta posición ira variendo conforme se vayan realizando
    		// eliminaciones.
    		eliminar.add((ModelProduct) lista.getItemAtPosition((checkedList.keyAt(i)+1)));
    		i++;
       	}
    	
    	checkedList.clear();
    	
    	for(ModelProduct p : eliminar)
    	{
    		mCallback.OnListItemMoved(this, p);
    		mAdapter.remove(p);
    	}

    	mAdapter.sort(new SortByDate());
		mAdapter.notifyDataSetChanged();
    	db.close();
    	
    	String mensaje = isUrgentList ? getResources().getString(R.string.toast_deleted_urgent) :
										getResources().getString(R.string.toast_added_urgent);
    	UIUtils.crearToast(eliminar.size() + " " + mensaje, getActivity());
	}
	
	private void purchasedCurrentSelectedItems()
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
					BaseColumns._ID+"="+rowView.getTag(), null, null, null, null /*order*/);
    			
    		if(cursorProduct.moveToFirst())
    		{
    			int previousPurchaserId = cursorProduct.getInt(cursorProduct.getColumnIndexOrThrow(Product.PRODUCT_PURCHASER_ID));
    			int nextPurchaserId = -1;
    			boolean found = false;

        		while(cursorUser.moveToNext() && !found)
        		{
        			if(cursorUser.getInt(cursorUser.getColumnIndexOrThrow(BaseColumns._ID)) == previousPurchaserId)
        			{
        				found = true;
        				// Si lo encontramos hay que coger el siguiente
        				if(!cursorUser.moveToNext())
        					cursorUser.moveToFirst();
        				nextPurchaserId = cursorUser.getInt(cursorUser.getColumnIndexOrThrow(BaseColumns._ID));
        			}
        		}
        		
	    		ContentValues values = new ContentValues();
	    		values.put(Product.PRODUCT_URGENT, 0);
	    		values.put(Product.PRODUCT_PURCHASER_ID, nextPurchaserId);
	    		values.put(Product.PRODUCT_DAY, UIUtils.getDay());
	    		values.put(Product.PRODUCT_MONTH, UIUtils.getMonth());
	    		
	    		db.update(Tables.PRODUCTS, values, BaseColumns._ID+"=?", 
						new String[]{String.valueOf(rowView.getTag())});
	    		
	    		ModelProduct mp = (ModelProduct) lista.getItemAtPosition((checkedList.keyAt(i)+1));

    			deleteFromAdapter.add(mp);
	    		mp.purchaser_id = nextPurchaserId;
	    		mp.special = false;
	    		mp.day = UIUtils.getDay();
	    		mp.month = UIUtils.getMonth();
	    	
	    		// Si no nos encontramos en una lista urgente hay que añadir este 
	    		// elemento al actuar adapter
    			if(!isUrgentList)
    				mAdapter.add(mp);
    		}
    		cursorProduct.close();
    		i++;
       	}

		cursorUser.close();
    	checkedList.clear();
    	
    	for(ModelProduct p : deleteFromAdapter)
    	{
    		if(isUrgentList)
    			mCallback.OnListItemMoved(this, p);
    		mAdapter.remove(p);
    	}

    	mAdapter.sort(new SortByDate());
		mAdapter.notifyDataSetChanged();
    	db.close();
    	
    	String mensaje = isUrgentList ? getResources().getString(R.string.toast_deleted_urgent) :
										getResources().getString(R.string.toast_added_urgent);
    	UIUtils.crearToast(deleteFromAdapter.size() + " " + mensaje, getActivity());
	}
	
	
	/**
	 * Add item to the adapter
	 * @param product
	 */
	public void addItemAdapter(ModelProduct product)
	{
		if(mAdapter != null)
		{
			mAdapter.add(product);
			mAdapter.sort(new SortByDate());
			mAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Finaliza la barra de acción contextual
	 */
	public void closeContextualMode()
	{
		if(mActionMode != null)
			mActionMode.finish();
	}
	
	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) 
	{
		switch (item.getItemId()) 
		{
	        case R.id.action_purchased:
	            purchasedCurrentSelectedItems();
	            mode.finish(); // Action picked, so close the Contextual Action Bar
	            return true;
			case R.id.action_urgent:
	            toggleUrgentCurrentSelectedItems();
	            mode.finish(); // Action picked, so close the Contextual Action Bar
	            return true;
	        case R.id.action_delete:
	            deleteCurrentSelectedItems();
	            mode.finish(); // Action picked, so close the Contextual Action Bar
	            return true;
	        default:
	            return false;
		}
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) 
	{
		MenuInflater inflater = mode.getMenuInflater();
		inflater.inflate(R.menu.groceries_menu, menu);
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) 
	{
		// Deselecciona todos los items, para ello es necsario realizar un click sobre cada item que esté 
		// en la lista de seleccionados
		int i=0;
		while(i<checkedList.size())
		{
    		// La lista empieza en el id 1 porque tiene un header, por lo tanto es necesario un +1
    		int position = checkedList.keyAt(i)+1;
    		lista.performItemClick(lista.getChildAt(position), position, lista.getItemIdAtPosition(position));
    	}
		
		checkedList.clear();
		mActionMode = null;
	}
	
	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) 
	{
		return false;
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
					UserQuery.PROJECTION, BaseColumns._ID+"=?", 
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
						mActionMode = getActivity().startActionMode(GroceriesFragment.this);
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

	/**
	 * ELIMINAR: TESTING PARA PROBAR CON UNA BASE DE DATOS
	 * @param mDBHelper
	 */
	private void añadirValoresBD(ScheduleDatabase mDBHelper)
	{
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Product.PRODUCT_NAME, "Pañales");
		values.put(Product.PRODUCT_SUBNAME, "Que sean Golden!");
		values.put(Product.PRODUCT_DAY, "16");
		values.put(Product.PRODUCT_MONTH, "8");
		values.put(Product.PRODUCT_PURCHASER_ID, 1);
		values.put(Product.PRODUCT_URGENT, 0);
		
		db.insert(Tables.PRODUCTS, null, values);
		values.clear();

		values.put(Product.PRODUCT_NAME, "Caca dsad sasadsa dsasad sads");
		values.put(Product.PRODUCT_SUBNAME, "Que sean Goldenas dsadsa dsad sadsa dsad sad sadsadsa dsad sadsadsadsa dsad sad sadsa dsadsa dsa dsad sad sadsad sad sadsa dsadsadsadsad sad sadsad sadsad sad sadsa ds!");
		values.put(Product.PRODUCT_DAY, "2");
		values.put(Product.PRODUCT_MONTH, "5");
		values.put(Product.PRODUCT_PURCHASER_ID, 1);
		values.put(Product.PRODUCT_URGENT, 1);
		
		db.insert(Tables.PRODUCTS, null, values);
		values.clear();

		values.put(Product.PRODUCT_NAME, "Pimienta");
		values.put(Product.PRODUCT_SUBNAME, "Que sean Golden!");
		values.put(Product.PRODUCT_DAY, "05");
		values.put(Product.PRODUCT_MONTH, "9");
		values.put(Product.PRODUCT_PURCHASER_ID, 1);
		values.put(Product.PRODUCT_URGENT, 1);
		
		db.insert(Tables.PRODUCTS, null, values);
		values.clear();

		values.put(Product.PRODUCT_NAME, "Sopa");
		values.put(Product.PRODUCT_SUBNAME, "Que sean Golden!");
		values.put(Product.PRODUCT_DAY, "8");
		values.put(Product.PRODUCT_MONTH, "12");
		values.put(Product.PRODUCT_PURCHASER_ID, 2);
		values.put(Product.PRODUCT_URGENT, 1);
		
		db.insert(Tables.PRODUCTS, null, values);
		values.clear();

		values.put(Product.PRODUCT_NAME, "Filetes");
		values.put(Product.PRODUCT_SUBNAME, "Que sean Golden!");
		values.put(Product.PRODUCT_DAY, "4");
		values.put(Product.PRODUCT_MONTH, "1");
		values.put(Product.PRODUCT_PURCHASER_ID, 2);
		values.put(Product.PRODUCT_URGENT, 1);
		
		db.insert(Tables.PRODUCTS, null, values);
		values.clear();

		values.put(Product.PRODUCT_NAME, "Platanos");
		values.put(Product.PRODUCT_SUBNAME, "Que sean Golden!");
		values.put(Product.PRODUCT_DAY, "16");
		values.put(Product.PRODUCT_MONTH, "8");
		values.put(Product.PRODUCT_PURCHASER_ID, 1);
		values.put(Product.PRODUCT_URGENT, 1);
		
		db.insert(Tables.PRODUCTS, null, values);
		values.clear();
		
		values.put(User.USER_NAME, "David González Sola");
		values.put(User.USER_LETTER, "D");
		values.put(User.USER_COLOR, "orange");
		values.put(User.USER_GROCERIES, 2);
		values.put(User.USER_TASKS, 15);
		values.put(User.USER_BILLS, 20);
		
		db.insert(Tables.USERS, null, values);values.clear();
		values.clear();
		
		values.put(User.USER_NAME, "Miguel Lopez Antequera");
		values.put(User.USER_LETTER, "M");
		values.put(User.USER_COLOR, "turquesa");
		values.put(User.USER_GROCERIES, 87);
		values.put(User.USER_TASKS, 42);
		values.put(User.USER_BILLS, 31.5);
		
		db.insert(Tables.USERS, null, values);
		values.clear();
		
		values.put(User.USER_NAME, "Marta Salinas Paniza");
		values.put(User.USER_LETTER, "S");
		values.put(User.USER_COLOR, "red");
		values.put(User.USER_GROCERIES, 14);
		values.put(User.USER_TASKS, 2);
		values.put(User.USER_BILLS, 45.3);
		
		db.insert(Tables.USERS, null, values);
	}
}
