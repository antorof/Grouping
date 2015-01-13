package com.cojisoft.roommates;

import android.os.Bundle;

public class InvitacionesActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (null == savedInstanceState)
        {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, InvitacionesFragment.newInstance())
                    .commit();
        }
    }


    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_invitaciones;
    }

    @Override
    protected int getSelfNavDrawerItem()
    {
        return NAVDRAWER_ITEM_INVITACIONES;
    }
}