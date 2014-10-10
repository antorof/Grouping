package com.example.roommates;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.roommates.Utils.UIUtils;

/**
 * Activity básico que maneja la funcionalidad básica y común de cualquier activity:
 * fadein, fadeout, navdrawer, etc
 * @author DavidGSola
 *
 */
public abstract class BaseActivity extends Activity  
{
	/**
	 * Handler para retrasar el cambio entre activities
	 */
    private Handler mHandler;	
	
    /**
     * NavDrawer
     */
	private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
	
    /**
     * Viewgroup con los elementos del navdrawer
     */
    private ViewGroup mDrawerItemsListContainer;
    
    /**
     *  Listado en orden de los navdrawer items que se han añadido al navdrawer
     */
    private ArrayList<Integer> mNavDrawerItems = new ArrayList<Integer>();

    /**
     *  Views que corresponden con cada navdrawer item, null si no ha sido creado
     */
    private View[] mNavDrawerItemViews = null;
    
    // Lista con todos los posibles items que puede haber en el navdrawer.
    protected static final int NAVDRAWER_ITEM_SUMMARY = 0;
    protected static final int NAVDRAWER_ITEM_GROCERIES = 1;
    protected static final int NAVDRAWER_ITEM_TASKS = 2;
    protected static final int NAVDRAWER_ITEM_BILLS = 3;
    protected static final int NAVDRAWER_ITEM_ROOMMATES = 4;
    protected static final int NAVDRAWER_ITEM_SETTINGS = 5;
    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;

    // Títulos de los posibles items del navdrawer 
    //(los indices deben corresponder con los de la lista superior)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{
            R.string.navdrawer_item_summary,
            R.string.navdrawer_item_groceries,
            R.string.navdrawer_item_tasks,
            R.string.navdrawer_item_bills,
            R.string.navdrawer_item_roommates,
            R.string.navdrawer_item_settings,
    };
    
    // Iconos de los posibles items del navdrawer
    //(los indices deben corresponder con los de la lista superior)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[] {
            R.drawable.ic_drawer_launcher,  // Summary
            R.drawable.ic_drawer_launcher,  // Groceries
            R.drawable.ic_drawer_launcher, // Tasks
            R.drawable.ic_drawer_launcher, // Bills
            R.drawable.ic_drawer_launcher, // Roommates
            R.drawable.ic_drawer_launcher, // Settings
    };
    
    // Delay para lanzar un item del navdrawer para permitir realizar la animación de cerrado
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    // Fade in y fade out para la hora de cambiar entre las diferentes actividades
    // a través del NavDrawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		mHandler = new Handler();
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) 
	{
		super.onPostCreate(savedInstanceState);
		
		ActionBar ab = getActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        // Crear la navdrawer
		setupNavDrawer();
		
        // Fadein
       /* View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) 
        {
            mainContent.setAlpha(0);
            mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        }*/ 	
	 }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		getMenuInflater().inflate(R.menu.base, menu);
		return true;
	}

	@Override
    public void onConfigurationChanged(Configuration newConfig) 
	{
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// Se delega el evento al ActionBarDrawerToggle, si devuelve
    	// true se ha maejado el evento sobre el icono de la app
        if (mDrawerToggle.onOptionsItemSelected(item))
          return true;
        
        // Manejar el resto de acciones sobre los items de la actionbar

        return super.onOptionsItemSelected(item);
    }

    /**
     * Devuelve le item del navdrawer que corresponde con la Activity actual.
     * Las subclases de BaseActivity deben sobreescribir este método con su
     * propio item.
     * @return NAVDRAWER_ITEM_INVALID para indicar que este Activity no tiene item en el navdrawer
     */
    protected int getSelfNavDrawerItem() 
    {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * Deuvelve si un item del navdrawer es de tipo especial
     * @param itemId
     * @return true: especial
     */
    private boolean isSpecialItem(final int itemId)
    {
    	return itemId == NAVDRAWER_ITEM_SETTINGS;
    }
    
    /**
     * Setup el navdrawer apropiadamente. Aquí podemos hacer que el navdrawer se cree
     * de manera diferente dependiende de los settings o de la activity donde nos 
     * encontremos.
     */
	private void setupNavDrawer()
	{
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (mDrawerLayout == null)
			return;
		
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.START);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Se llama cuando el drawer se ha cerrado completamente. */
            @Override
			public void onDrawerClosed(View view) 
            {
                super.onDrawerClosed(view);
                getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // recrear el menú de opciones
            }

            /** Se llama cuando el drawer se ha abierto completamente. */
            @Override
			public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle(getString(R.string.app_name));
                invalidateOptionsMenu(); // recrear el menú de opciones
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerToggle.syncState();
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // Populate el navdrawer con los items que correspondan
        populateNavDrawer();
	}
	
	/**
	 * Crea el navdrawer con los items que correspondan
	 */
	private void populateNavDrawer()
	{
        mNavDrawerItems.clear();
        
        mNavDrawerItems.add(NAVDRAWER_ITEM_SUMMARY);
        
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        
        mNavDrawerItems.add(NAVDRAWER_ITEM_GROCERIES);
        mNavDrawerItems.add(NAVDRAWER_ITEM_TASKS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_BILLS);
        mNavDrawerItems.add(NAVDRAWER_ITEM_ROOMMATES);
        
        mNavDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        
        mNavDrawerItems.add(NAVDRAWER_ITEM_SETTINGS);
        
        createNavDrawerItems();
	}
	
	/**
	 * Crea los diferentes items del navdrawer
	 */
	private void createNavDrawerItems()
	{
		mDrawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer);
		if (mDrawerItemsListContainer == null)
			return;

		mNavDrawerItemViews = new View[mNavDrawerItems.size()];
		mDrawerItemsListContainer.removeAllViews();
	    
		// Crea cada item de la lista de items a crear
	    int i = 0;
	    for (int itemId : mNavDrawerItems) 
	    {
	    	mNavDrawerItemViews[i] = makeNavDrawerItem(itemId, mDrawerItemsListContainer);
	        mDrawerItemsListContainer.addView(mNavDrawerItemViews[i]);
	        ++i;
	    }
	}
	
	/**
	 * Crea un item del navdrawer con su icono y texto correspondiente
	 * @param itemId id del item
	 * @param container viewgroup del navdrawer
	 * @return view del item
	 */
	private View makeNavDrawerItem(final int itemId, ViewGroup container) 
	{
        boolean selected = getSelfNavDrawerItem() == itemId;
        int layoutToInflate = 0;
    
        if (itemId == NAVDRAWER_ITEM_SEPARATOR)
            layoutToInflate = R.layout.navdrawer_separator;
        else
            layoutToInflate = R.layout.navdrawer_item;
        
        View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (itemId == NAVDRAWER_ITEM_SEPARATOR)
        {
            UIUtils.setAccessibilityIgnore(view);
            return view;
        }

        
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        // Comprobar que no nos salimos del vector (haber introducido un item que no se espera)
        int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ?
                NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ?
                NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // Setear el icono y el texto
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) 
            iconView.setImageResource(iconId);
        
        titleView.setText(getString(titleId));

        // Formatear el item
        formatNavDrawerItem(view, itemId, selected);

        // Asociar el listener
        view.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
                onNavDrawerItemClicked(itemId);
            }
        });

        return view;
    }
	
	/**
	 * Formatea un item del navdrawer dependiendo de si es el item de la activity
	 * actual o no
	 * @param view view
	 * @param itemId id del item
	 * @param selected 
	 */
    private void formatNavDrawerItem(View view, int itemId, boolean selected) 
    {
        if (itemId == NAVDRAWER_ITEM_SEPARATOR)
            return;

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ?
                getResources().getColor(R.color.navdrawer_text_color_selected) :
                getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ?
                getResources().getColor(R.color.navdrawer_icon_tint_selected) :
                getResources().getColor(R.color.navdrawer_icon_tint));
    }
    
    /**
     * Función asociada al listener de los items del navdrawer.
     * @param itemId id del item seleccionado del navdrawer
     */
    private void onNavDrawerItemClicked(final int itemId) 
    {
    	// Si es el mismo cerramos el navdrawer
    	if (itemId == getSelfNavDrawerItem()) 
    	{
    		mDrawerLayout.closeDrawer(Gravity.START);
    		return;
    	}
	
    	// Si es un item especial (settings) no hacemos animación
    	if (isSpecialItem(itemId))
    		goToNavDrawerItem(itemId);
    	else 
    	{
    		// Lanzamos la actividad después de un tiempo para permitir realizar la animación de fadeout
		    mHandler.postDelayed(new Runnable()
		    {
		        @Override
		        public void run() {
		            goToNavDrawerItem(itemId);
		        }
		    }, NAVDRAWER_LAUNCH_DELAY);
	
		    setSelectedNavDrawerItem(itemId);
		 
		    // fadeout
/*		    View mainContent = findViewById(R.id.main_content);
		    if (mainContent != null) 
		    {
		        mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
		    }*/
    	}
	
    	mDrawerLayout.closeDrawer(Gravity.START);
    }
    
    /**
     * Setear el item del navdrawer que ha sido seleccionado
     * @param itemId
     */
    private void setSelectedNavDrawerItem(int itemId)
    {
        if (mNavDrawerItemViews != null) 
        {
            for (int i = 0; i < mNavDrawerItemViews.length; i++) 
            {
                if (i < mNavDrawerItems.size()) 
                {
                    int thisItemId = mNavDrawerItems.get(i);
                    formatNavDrawerItem(mNavDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }
    
    /**
     * Funcionalidad de los items del navdrawer
     * @param item id del item del navdrawer seleccionado
     */
    private void goToNavDrawerItem(int item) {
        Intent intent;
        switch (item) 
        {
	        case NAVDRAWER_ITEM_GROCERIES:
	            intent = new Intent(this, GroceriesActivity.class);
	            startActivity(intent);
	            finish();
	            break;
	        case NAVDRAWER_ITEM_BILLS:
	            intent = new Intent(this, BillsActivity.class);
	            startActivity(intent);
	            finish();
	            break;
	        case NAVDRAWER_ITEM_TASKS:
	            intent = new Intent(this, TasksActivity.class);
	            startActivity(intent);
	            finish();
	            break;
            case NAVDRAWER_ITEM_ROOMMATES:
                intent = new Intent(this, RoommatesActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
