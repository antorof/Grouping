package com.cojisoft.models;

public class ModelProduct extends ModelBase
{
	public String name;
	public String subname;
	public int purchaser_id;
	public boolean special;
	public int day;
	public int month;
	
	public ModelProduct(String name, String subname, int day, int month, int purchaser_id, boolean special, int id)
	{
		this.name = name;
		this.subname = subname;
		this.day = day;
		this.month = month;
		this.purchaser_id = purchaser_id;
		this.special = special;
		this.id = id;
	}
}
