package com.smartcitytraveller.mobile.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHandler extends SQLiteOpenHelper {
  private static final String TAG = DbHandler.class.getSimpleName();

  private static final String DATABASE_NAME = "smart_city_traveller.db";
  // always update database version
  private static final int DATABASE_VERSION = 16;

  public DbHandler(@Nullable Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {}

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    onCreate(db);
  }
}
