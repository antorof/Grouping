package com.cojisoft.roommates;

import android.os.Bundle;

public class RoommatesActivity extends BaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		if (null == savedInstanceState) 
		{
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, RoommatesFragment.newInstance())
                    .commit();
        }
	}


    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_prueba_activity;
    }

	@Override
    protected int getSelfNavDrawerItem() 
    {
        return NAVDRAWER_ITEM_ROOMMATES;
    }
}