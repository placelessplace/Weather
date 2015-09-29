package com.test.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class WeatherOpenHelper extends SQLiteOpenHelper{
	//Province表建表语句
	public static final String CREATE_PROVINCE = "create table Province ("
			+"id integer primary key autoincrement, "
			+"province_name text, "
			+"province_code text)";
	
	//City表建表语句
	public static final String CREATE_CITY = "create table City ("
			+"id integer primart key autoincrement, "
			+"city_name text,"
			+"city_cpde text,"
			+"province_id integer)";
	
	//County表建表语句
	public static final String CREATE_COUNTY = "create table County ("
			+"id integer primary key autoincrement ,"
			+"county_name text,"
			+"county_code text,"
			+"city_id integer)";
	
	public WeatherOpenHelper(Context context,String name,CursorFactory factory ,int version){
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		
	}
}
