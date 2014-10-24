package com.cojisoft.Utils;

import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.cojisoft.roommates.R;

public class UIUtils 
{
	public static void setAccessibilityIgnore(View view) 
	{
		view.setClickable(false);
		view.setFocusable(false);
		view.setContentDescription("");
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
//			view.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
	}
	
	/**
	 * Formatea un String fecha de tipo (?XX YYY) siendo ?XX el día
	 * e YYY el mes a un fecha de tipo (?? YYY).
	 * @param day día
     * @param month mes
	 * @return
	 */
	public static String formatDate(int day, int month)
	{
		String dateFormated;
		if(day < 10)
			dateFormated = "0" + String.valueOf(day) + " ";
		else
			dateFormated = String.valueOf(day) + " ";
		
		switch(month)
		{
			case 1: 
				dateFormated = dateFormated.concat("JAN");
				break;
			case 2: 
				dateFormated = dateFormated.concat("FEB");
				break;
			case 3: 
				dateFormated = dateFormated.concat("MAR");
				break;
			case 4: 
				dateFormated = dateFormated.concat("APR");
				break;
			case 5: 
				dateFormated = dateFormated.concat("MAY");
				break;
			case 6: 
				dateFormated = dateFormated.concat("JUN");
				break;
			case 7: 
				dateFormated = dateFormated.concat("JUL");
				break;
			case 8: 
				dateFormated = dateFormated.concat("AUG");
				break;
			case 9: 
				dateFormated = dateFormated.concat("SEP");
				break;
			case 10: 
				dateFormated = dateFormated.concat("OCT");
				break;
			case 11: 
				dateFormated = dateFormated.concat("NOV");
				break;
			case 12: 
				dateFormated = dateFormated.concat("DEC");
				break;
			default:
				dateFormated = dateFormated.concat("---");
				break;
		}
		
		return dateFormated;
				
	}
	
	/**
	 * Devuelve el id del color correspondiente al {@value color} dentro
	 * de los recursos del sistema.
	 * @param context
	 * @param color
	 * @return id del color.
	 */
	public static int getColor(Context context, String color)
	{
		if(color.equals("orange") )
			return context.getResources().getColor(R.color.orange);
		else if(color.equals("turquesa") )
			return context.getResources().getColor(R.color.turquesa);		
		else if(color.equals("red"))
			return context.getResources().getColor(R.color.red);
		else
			return -1;
	}
	
	/**
	 * Devuelve el número de día actual dentro del mes
	 * @return
	 */
	public static int getDay()
	{
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Devuelve el número mes actual
	 * @return
	 */
	public static int getMonth()
	{
		Calendar cal = Calendar.getInstance();
		return (cal.get(Calendar.MONTH) + 1);
	}
	
	/**
	 * Añade un header y un footer a {@link lista} con el fin de añadir una especie
	 * de padding inicial a la lista. Este divider al ser un header y un footer se
	 * verá afectado por el scrolling sobre la lista. 
	 * @param lista
	 * @param context
	 */
	public static void addHeaderFooterDivider(ListView lista, Context context)
	{
		LayoutInflater inflater = LayoutInflater.from(context);
		View divider = inflater.inflate(R.layout.divider_view, null);
		lista.addHeaderView(divider);
		lista.addFooterView(divider);
	}
	
	/**
	 * Crea un mensaje de tipo <code>Toast</code> con un mensaje y 
	 * en un contexto espec&iacute;fico
	 * @param mensaje <code>String</code> con el mensaje a mostrar en la alerta
	 * @param ctx Contexto donde se va a crear la alerta
	 */
	public static void crearToast(String mensaje, Context ctx)
	{
		Toast.makeText
        (
        	ctx,
            mensaje,
            Toast.LENGTH_SHORT
        ).show();
	}
}
