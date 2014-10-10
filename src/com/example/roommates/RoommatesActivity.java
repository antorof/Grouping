package com.example.roommates;

import android.os.Bundle;

public class RoommatesActivity extends BaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roommates);
		
		if (null == savedInstanceState) 
		{
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, RoommatesFragment.newInstance())
                    .commit();
        }
	}

	
	@Override
    protected int getSelfNavDrawerItem() 
    {
        return NAVDRAWER_ITEM_ROOMMATES;
    }
}