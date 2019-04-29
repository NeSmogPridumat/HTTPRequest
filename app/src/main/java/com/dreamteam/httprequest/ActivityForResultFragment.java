package com.dreamteam.httprequest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dreamteam.httprequest.Interfaces.PresenterInterface;

import java.io.FileNotFoundException;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

//TODO: это класс, который можно подразумевать как interactor работающий с data (камера, Галлерея). Как вариант, можно вынести его в отдельный пакет и использовать как универсальный обработчик Intent'ов (например для замены картинки в группе или event). Также возможно сюда (в пакет или класс) могут попасть уведомления (службы), чтобы не нагружать Activity, либо контроллеры, которые не предназначены для обработки данных

@SuppressLint("ValidFragment")
public class ActivityForResultFragment extends Fragment {//TODO: возможно стоит создать класс с кодами запроса и передать через него, чтобы сделать этот класс-фрагмент более универсальным

    PresenterInterface delegate;
    int i;
    DialogConfig dialogConfig;

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private static final int REQUEST_ID_VIDEO_CAPTURE = 101;
    private int CAMERA;
    private int GALLERY;
    private int DELETE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogConfig = new DialogConfig();
        CAMERA = dialogConfig.CAMERA_REQUEST_CODE;
        GALLERY = dialogConfig.GALLERY_REQUEST_CODE;
        DELETE = dialogConfig.DELETE_PHOTO_REQUEST_CODE;

        if (i == dialogConfig.CAMERA_REQUEST_CODE){
//            if (android.os.Build.VERSION.SDK_INT >= 21) {
//            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);

        }else if (i == dialogConfig.GALLERY_REQUEST_CODE){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, i);
        }else if (i == dialogConfig.DELETE_PHOTO_REQUEST_CODE){
            delegate.forResult(null);
        }
    }

    public ActivityForResultFragment(PresenterInterface delegate, int i){
        this.i = i;
        this.delegate = delegate;
    }

    //при добавлении форагмента сравниваем полученую позицию



//        switch (i){
//            //открываем камеру
//            case CAMERA:
//                if (android.os.Build.VERSION.SDK_INT >= 21) {
////
////                    int readPermission = ActivityCompat.checkSelfPermission(getContext(),
////                            Manifest.permission.READ_EXTERNAL_STORAGE);
////                    int writePermission = ActivityCompat.checkSelfPermission(getContext(),
////                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
////
////                    if (writePermission != PackageManager.PERMISSION_GRANTED ||
////                            readPermission != PackageManager.PERMISSION_GRANTED) {
////                        // If don't have permission so prompt the user.
////                        this.requestPermissions(
////                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
////                                        Manifest.permission.READ_EXTERNAL_STORAGE},
////                                REQUEST_ID_READ_WRITE_PERMISSION
////                        );
////                    }
//                }
////                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
////                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
////                    startActivityForResult(cameraIntent, i);
////                }
////                else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    // your code here - is api 21
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    this.startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE);
////                }
//                break;
//
//            //открываем Галлерею
//            case dialogGonfig.GALLERY_REQUEST_CODE:
//                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                photoPickerIntent.setType("image/*");
//                startActivityForResult(photoPickerIntent, i);
//                break;
//
//            //отправляем делегату null(при удалении фото)
//            case 2:
//                delegate.forResult(null);
//                break;
//        }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_READ_WRITE_PERMISSION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (read/write).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getContext(), "Permission granted!", Toast.LENGTH_LONG).show();

                }
                // Cancelled or denied.
                else {
                    Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    // Получние результатов интента
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;

        //если код запроса совпадает и запрос выполнен успешно (resultCode == RESULT_OK)
        if (requestCode == 100 && resultCode == RESULT_OK) {

            // перобразуем сделанное фото в Bitmap
            bitmap = (Bitmap) data.getExtras().get("data");

            //отправляем Bitmap в делегат
            delegate.forResult(bitmap);

        } else if (requestCode == dialogConfig.GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            //получаем Bitmap из интента (data.getData())
//            bitmap = BitmapFactory.decodeFile(data.getData().getEncodedPath().toString());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        this.getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //отпавляем Bitmap в делегат
            delegate.forResult(bitmap);
        }
//        else if (requestCode == dialogGonfig.CAMERA_REQUEST_CODE && resultCode == RESULT_OK){
//// перобразуем сделанное фото в Bitmap
//            Bitmap thumbnailBitmap = (Bitmap) data.getExtras().get("data");
//
//            //отправляем Bitmap в делегат
//            delegate.forResult(thumbnailBitmap);
//        }
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}


