package com.example.lostfoundapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.lostfoundapp.model.Item

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "LostFoundDB", null, 2){

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT,
                description TEXT,
                category TEXT,
                image_uri TEXT,
                date_time TEXT,
                type TEXT,
                location TEXT,
                latitude REAL,
                longitude REAL
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS items")
        onCreate(db)
    }

    fun insertItem(item: Item) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("title", item.title)
            put("description", item.description)
            put("category", item.category)
            put("image_uri", item.imageUri)
            put("date_time", item.dateTime)
            put("type", item.type)
            put("location", item.location)
            put("latitude", item.latitude)
            put("longitude", item.longitude)
        }
        db.insert("items", null, values)
    }

    fun getAllItems(): List<Item> {
        val list = mutableListOf<Item>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM items", null)

        if (cursor.moveToFirst()) {
            do {
                val item = Item(
                    id = cursor.getInt(0),
                    title = cursor.getString(1),
                    description = cursor.getString(2),
                    category = cursor.getString(3),
                    imageUri = cursor.getString(4),
                    dateTime = cursor.getString(5),
                    type = cursor.getString(6),
                    location = cursor.getString(7),
                    latitude = cursor.getDouble(8),
                    longitude = cursor.getDouble(9)
                )
                list.add(item)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun deleteItem(id: Int) {
        val db = writableDatabase
        db.delete("items", "id=?", arrayOf(id.toString()))
    }
}