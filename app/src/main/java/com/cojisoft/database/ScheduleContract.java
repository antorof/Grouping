package com.cojisoft.database;

import android.provider.BaseColumns;

public class ScheduleContract 
{
	public static abstract class User implements BaseColumns
	{
		public static final String USER_NAME = "user_name";
		public static final String USER_GROCERIES = "user_groceries";
		public static final String USER_TASKS = "user_tasks";
		public static final String USER_BILLS = "user_bills";
		public static final String USER_LETTER = "user_letter";
		public static final String USER_COLOR = "user_color";
	}
	
	
	public static abstract class Product implements BaseColumns
	{
		public static final String PRODUCT_NAME = "product_name";
		public static final String PRODUCT_SUBNAME = "product_subname";
		public static final String PRODUCT_DAY = "product_day";
		public static final String PRODUCT_MONTH = "product_month";
		public static final String PRODUCT_PURCHASER_ID = "product_purchaser_id";
		public static final String PRODUCT_URGENT = "product_urgent";
	}
	
	
	private ScheduleContract()
	{
	}
	
}
