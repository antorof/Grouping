package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import database.ScheduleContract.Product;
import database.ScheduleContract.User;

public class ScheduleDatabase extends SQLiteOpenHelper
{
	private static String DATABASE_NAME = "database.db";
	
	private static int DATABASE_VERSION = 1;
	
    private final Context mContext;
    
    public interface Tables
    {
    	String USERS = "users";
    	String PRODUCTS = "products";
    }
	
	public ScheduleDatabase(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}	 
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		db.execSQL("CREATE TABLE " + Tables.USERS + " ("
					+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	                + User.USER_NAME + " TEXT UNIQUE NOT NULL,"
	                + User.USER_GROCERIES + " INTEGER,"
	                + User.USER_TASKS + " INTEGER,"
	    	        + User.USER_BILLS + " INTEGER,"
	    	    	+ User.USER_LETTER + " TEXT NOT NULL,"
	                + User.USER_COLOR + " TEXT NOT NULL)");
		
		db.execSQL("CREATE TABLE " + Tables.PRODUCTS + " ("
					+ BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
	                + Product.PRODUCT_NAME+ " TEXT NOT NULL,"
	                + Product.PRODUCT_SUBNAME + " TEXT NOT NULL,"
	                + Product.PRODUCT_DAY + " INTEGER,"
	    	        + Product.PRODUCT_MONTH + " INTEGER,"
	                + Product.PRODUCT_PURCHASER_ID + " INTEGER,"
	                + Product.PRODUCT_URGENT + " INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
        db.execSQL("DROP TABLE IF EXISTS " + Tables.USERS);
        db.execSQL("DROP TABLE IF EXISTS " + Tables.PRODUCTS);
		onCreate(db);
	}
}
