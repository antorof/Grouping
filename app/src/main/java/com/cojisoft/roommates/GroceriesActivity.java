package com.cojisoft.roommates;

import com.cojisoft.models.ModelBase;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.cojisoft.roommates.BaseFragment.OnListChangedListener;

public class GroceriesActivity extends BaseActivity implements ActionBar.TabListener, OnListChangedListener
{

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v13.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	GroceriesFragment[] fragments;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groceries);
		fragments = new GroceriesFragment[2];
		fragments[0] = GroceriesFragment.newInstance();
		fragments[1] = GroceriesFragment.newInstance();
		
		// Modificamos el modo de navegación de la ActionBar a Tabs para que se
		// rendericen una serie de Tabs o pestañas
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each tab
		mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(
			new ViewPager.SimpleOnPageChangeListener() 
			{
				@Override
		        public void onPageSelected(int position) 
		        {
					actionBar.setSelectedNavigationItem(position);
		        }
			}
		);
		
        actionBar.addTab(actionBar.newTab().setText(getString(R.string.urgent_list)).setTabListener(this));
	    actionBar.addTab(actionBar.newTab().setText(getString(R.string.normal_list)).setTabListener(this));
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) 
	{
        mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) 
	{
	}
	
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft)
	{
		fragments[tab.getPosition()].closeContextualMode();
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter 
	{

		public SectionsPagerAdapter(FragmentManager fragmentManager) 
		{
			super(fragmentManager);
		}

		@Override
		public Fragment getItem(int position) 
		{
			// getItem is called to instantiate the fragment for the given page.
			Bundle args = new Bundle();
			args.putInt(GroceriesFragment.ARG_SECTION_NUMBER, position + 1);
			args.putBoolean(GroceriesFragment.ARG_SPECIAL_LIST, position == 0 ? true : false);
			fragments[position].setArguments(args);
			return fragments[position];
		}

		@Override
		public int getCount() 
		{
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) 
		{
			switch (position) 
			{
				case 0:
					return getString(R.string.urgent_list);
				case 1:
					return getString(R.string.normal_list);
			}
			return null;
		}
	}
	
	@Override
    protected int getSelfNavDrawerItem() 
    {
        return NAVDRAWER_ITEM_GROCERIES;
    }

	@Override
	public void OnListItemMoved(Fragment fragment, ModelBase product) 
	{
		for(int i=0; i<fragments.length; i++)
			if(fragments[i] != fragment)
				fragments[i].addItemAdapter(product);
	}
}
