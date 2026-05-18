package com.example.lostfoundapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.lostfoundapp.activities.AddItemActivity
import com.example.lostfoundapp.activities.ItemListActivity
import com.example.lostfoundapp.activities.MapActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        val btnAdd = findViewById<Button>(R.id.btnAddItem)
        val btnView = findViewById<Button>(R.id.btnViewItems)
        val btnShowMap = findViewById<Button>(R.id.btnShowMap)

        btnShowMap.setOnClickListener {
            startActivity(Intent(this, MapActivity::class.java))
        }
        btnAdd.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }

        btnView.setOnClickListener {
            startActivity(Intent(this, ItemListActivity::class.java))
        }
    }


}