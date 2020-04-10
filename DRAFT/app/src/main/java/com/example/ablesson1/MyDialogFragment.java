package com.example.ablesson1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.util.Objects;

public class MyDialogFragment extends DialogFragment implements Constants {

    //стандартный конструктор фрагмента
    static MyDialogFragment create (String message) {
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putString(MESSAGE, message);
        myDialogFragment.setArguments(args);
        return myDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String message = null;
        if (getArguments() != null) {
            message = getArguments().getString(MESSAGE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        // В билдере указываем заголовок окна
        builder.setTitle(R.string.error)
                // Указываем сообщение в окне
                .setMessage(message)
                // Можно указать и пиктограмму
                .setIcon(R.mipmap.ic_launcher_round)
                // Из этого окна нельзя выйти кнопкой Back
                .setCancelable(false)
                // Устанавливаем кнопку (название кнопки также можно задавать строкой)
                .setPositiveButton(getString(R.string.close),
                        // Ставим слушатель, нажатие будем обрабатывать
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //возвращаемся на фрагмент выбора города
                                FragmentManager fm = getFragmentManager();
                                if (fm != null) {
                                    fm.popBackStack();
                                    //вызываем повторно диалог ввода города
                                    MyBottomSheetDialogFragment dialogFragment = MyBottomSheetDialogFragment.newInstance();
                                    dialogFragment.show(getFragmentManager(), "dialog_fragment");
                                }
                                dismiss();
                            }
                        });
        return builder.create();
    }
}
