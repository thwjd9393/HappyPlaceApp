package com.airapssinsj.happyplaceapp.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.airapssinsj.happyplaceapp.model.HappyPlaceModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1 // Database version
        private const val DATABASE_NAME = "HappyPlacesDatabase" // Database name
        private const val TABLE_HAPPY_PLACE = "HappyPlacesTable" // Table Name

        //All the Columns names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        //creating table with fields
        val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    // TODO (Step 4 : After Creating a database handler class. Let us create an function to insert a happy place detail to respective table.)
    // START
    /**
     * Function to insert a Happy Place details to SQLite Database.
     */
    fun addHappyPlace(happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase

        // ContentValues()
        // 키-값 쌍 형태로 데이터를 저장
        // SQLite 데이터베이스의 insert()와 update() 메서드에 전달하는 데 사용
        //
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title) // HappyPlaceModelClass TITLE
        contentValues.put(KEY_IMAGE, happyPlace.image) // HappyPlaceModelClass IMAGE
        contentValues.put(
            KEY_DESCRIPTION,
            happyPlace.description
        ) // HappyPlaceModelClass DESCRIPTION
        contentValues.put(KEY_DATE, happyPlace.date) // HappyPlaceModelClass DATE
        contentValues.put(KEY_LOCATION, happyPlace.location) // HappyPlaceModelClass LOCATION
        contentValues.put(KEY_LATITUDE, happyPlace.latitude) // HappyPlaceModelClass LATITUDE
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude) // HappyPlaceModelClass LONGITUDE

        // Inserting Row
        val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return result
    }

    fun updateHappyPlace(happyPlace: HappyPlaceModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title) // HappyPlaceModelClass TITLE
        contentValues.put(KEY_IMAGE, happyPlace.image) // HappyPlaceModelClass IMAGE
        contentValues.put(
            KEY_DESCRIPTION,
            happyPlace.description
        ) // HappyPlaceModelClass DESCRIPTION
        contentValues.put(KEY_DATE, happyPlace.date) // HappyPlaceModelClass DATE
        contentValues.put(KEY_LOCATION, happyPlace.location) // HappyPlaceModelClass LOCATION
        contentValues.put(KEY_LATITUDE, happyPlace.latitude) // HappyPlaceModelClass LATITUDE
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude) // HappyPlaceModelClass LONGITUDE

        // Inserting Row
        val result = db.update(
            TABLE_HAPPY_PLACE,
            contentValues,
            KEY_ID + "=" + happyPlace.id,
            null)
        //2nd argument is String containing nullColumnHack

        db.close() // Closing database connection
        return result
    }

    fun deleteHappyPlace(happyPlaceId: Int): Int {
        val db = this.writableDatabase

        val result = db.delete(
            TABLE_HAPPY_PLACE,
            "$KEY_ID=$happyPlaceId",
            null
            )

        return result
    }

    @SuppressLint("Range")
    fun getHappyPlaceList():ArrayList<HappyPlaceModel> {
        val happyPlaceList = ArrayList<HappyPlaceModel>()
        val selectQuery = "SELECT * FROM $TABLE_HAPPY_PLACE"
        val db = this.readableDatabase

        try {
            //커서 준비
            val cursor : Cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val place = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),
                    )
                    happyPlaceList.add(place) //리스트에 넣기
                }while (cursor.moveToNext())
            }
            cursor.close() //커서 닫기
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return happyPlaceList
    }

}
