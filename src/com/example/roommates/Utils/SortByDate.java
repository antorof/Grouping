package com.example.roommates.Utils;

import java.util.Comparator;

import models.ModelProduct;

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
