package com.dreamteam.httprequest.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.dreamteam.httprequest.Interfaces.PresenterInterface;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class CustomDialogFragment extends DialogFragment{

    PresenterInterface delegate;
    DialogConfig dialogConfig = new DialogConfig();

    public CustomDialogFragment(PresenterInterface delegate){
        this.delegate = delegate;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //получаем данные из Bundle
        Bundle args = getArguments();
        //Вытаскиваем заголовок из Bundle
        String title = args.getString("title");

        //получаем список вариантов из Bundle
        int[] photoActionArray = args.getIntArray("array");

        //получаем текстовый массив для списка Dialog
        ArrayList<String> answers = dialogConfig.getStringArray(photoActionArray);

        //преобразую коллекцию в простой массив
        final String [] mas  = new String[answers.size()];
        answers.toArray(mas);

        //вывожу Dialog на экран и вешаю слушатель
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)//заголовок
                .setItems(mas, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delegate.answerDialog(dialogConfig.getAnswer(mas[which]));
                    }
                });
        return builder.create();
    }

}


