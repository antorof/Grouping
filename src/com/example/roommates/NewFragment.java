package com.example.roommates;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.roommates.Utils.UIUtils;

public class NewFragment extends Fragment 
{
	ListView lista;
	String[] values;
	
	NewAdapter mAdapter = null;
	
   
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	{
		return inflater.inflate(R.layout.fragment_new, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) 
	{
	    super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) 
	{
		lista = (ListView) view.findViewById(R.id.list_new);
		
		UIUtils.addHeaderFooterDivider(lista, getActivity());
		values= new String[] { getString(R.string.new_roommate), getString(R.string.new_grocery)};
		
		mAdapter = new NewAdapter(getActivity(), values);
		
		lista.setAdapter(mAdapter);
	}
	
	public class NewAdapter extends ArrayAdapter<String>
	{
		/**
		 * Contexto donde se utilizar&aacute; el adaptador
		 */
		private final Context context;
		
		/**
		 * Valores de la lista
		 */
		private final String[] valores;
		
		/**
		 * Constructor por defecto. Asocia el contexto y almacena
		 * los valores de la lista.
		 * 
		 * @param context contexto del adaptador
		 * @param valores <code>String</code> con los valores de la lista
		 */
		public NewAdapter(Context context, String[] valores) 
		{
			// Se utiliza el layout R.layout.item_lista para los elementos de la lista
			super(context, R.layout.list_item_new, valores);
			this.context = context;
			this.valores = valores;
		}

		/**
		 * Formatea los distintos campos de un elemento de la lista
		 * dependiendo del valor de la lista para esa posici&oacute;n
		 * 
		 * @param position actual posici&oacute;n de la lista a adaptar
		 * @param convertView 
		 * @param parent
		 * 
		 * @return vista del elemento de la vista adaptado correctamente
		 */
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) 
		{	
			// Se "infla" el layout correspondiente
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.list_item_new, parent, false);
			
			// Se obtienen referencias a los distintos campos del layout
			ImageView iconView = (ImageView) rowView.findViewById(R.id.new_icon);
			ImageView infoView = (ImageView) rowView.findViewById(R.id.new_info);
			View botonAmpliado = (View) rowView.findViewById(R.id.botonImagen);
			
			TextView textView = (TextView) rowView.findViewById(R.id.new_name);

			// Se obtiene el valor de la lista para la posición actual
			String s = valores[position];
		    
			if (s.equals( context.getString(R.string.new_roommate)) ) 	// Si es el modo "Nueva Canción" se añade la imagen R.drawable.plus
			{
				iconView.setImageResource(R.drawable.ic_drawer_settings);
				textView.setText(context.getString(R.string.new_roommate));
		    
			}else if(s.equals( context.getString(R.string.new_grocery)))	// Si es el modo "Libre" se añade la imagen R.drawable.song
			{
				iconView.setImageResource(R.drawable.ic_done_groceries);
				textView.setText(context.getString(R.string.new_grocery));
			}
			 
			iconView.setColorFilter(getResources().getColor(R.color.primary));
			infoView.setColorFilter(getResources().getColor(R.color.secondary_text));

			botonAmpliado.setOnClickListener(new View.OnClickListener() 
	    		{
                    public void onClick(View v) 
                    {
                    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
                     	builder.setMessage("ola k ase");
                		
                     	builder.setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
                     		public void onClick(DialogInterface dialog, int id) 
                     		{
                     		
                	        }
                     	});

                		builder.create().show();
                    }
                });
		    return rowView;
		}
	}
}
