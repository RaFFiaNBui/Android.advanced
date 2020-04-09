package com.example.ablesson_1.history;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

// Описание объекта, обрабатывающего данные
// @Dao - доступ к данным
// В этом классе описывается, как будет происходить обработка данных
@Dao
public interface HistoryDao {

    // Метод для добавления строчки в базу данных
    // @Insert - признак добавления
    @Insert
    void insertLineOfHistory(LineOfHistory lineOfHistory);

    // Удаляем данные строки
    @Delete
    void deleteLineOfHistory(LineOfHistory lineOfHistory);

    // Забираем данные по всем строкам
    @Query("SELECT * FROM lineofhistory")
    List<LineOfHistory> getAllLinesOfHistory();

    //Получаем количество записей в таблице
    @Query("SELECT COUNT() FROM LineOfHistory")
    long getCountLinesOfHistory();
}
