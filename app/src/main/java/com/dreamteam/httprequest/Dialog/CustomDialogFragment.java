package com.dreamteam.httprequest.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class CustomDialogFragment extends DialogFragment {

    PresenterInterface delegate;
    DialogConfig dialogConfig;
    MainActivity activity;
    EditText entry, titleEditText;

    public CustomDialogFragment(PresenterInterface delegate, MainActivity activity){
        this.activity = activity;
        this.delegate = delegate;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        dialogConfig = new DialogConfig(activity);
        //получаем данные из Bundle
        Bundle args = getArguments();

        final String type = args.getString("type");
        if (type != null && type.equals("discussion")) {
            //получаем список вариантов из Bundle
            final int[] photoActionArray = args.getIntArray("array");
            //получаем текстовый массив для списка Dialog
            ArrayList<String> answers = dialogConfig.getStringArray(photoActionArray);

            //преобразую коллекцию в простой массив
            final String[] mas = new String[answers.size()];
            answers.toArray(mas);

            //вывожу Dialog на экран и вешаю слушатель
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


            final LayoutInflater inflater = getActivity().getLayoutInflater();
            final View view = inflater.inflate(R.layout.created_discussion_dialog_signin, null);
            entry = (EditText) view.findViewById(R.id.message);
            titleEditText = (EditText)view.findViewById(R.id.title);

            builder.setView(view)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if ((titleEditText.getText().toString().equals(""))){
                                titleEditText.setError(getResources().getString(R.string.fill_in_the_field));
                                titleEditText.requestFocus();
                            } else if ((entry.getText().toString().equals(""))){
                                entry.setError(getResources().getString(R.string.fill_in_the_field));
                                entry.requestFocus();
                            }else {
                                String title = titleEditText.getText().toString();
                                String message = entry.getText().toString();
                                RadioButton radioButtonInfo = view.findViewById(R.id.infoRadioButton);
                                RadioButton radioButtonWarning = view.findViewById(R.id.warningRadioButton);
                                RadioButton radioButtonDanger = view.findViewById(R.id.dangerRadioButton);
                                String priority = new String();
                                if(radioButtonInfo.isChecked()){
                                    priority = "info";
                                } else if (radioButtonWarning.isChecked()){
                                    priority = "warning";
                                } else if(radioButtonDanger.isChecked()){
                                    priority = "danger";
                                }
                                delegate.answerDialog(-1, title, message, priority);
                            }
                        }
                    });

//                    .setItems(mas, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            delegate.answerDialog(dialogConfig.getAnswer(mas[which]));
//                        }
//                    });

            return builder.create();

        } else {
            //Вытаскиваем заголовок из Bundle
            String title = args.getString("title");

            //получаем список вариантов из Bundle
            int[] photoActionArray = args.getIntArray("array");

            //получаем текстовый массив для списка Dialog
            ArrayList<String> answers = dialogConfig.getStringArray(photoActionArray);

            //преобразую коллекцию в простой массив
            final String[] mas = new String[answers.size()];
            answers.toArray(mas);

            //вывожу Dialog на экран и вешаю слушатель
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title)//заголовок
                    .setItems(mas, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            delegate.answerDialog(dialogConfig.getAnswer(mas[which]), null, null, null);
                        }
                    });
            return builder.create();
        }
    }

}


