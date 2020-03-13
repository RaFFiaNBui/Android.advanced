package com.example.ablesson_1;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class MainFragment extends Fragment implements Constants {

    private boolean isLandscape;    // true - планшетная ориентация
    private Parcel currentParcel;   // Текущая посылка (название города)

    private Pattern checkCityName = Pattern.compile("^[A-Z][a-z]{2,}$");

    //ClickListener на TextView поля списка RecyclerView
    private CitiesAdapter.RecyclerItemClickListener clickListener = new CitiesAdapter.RecyclerItemClickListener() {
        @Override
        public void onItemClick(String data) {
            //при каждом клике создается новая посылка с названием города
            currentParcel = new Parcel(data);
            //и запускается метод отображения с названием города
            showSecondFragment(currentParcel);
        }
    };

    //ClickListener на кнопку ввода названия города
    private View.OnClickListener enterCityListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //при клике создается новая посылка с названием города
/*            TextInputEditText enterCity = v.findViewById(R.id.field_enter_city);
            String str = enterCity.getText().toString();
            if (enterCity.getText().toString() != null) {
                currentParcel = new Parcel(str);
                //и запускается метод отображения с названием города
                showSecondFragment(currentParcel);
            }*/
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText cityName = view.findViewById(R.id.field_enter_city);
        final Button btn = view.findViewById(R.id.btn_enter_city);
        //проверка при потере фокуса
        cityName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btn.setVisibility(View.VISIBLE);
                    return;
                }
                TextView tv = (TextView) v;
                validate(tv, checkCityName, "Город не найден");
            }
        });
        //подтягиваем наш список городов
        String[] data = getResources().getStringArray(R.array.items);
        //инициализируем RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        //подсказываем, что наш список конечный
        recyclerView.setHasFixedSize(true);
        //инициализируем LayoutManager (повторно, он был проинициализирован в fragment_main)
        if (getContext() != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        //инициализируем адаптер
        CitiesAdapter citiesAdapter = new CitiesAdapter(data);
        //вешаем Listener на адаптер
        citiesAdapter.setClickListener(clickListener);
        //устанавливаем нашему списку адаптер
        recyclerView.setAdapter(citiesAdapter);

        Button buttonEnterCity = view.findViewById(R.id.btn_enter_city);
        buttonEnterCity.setOnClickListener(enterCityListener);

        //инициализация фонового изображения
        ConstraintLayout constraintLayout = view.findViewById(R.id.mainFragmentLayout);
        TextView textView = view.findViewById(R.id.textView);
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        if (sharedPref.getBoolean(IS_DARK_THEME, false)) {
            textView.setBackgroundResource(R.color.colorBlue);
        } else {
            constraintLayout.setBackgroundResource(R.drawable.first_page);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Определение, можно ли будет расположить фрагмет с погодой рядом с фрагментом городов
        isLandscape = getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

        // Если это не первое создание, то восстановим текущую позицию
        //+ Здесь также востанавливаем Parcel
        if (savedInstanceState != null) {
            // Восстановление текущей позиции.
            currentParcel = (Parcel) savedInstanceState.getSerializable(CITY);
        } else {
            //+ Если воcстановить не удалось, то сделаем пустой объект
            currentParcel = new Parcel(getResources().getStringArray(R.array.items)[0]);
        }

        // Если можно вставить фрагмент с погодой, то сделаем это
        if (isLandscape) {
            showSecondFragment(currentParcel);
        }
    }

    // Сохраним текущую позицию (вызывается перед выходом из фрагмента)
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //+ Также меняем текущую позицию на Parcel
        outState.putSerializable(CITY, currentParcel);
        super.onSaveInstanceState(outState);
    }

    // Показать 2 фрагмент. Ecли возможно, то показать рядом с первым,
    // если нет, то открыть вторую activity
    private void showSecondFragment(Parcel parcel) {
        if (isLandscape) {
            // Проверим, что фрагмент с погодой существует в activity
            CityFragment detail = (CityFragment)
                    getFragmentManager().findFragmentById(R.id.frame_for_fragment_2);
            // Если есть необходимость, то выведем 2 фрагмент
            //+ Здесь также применяем Parcel
            if (detail == null || !parcel.getCityName().equals(detail.getParcel().getCityName())) {
                // Создаем новый фрагмент с текущей позицией для вывода герба
                detail = CityFragment.create(parcel);

                // Выполняем транзакцию по замене фрагмента
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.frame_for_fragment_2, detail);  // замена фрагмента
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        } else {
            // Если нельзя вывести герб рядом, откроем вторую activity
            FragmentActivity fragmentActivity = getActivity();
            if (fragmentActivity != null) {
                Intent intent = new Intent(fragmentActivity, SecondActivity.class);
                //+ и передадим туда Parcel
                intent.putExtra(PARCEL, parcel);
                startActivity(intent);
            }
        }
    }

    private void validate(TextView tv, Pattern check, String msg) {
        String value = tv.getText().toString();
        if (check.matcher(value).matches()) {
            hideError(tv);
        } else {
            showError(tv, msg);
        }
    }

    private void hideError(TextView tv) {
        tv.setError(null);
    }

    private void showError(TextView tv, String msg) {
        tv.setError(msg);
    }
}