package com.example.ablesson1.history;

import java.util.List;

// Вспомогательный класс, развязывающий зависимость между Room и RecyclerView
public class HistorySource {

    private final HistoryDao historyDao;

    // Буфер с данными: сюда будем подкачивать данные из БД
    private List<LineOfHistory> lines;

    public HistorySource(HistoryDao historyDao) {
        this.historyDao = historyDao;
    }

    // Получить все строчки
    public List<LineOfHistory> getLines() {
        return lines;
    }

    //Загружаем все строчки в буфер
    public void loadLines() {
        lines = historyDao.getAllLinesOfHistory();
    }

    //Получаем количество записей
    public int getCountLines() {
        //return historyDao.getCountLinesOfHistory(); // подтягивал все записи из таблицы(для фильтра не подходит)
        return lines.size();
    }

    //Добавляем строчку
    public void addLine(LineOfHistory lineOfHistory) {
        historyDao.insertLineOfHistory(lineOfHistory);
    }

    //Удаляем строчку из базы
    public void deleteLine(LineOfHistory lineOfHistory) {
        historyDao.deleteLineOfHistory(lineOfHistory); //из БД
        lines.remove(lineOfHistory);    //из текущего List
    }

    //Очистить БД
    public void deleteAll() {
        historyDao.deleteAll(); //из БД
        lines.clear();  //из List
    }

    //Получим данные только об одном городе
    public void getHistoryByName(String name) {
        lines = historyDao.getHistoryByName(name);
    }

    // Получим данные только по дате
    public void getHistoryByDate(long date) {
        lines = historyDao.getHistoryByDate(date);
    }

    // Получим данные только по температуре
    public void getHistoryByTemperature(String temp) {
        lines = historyDao.getHistoryByTemperature(temp);
    }
}
