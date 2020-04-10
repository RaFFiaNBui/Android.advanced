package com.example.ablesson1;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class ThermometerView extends View {

    // Цвет термометра
    private int thermometrColor = Color.GRAY;
    // Цвет уровня температуры
    private int levelColor = Color.RED;
    // Изображение батареи
    private RectF thermometrRectangle = new RectF();
    // Изображение уровня заряда
    private Rect levelRectangle = new Rect();
    // Изображение головы батареи
    private Rect headRectangle = new Rect();
    // "Краска" термометра
    private Paint thermometrPaint;
    // "Краска" температуры
    private Paint levelPaint;
    // Ширина элемента
    private int width = 0;
    // Высота элемента
    private int height = 0;
    // Уровень температуры
    private int level = 50;

    // Константы
    // Отступ элементов
    private final static int padding = 10;
    // Скругление углов термометра
    private final static int round = 5;
    // Ширина головы термометра
    private final static int headWidth = 10;

    public ThermometerView(Context context) {
        super(context);
        init();
    }

    // Вызывается при добавлении элемента в макет
    // AttributeSet attrs - набор параметров, указанных в макете для этого элемента
    public ThermometerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        init();
    }

    // Вызывается при добавлении элемента в макет с установленными стилями
    // AttributeSet attrs - набор параметров, указанных в макете для этого элемента
    // int defStyleAttr - базовый установленный стиль
    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        init();
    }

    // Вызывается при добавлении элемента в макет с установленными стилями
    // AttributeSet attrs - набор параметров, указанных в макете для этого элемента
    // int defStyleAttr - базовый установленный стиль
    // int defStyleRes - ресурс стиля, если он не был определен в предыдущем параметре
    public ThermometerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttr(context, attrs);
        init();
    }

    // Инициализация атрибутов пользовательского элемента из xml
    private void initAttr (Context context, AttributeSet attrs) {

        // При помощи этого метода получаем доступ к набору атрибутов.
        // На выходе массив со значениями
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ThermometerView, 0, 0);

        // Чтобы получить какое-либо значение из этого массива,
        // надо вызвать соответсвующий метод, и передав в этот метод имя ресурса
        // указанного в файле определения атрибутов (attrs.xml)
        thermometrColor = typedArray.getColor(R.styleable.ThermometerView_thermometer_color, Color.GRAY);

        // вторым параметром идет значение по умолчанию, если атрибут не указан в макете,
        // то будет подставлятся эначение по умолчанию.
        levelColor = typedArray.getColor(R.styleable.ThermometerView_level_color, Color.GREEN);

        // Обратите внимание, что первый параметр пишется особым способом
        // через подчерки. первое слово означает имя определения
        // <declare-styleable name="BatteryView">
        // следующее слово имя атрибута
        // <attr name="level" format="integer" />
        level = typedArray.getInteger(R.styleable.ThermometerView_level, 50);

        // В конце работы дадим сигнал,
        // что нам больше массив со значениями атрибутов не нужен
        // Система в дальнейшем будет переиспользовать этот объект,
        // и мы никогда не получим к нему доступ из этого элемента
        typedArray.recycle();
    }

    // Начальная инициализация полей класса
    private void init () {
        thermometrPaint = new Paint();
        thermometrPaint.setColor(thermometrColor);
        thermometrPaint.setStyle(Paint.Style.FILL);
        levelPaint = new Paint();
        levelPaint.setColor(levelColor);
        levelPaint.setStyle(Paint.Style.FILL);
    }

    // Когда система Андроид создает пользовательский экран, то еще неизвестны размеры элемента.
    // Это связанно с тем, что используются расчетные единица измерения,
    // то есть, чтобы элемент выглядил одинаково на разных усройствах,
    // необходимы расчеты размера элемента, в привязке к размеру экрана, его разрешения и плотности пикселей.
    // Этот метод вызывается при необходимости изменения размера элемента
    // Такая необходимость возникает каждый раз при отрисовке экрана.
    // Если нам надо нарисовать свой элемент, то переопределяем этот метод,
    // и задаем новые размеры нашим элементам внутри view
    @Override
    protected void onSizeChanged (int w,int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Получить реальные ширину и высоту
        width = w - getPaddingLeft() - getPaddingRight();
        height = h - getPaddingTop() - getPaddingBottom();

        //отрисовка термометра
        thermometrRectangle.set(padding, padding,
                width - padding - headWidth,
                height - padding);
        headRectangle.set(width - padding - headWidth,
                2 * padding,
                width - padding,
                height - 2 * padding);
        levelRectangle.set(2 * padding,
                2 * padding,
                (int) ((width - 2 * padding - headWidth)*((double)level/(double)100)),
                height - 2 * padding);
    }

    // Вызывается, когда надо нарисовать элемент
    @Override
    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRoundRect(thermometrRectangle, round, round, thermometrPaint);


    }
}
