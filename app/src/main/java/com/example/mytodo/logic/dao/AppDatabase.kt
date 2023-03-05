package com.example.mytodo.logic.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mytodo.logic.convert.LocalDateTimeConverter
import com.example.mytodo.logic.domain.entity.Project
import com.example.mytodo.logic.domain.entity.Task

@Database(version = 3, entities = [Project::class, Task::class])
@TypeConverters(LocalDateTimeConverter::class)
abstract class AppDatabase : RoomDatabase(){

    abstract fun getProjectDao() : ProjectDao

    abstract fun getTaskDao() : TasksDao

    /**
     * 静态方法
     */
    companion object {

        /**
         * database.execSQL("create table Task " +
        "(id Integer primary key autoincrement not null, name text not null, " +
        "description text, state integer not null, projectId integer not null)")
         */
        private val MIGRATION_1_2 = object : Migration(1,2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table task add createTime INTEGER not null default -28800")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2,3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table task add column remindTime INTEGER")
                database.execSQL("alter table task add column  endTime INTEGER")
                database.execSQL("alter table task add column isRepeat INTEGER NOT NULL DEFAULT(0)")
            }
        }

        private var instance : AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context) : AppDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(context.applicationContext,
                AppDatabase::class.java,"MyTodo")
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().apply {
                    instance = this
                }
        }
    }


}