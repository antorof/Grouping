package com.cojisoft.roommates;

import android.os.Bundle;

public class MisQuedadasActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (null == savedInstanceState)
        {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, QuedadasFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_quedadas;
    }

    @Override
    protected int getSelfNavDrawerItem()
    {
        return NAVDRAWER_ITEM_MISQUEDADAS;
    }
}