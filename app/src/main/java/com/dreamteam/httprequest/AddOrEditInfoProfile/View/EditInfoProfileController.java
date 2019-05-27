package com.dreamteam.httprequest.AddOrEditInfoProfile.View;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dreamteam.httprequest.AddOrEditInfoProfile.Data.InfoProfileData;
import com.dreamteam.httprequest.AddOrEditInfoProfile.Presenter.EditInfoProfilePresenter;
import com.dreamteam.httprequest.AddOrEditInfoProfile.Protocols.EditInfoProfileViewInterface;
import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Data.RequestInfo;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;
import com.dreamteam.httprequest.MainActivity;
import com.dreamteam.httprequest.R;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class EditInfoProfileController extends Fragment implements EditInfoProfileViewInterface {

    private EditText titleEditTextView, descriptionEditTextView;
    private TextView titleTextView, descriptionTextView;
    private Button saveButton;
    private ImageView editImageView;
    private RelativeLayout progressBar;

    private InfoProfileData infoProfileData;
    private String type;
    private boolean checkImage = false;

    private EditInfoProfilePresenter editProfilePresenter;
    private PresenterInterface delegate;
    private RequestInfo requestInfo;
    private ConstantConfig constantConfig = new ConstantConfig();

    public EditInfoProfileController(InfoProfileData infoProfileData, RequestInfo requestInfo, PresenterInterface delegate, String type) {
        // Required empty public constructor
        this.delegate = delegate;
        this.infoProfileData = infoProfileData;
        this.type = type;
        this.requestInfo = requestInfo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_info_profile_controller, container, false);
        titleTextView = view.findViewById(R.id.title_edit_info_profile_text_view);
        titleEditTextView = view.findViewById(R.id.title_edit_info_profile_edit_text);
        descriptionTextView = view.findViewById(R.id.description_edit_info_profile_text_view);
        descriptionEditTextView = view.findViewById(R.id.description_edit_info_profile_edit_text);
        editImageView = view.findViewById(R.id.edit_info_profile_image);
        editImageView.setImageBitmap(null);
        saveButton = view.findViewById(R.id.save_profile_info_button);

        //если один тип - задаем один вариант отображения вью, если другой тип  - другой
        if (type.equals(constantConfig.USER_TYPE)){
            titleTextView.setText(R.string.name);
            titleEditTextView.setHint(R.string.enter_new_name);
            descriptionTextView.setText(R.string.surname);
            descriptionEditTextView.setHint(R.string.enter_new_surname);
        } else if (type.equals(constantConfig.ADD_GROUP_TYPE) || type.equals(constantConfig.EDIT_GROUP_TYPE)){
            titleTextView.setText(R.string.title);
            titleEditTextView.setHint(R.string.enter_new_title);
            descriptionTextView.setText(R.string.description);
            descriptionEditTextView.setHint(R.string.enter_new_description);
            descriptionEditTextView.setSingleLine(false);
            descriptionEditTextView.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            descriptionEditTextView.setLines(3);
            descriptionEditTextView.setVerticalScrollBarEnabled(true);
        }

        //если идет изменение чего-то и мы данные изменяем, выставляем их
        if(infoProfileData != null) {
            titleEditTextView.setText(infoProfileData.title);
            descriptionEditTextView.setText(infoProfileData.description);
            editImageView.setImageBitmap(infoProfileData.imageData);
        }else {
            infoProfileData = new InfoProfileData();
        }
        progressBar = view.findViewById(R.id.progressBarOverlay);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
        editProfilePresenter = new EditInfoProfilePresenter(activity, this, delegate);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        //слушатель на ImageView и вызов диалогового окна
        editImageView.setOnClickListener(new View.OnClickListener() {//слушатель нажатия на ImageView
            @Override
            public void onClick(View v) {
                checkImage = true;
                editProfilePresenter.showDialog();
            }
        });

//TODO: сделать так, что если какие-то данные не изменились, то не отправлять эти данные на сервер
        //слушатель на кнопку
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//-----------------------------------------------------слушатель на кнопку Save
                if ((titleEditTextView.getText().toString().equals("")))//-----------------------------------проверка на заполнение поля
                {
                    titleEditTextView.setError(getResources().getString(R.string.fill_in_the_field));
                    titleEditTextView.requestFocus();
                } else if (descriptionEditTextView.getText().toString().equals("")){
                    descriptionEditTextView.setError(getResources().getString(R.string.fill_in_the_field));
                    descriptionEditTextView.requestFocus();
                } else {//---------------------------------------------------------------------------------код отправки изменеий на сервер
                    infoProfileData.title = titleEditTextView.getText().toString();
                    infoProfileData.description = descriptionEditTextView.getText().toString();
                    if (checkImage) {
                        if (((BitmapDrawable)editImageView.getDrawable()).getBitmap() == null) {
                            //TODO: поставить какую-нибудь зашлушку, а-ля картинка по дэфолту
                        }else{
                            infoProfileData.imageData = ((BitmapDrawable)editImageView.getDrawable()).getBitmap();
                        }
                    }
                    //отправка данных на изменение
                    editProfilePresenter.editInfo(infoProfileData, requestInfo, type);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });
        super.onStart();
    }

    //получение картинки от forActivityResult
    @Override
    public void forResult(Bitmap bitmap) {
        editImageView.setImageBitmap(bitmap);
    }
}
