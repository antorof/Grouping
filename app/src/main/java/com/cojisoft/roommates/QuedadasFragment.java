package com.cojisoft.roommates;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class QuedadasFragment extends Fragment
{
    ImageButton mAddButton;

    public static QuedadasFragment newInstance()
    {
        return new QuedadasFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_quedadas, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        // Traer al frente el botón para que no aparezca escondido trás la lista
        mAddButton = (ImageButton) view.findViewById(R.id.add_button);
        mAddButton.bringToFront();
        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

