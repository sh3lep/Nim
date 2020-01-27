package ru.spbstu.shelepov.nim

import android.app.Application
import androidx.room.Room
import ru.spbstu.shelepov.nim.database.ScoreDatabase

//Создание базы данных в приложении, сделано здесь чтобы она была одна на все приложение.
class App :Application() {
    lateinit var database: ScoreDatabase

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, ScoreDatabase::class.java, "database")
            .build()
    }
}