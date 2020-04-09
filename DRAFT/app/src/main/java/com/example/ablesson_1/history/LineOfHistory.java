package com.example.ablesson_1.history;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// @Entity - это признак табличного объекта, то есть объект будет сохраняться
// в базе данных в виде строки
// indices указывает на индексы в таблице
@Entity(indices = {@Index(value = {"city", "temperature", "date"})})
public class LineOfHistory {

    // @PrimaryKey - указывает на ключевую запись,
    // autoGenerate = true - автоматическая генерация ключа
    @PrimaryKey(autoGenerate = true)
    public long id;

    // @ColumnInfo позволяет задавать параметры колонки в БД
    // name = "city" - имя колонки
    @ColumnInfo(name = "city")
    public String cityName; //название города

    @ColumnInfo(name = "temperature")
    public String cityTemp;    //температура в городе
    
    @ColumnInfo(name = "date")
    public long date;       //дата запроса
}
