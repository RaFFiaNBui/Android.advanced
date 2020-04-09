package com.example.ablesson1.history;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {LineOfHistory.class}, version = 1)
public abstract class HistoryDatabase extends RoomDatabase {
    public abstract HistoryDao getHistoryDao();
}
