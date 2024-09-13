package com.example.myapplication

import android.app.Application
import androidx.room.Room
import com.example.myapplication.db.TodoDatabase
import com.google.firebase.FirebaseApp

class MainApplication : Application() {

    companion object {
        lateinit var todoDatabase: TodoDatabase
    }

    override fun onCreate() {
        super.onCreate()

        // Inicijalizacija Firebase-a
        FirebaseApp.initializeApp(this)

        // Inicijalizacija Room baze podataka
        todoDatabase = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            TodoDatabase.NAME
        ).build()
    }
}
