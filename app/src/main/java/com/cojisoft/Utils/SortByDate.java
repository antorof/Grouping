package com.cojisoft.Utils;

import java.util.Comparator;
import com.cojisoft.models.ModelProduct;

public class SortByDate implements Comparator<ModelProduct>
{
	@Override
	public int compare(ModelProduct lhs, ModelProduct rhs) 
	{
		if(lhs.month == rhs.month)
			return lhs.day - rhs.day;
		else
			return lhs.month - rhs.month;
	}

}
