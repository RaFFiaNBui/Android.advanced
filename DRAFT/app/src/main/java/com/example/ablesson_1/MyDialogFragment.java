package com.example.ablesson_1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // В билдере указываем заголовок окна
        builder.setTitle(R.string.error)
                // Указываем сообщение в окне (также есть вариант со
                // строковым параметром)
                .setMessage("File not found")
                // Можно указать и пиктограмму
                .setIcon(R.mipmap.ic_launcher_round)
                // Из этого окна нельзя выйти кнопкой Back
                .setCancelable(false)
                // Устанавливаем кнопку (название кнопки также можно
                // задавать строкой)
                .setPositiveButton(getString(R.string.close),
                        // Ставим слушатель, нажатие будем обрабатывать
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dismiss();
                            }
                        });
        return builder.create();
    }
}
