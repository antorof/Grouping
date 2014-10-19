package com.example.roommates;

import java.util.ArrayList;

import models.ModelBase;
import models.ModelProduct;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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
public abstract class BaseFragment extends Fragment implements ActionMode.Callback
{
	public interface OnListChangedListener
	{
		public void OnListItemMoved(Fragment fragment, ModelBase product);
	}
	
	public static final String ARG_SECTION_NUMBER 	= "section_number";
	public static final String ARG_SPECIAL_LIST 	= "special_list";
	protected static final int ANIM_ROTATE_DURATION = 300;
	
	/**
	 * Callback para los cambios en la lista
	 */
	protected OnListChangedListener mCallback;
	
	/**
	 * Handler para lanzar las animaciones
	 */
	protected Handler mHandler;
	
	/**
	 * Es una lista especial
	 */
	protected boolean isSpecialList;
	
	/**
	 * Lista de los productos de la lista del fragment
	 */
	protected ArrayList<ModelProduct> productos;
	
	/**
	 * View de la lista del fragment
	 */
	protected ListView lista;
	
	/**
	 * Mantiene una referencia mediante la posición en la lista de los
	 * items que estén siendo seleccionados
	 */
	protected SparseBooleanArray checkedList = new SparseBooleanArray();

	/**
	 * Botón de añadir un elemento a la aplicación
	 */
	protected ImageButton mAddButton;
	
	/**
	 * Referencia para mostrar la action bar contextual
	 */
	protected ActionMode mActionMode = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		return inflater.inflate(R.layout.fragment_groceries, container, false);
	}
	
	/**
	 * Inicia la vista y variables del fragment. 
	 * @param view
	 */
	protected void init(View view)
	{
		Bundle bundle = getArguments();
		isSpecialList = bundle.getBoolean(ARG_SPECIAL_LIST);
		
		mHandler = new Handler();
		
		// Traer al frente el botón para que no aparezca escondido trás la lista
		mAddButton = (ImageButton) view.findViewById(R.id.add_button);
		mAddButton.bringToFront();
		mAddButton.setOnClickListener( new OnClickListener() 
		{
            @Override
            public void onClick(View view) 
            {
            }
        });
	}

	/**
	 * Anima dos iconos
	 * @param item1
	 * @param item2
	 * @param isChecked
	 * @param view
	 */
	protected void animateIcons(final ImageView item1, final ImageView item2, boolean isChecked, View view)
	{	 
		Animation rotate_out = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_item_out);
        Animation rotate_in = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_item_in);
        rotate_out.setDuration(ANIM_ROTATE_DURATION);
        rotate_in.setDuration(ANIM_ROTATE_DURATION);
            
		if(isChecked)
		{
			item1.startAnimation(rotate_out);
			item2.startAnimation(rotate_in);
			
			item2.setVisibility(View.VISIBLE);
			// Volvemos a hacerlo invisible una vez termina la animación
		    mHandler.postDelayed(new Runnable()
		    {
		        @Override
		        public void run() 
		        {
		        	item1.setVisibility(View.INVISIBLE);
		        }
		    }, ANIM_ROTATE_DURATION);
		    
			view.setBackgroundColor(getResources().getColor(R.color.list_item_selected));
		}else
		{
			item1.startAnimation(rotate_in);
			item2.startAnimation(rotate_out);
			
            item1.setVisibility(View.VISIBLE);
			// Volvemos a hacerlo invisible una vez termina la animación
		    mHandler.postDelayed(new Runnable()
		    {
		        @Override
		        public void run() 
		        {
		        	item2.setVisibility(View.INVISIBLE);
		        }
		    }, ANIM_ROTATE_DURATION);
		    view.setBackgroundColor(getResources().getColor(R.color.backgroundListItem));
		}
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
			Log.v("MENSAJE", "activity " + activity.getClass().toString());
			 mCallback = (OnListChangedListener) activity;
		}catch(ClassCastException e)
		{
			throw new ClassCastException(activity.toString()
            + " must implement OnHeadlineSelectedListener");
		}
	}

	/**
	 * Crea un cursor con lo elementos de la base de datos que se deben cargar en la lista.
	 * @param db referencia a la base de datos.
	 * @param special true-> special list
	 * @return
	 */
	protected abstract Cursor getItems(SQLiteDatabase db, boolean special);
	
	/**
	 * Infla y crea la lista junto con su adapter.
	 * @param view
	 * @param db
	 */
	protected abstract void createList(View view, SQLiteDatabase db);
	
	/**
	 * Crea un elemento subclase de ModelBase a partir de una posición del cursor
	 * @param cursor
	 * @param special
	 * @return item creado subclase de ModelBase
	 */
	protected abstract ModelBase createItemFromCursor(Cursor cursor, Boolean special);

	/**
	 * Añade un elemento al adaptador
	 * @param item
	 */
	protected abstract void addItemToAdapter(ModelBase item);
	
	/**
	 * Elimina un elemento del adaptador
	 * @param item
	 */
	protected abstract void deleteItemFromAdapter(ModelBase item);
	
	/**
	 * Elimina un elemento de la base de datos
	 * @param id
	 */
	protected abstract void deleteItem(String id);
	
	/**
	 * Actualiza un elemento de la base de datos entre especial o no
	 * @param id
	 */
	protected abstract void toggleSpecialItem(String id);
	
	/**
	 * Actualiza los elementos de la base de datos como hechos
	 */
	protected abstract void doneCurrentSelectedItems();
	
	/**
	 * Elimina un elemento de la base de datos
	 * @param id
	 */
	protected abstract void createToastDelete(int size);
	
	/**
	 * Actualiza un elemento de la base de datos entre especial o no
	 * @param id
	 */
	protected abstract void createToastToggle(int size);
	
	/**
	 * Actualiza los elementos de la base de datos como hechos
	 */
	protected abstract void createToastDone(int size);
	
	/**
	 * Ordena y notifica al adaptador que se han actualizado sus elementos
	 */
	protected abstract void sortAndNotifyAdapter();
	
	/**
	 * Elimina los items que estén seleccionados actualmente
	 */
	private void deleteCurrentSelectedItems()
	{
    	int size = checkedList.size();
    	int i=0;
		ArrayList<ModelBase> eliminar = new ArrayList<ModelBase>();
		
    	while(i<size)
    	{
    		// La lista empieza en el id 1, por lo tanto es necesario un +1 
    		ModelProduct p = (ModelProduct) lista.getItemAtPosition(checkedList.keyAt(i)+1);
    		
    		
    		deleteItem(String.valueOf(p.id));
    		
    		eliminar.add((ModelProduct) lista.getItemAtPosition((checkedList.keyAt(i)+1)));
    		i++;
       	}
    	
    	checkedList.clear();
    	
    	for(ModelBase p : eliminar)
    		deleteItemFromAdapter(p);

    	sortAndNotifyAdapter();
    	createToastDelete(eliminar.size());
	}
	
	/**
	 * Actualiza los elementos seleccionados como urgentes o no urgentes
	 */
	private void toggleUrgentCurrentSelectedItems()
	{
		int size = checkedList.size();
    	int i=0;
		ArrayList<ModelBase> eliminar = new ArrayList<ModelBase>();
		
    	while(i<size)
    	{
    		// La lista empieza en el id 1, por lo tanto es necesario un +1 
    		ModelProduct p = (ModelProduct) lista.getItemAtPosition(checkedList.keyAt(i)+1);
    		

    		toggleSpecialItem(String.valueOf(p.id));
    		
    		eliminar.add((ModelProduct) lista.getItemAtPosition((checkedList.keyAt(i)+1)));
    		i++;
       	}
    	
    	checkedList.clear();
    	
    	for(ModelBase p : eliminar)
    	{
    		mCallback.OnListItemMoved(this, p);
    		deleteItemFromAdapter(p);
    	}

    	sortAndNotifyAdapter();
    	createToastToggle(eliminar.size());
	}

	/**
	 * Add item to the adapter
	 * @param product
	 */
	public void addItemAdapter(ModelBase product)
	{
		addItemToAdapter(product);
		sortAndNotifyAdapter();
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
	            doneCurrentSelectedItems();
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
	public void onDestroyActionMode(ActionMode mode) 
	{
		checkedList.clear();
		mActionMode = null;
	}
	
	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) 
	{
		return false;
	}

	/**
	 * ELIMINAR: TESTING PARA PROBAR CON UNA BASE DE DATOS
	 * @param mDBHelper
	 */
	protected static void añadirValoresBD(ScheduleDatabase mDBHelper)
	{
		SQLiteDatabase db = mDBHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(Product.PRODUCT_NAME, "Pañales2");
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
		
		values.put(Product.PRODUCT_NAME, "Cacahuetes");
		values.put(Product.PRODUCT_SUBNAME, "Que se sa dsadsa dsa dsad sad sadsad sad sadsa dsads adsads ad sad sadsad sadsad sad sadsa ds!");
		values.put(Product.PRODUCT_DAY, "2");
		values.put(Product.PRODUCT_MONTH, "5");
		values.put(Product.PRODUCT_PURCHASER_ID, 3);
		values.put(Product.PRODUCT_URGENT, 1);
		
		db.insert(Tables.PRODUCTS, null, values);
		values.clear();
		
		values.put(Product.PRODUCT_NAME, "Ratoness");
		values.put(Product.PRODUCT_SUBNAME, "Que sean Goldenas ds sad sadsa ds!");
		values.put(Product.PRODUCT_DAY, "1");
		values.put(Product.PRODUCT_MONTH, "12");
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
