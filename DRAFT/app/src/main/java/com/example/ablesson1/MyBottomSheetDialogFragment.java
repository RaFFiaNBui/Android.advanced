package com.example.ablesson1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public static MyBottomSheetDialogFragment newInstance() {
        return new MyBottomSheetDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //раздуваем наш макет
        View view = inflater.inflate(R.layout.bottom_sheet_dialog_fragment, container, false);

        //инициализируем поле ввода города
        final EditText fieldCity = view.findViewById(R.id.dialog_enter_city);

        //позволяем закрывать окно кнопкой Back
        setCancelable(true);

        //инициализируем кнопку поиска
        view.findViewById(R.id.dialog_enter_city_btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //кладем в посылку значение введеного города
                Parcel parcel = new Parcel(fieldCity.getText().toString());
                //создаем фрагмент
                CityFragment detail = CityFragment.create(parcel);

                // Выполняем транзакцию по замене фрагмента
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.first_fragment, detail);  // замена фрагмента
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.addToBackStack("Start");
                ft.commit();
                //закрываем диалог
                dismiss();
            }
        });
        return view;
    }
}
