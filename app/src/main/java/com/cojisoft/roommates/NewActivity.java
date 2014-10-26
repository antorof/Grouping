package com.cojisoft.roommates;

import android.app.Fragment;
import android.content.Intent;

public class NewActivity extends BaseSimpleActivity {

	@Override
	protected Fragment onCreatePane() 
	{
		return new NewFragment();
	}

    @Override
    public Intent getParentActivityIntent() 
    {
        // TODO: make this Activity navigate up to the right screen depending on how it was launched
        return new Intent(this, RoommatesActivity.class);
    }

    @Override
    protected int getLayoutResource()
    {
        return R.layout.activity_prueba_activity;
    }
	
}
