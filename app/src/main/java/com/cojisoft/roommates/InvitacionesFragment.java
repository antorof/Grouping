package com.cojisoft.roommates;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cojisoft.Utils.UIUtils;

public class InvitacionesFragment extends Fragment
{
    ListView lista;

    private String[] titulos = new String[] {"Comida en Burger", "Fiesta Nochebuena", "Fiesta de pijamas"};
    private String[] subtitulos = new String[] {"Mamá - Mañana hay fiesta de", "Pedro - Hay que ver si nos vamos organizando", "María - Bueno, tenemos que organizar"};

    SparseBooleanArray checkedList = new SparseBooleanArray();

    ImageButton mAddButton;


    public static InvitacionesFragment newInstance()
    {
        return new InvitacionesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_invitaciones, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        lista = (ListView) view.findViewById(R.id.list_invitaciones);

        UIUtils.addHeaderFooterDivider(lista, getActivity());
        InvitacionesArrayAdapter adapterFijo = new InvitacionesArrayAdapter(getActivity(), titulos, subtitulos);

        lista.setAdapter(adapterFijo);

        // Traer al frente el botón para que no aparezca escondido trás la lista
        mAddButton = (ImageButton) view.findViewById(R.id.add_button);
        mAddButton.bringToFront();
        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view)
            {
                UIUtils.crearToast("Funcionalidad aún no creada", getActivity());
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Clase adaptador para los elementos de una lista de canciones
     * y los distintos modos de juego que tambi&eacute;n forman parte de la
     * lista.
     * @author DavidGSola
     *
     */
    public class InvitacionesArrayAdapter extends ArrayAdapter<String>
    {
        /**
         * Contexto donde se utilizar&aacute; el adaptador
         */
        private final Context context;

        /**
         * Valores de la lista
         */
        private final String[] titulos;

        /**
         * Valores de los subtitulos
         */
        private final String[] subtitulos;

        public InvitacionesArrayAdapter(Context context, String[] titulos, String[] subtitulos)
        {
            // Se utiliza el layout R.layout.item_lista para los elementos de la lista
            super(context, R.layout.list_item_invitacion, titulos);
            this.context = context;
            this.titulos = titulos;
            this.subtitulos = subtitulos;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            // Se "infla" el layout correspondiente
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_item_invitacion, parent, false);

            // Se obtienen referencias a los distintos campos del layout
            View botonAmplView = rowView.findViewById(R.id.botonImagen);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.icon_invitation);
            TextView titleView = (TextView) rowView.findViewById(R.id.title_invitation);
            TextView subtitleView = (TextView) rowView.findViewById(R.id.subtitle_invitation);

            // Se obtiene el valor de la lista para la posición actual
            String titulo = titulos[position];
            String subtitulo = subtitulos[position];

            titleView.setText(titulo);
            subtitleView.setText(subtitulo);

            if(position == 0)
            {
                imageView.setImageResource(R.drawable.persona1);
            }else if(position == 1)
            {
                imageView.setImageResource(R.drawable.persona2);
            }else if(position == 2)
            {
                imageView.setImageResource(R.drawable.persona3);
            }else if(position == 3)
            {
                imageView.setImageResource(R.drawable.persona4);
            }else if(position == 4)
            {
                imageView.setImageResource(R.drawable.persona5);
            }else if(position == 5)
            {
                imageView.setImageResource(R.drawable.persona6);
            }else if(position == 6)
            {
                imageView.setImageResource(R.drawable.persona7);
            }

            return rowView;
        }
    }
}

