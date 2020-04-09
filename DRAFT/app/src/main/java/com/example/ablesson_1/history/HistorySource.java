package com.example.ablesson_1.history;

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
        // Если объекты еще не загружены, загружаем их.
        // Это сделано для того, чтобы не делать запросы к БД каждый раз
        if (lines == null) {
            LoadLines();
        }
        return lines;
    }

    //Загружаем все строчки в буфер
    private void LoadLines() {
        lines = historyDao.getAllLinesOfHistory();
    }

    //Получаем количество записей
    public long getCountLines() {
        return historyDao.getCountLinesOfHistory();
    }

    //Добавляем строчку
    public void addLine(LineOfHistory lineOfHistory) {
        historyDao.insertLineOfHistory(lineOfHistory);
    }

    //Удаляем строчку из базы
    public void deleteLine(LineOfHistory lineOfHistory) {
        historyDao.deleteLineOfHistory(lineOfHistory);
    }
}
