package com.dreamteam.httprequest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import android.widget.Toast;

import com.dreamteam.httprequest.Dialog.DialogConfig;
import com.dreamteam.httprequest.Interfaces.PresenterInterface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

//TODO: это класс, который можно подразумевать как interactor работающий с data (камера, Галлерея). Как вариант, можно вынести его в отдельный пакет и использовать как универсальный обработчик Intent'ов (например для замены картинки в группе или event). Также возможно сюда (в пакет или класс) могут попасть уведомления (службы), чтобы не нагружать Activity, либо контроллеры, которые не предназначены для обработки данных

@SuppressLint("ValidFragment")
public class ActivityForResultFragment extends Fragment {//TODO: возможно стоит создать класс с кодами запроса и передать через него, чтобы сделать этот класс-фрагмент более универсальным

    PresenterInterface delegate;
    int i;
    DialogConfig dialogConfig;
    private Uri photoURI;
    String mCurrentPhotoPath;
    File photoFile = null;

    private static final int REQUEST_ID_READ_WRITE_PERMISSION = 99;
    private static final int REQUEST_ID_IMAGE_CAPTURE = 100;
    private static final int REQUEST_ID_VIDEO_CAPTURE = 101;
    private int CAMERA;
    private int GALLERY;
    private int DELETE;

    public ActivityForResultFragment(PresenterInterface delegate, int i) {
        this.i = i;
        this.delegate = delegate;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogConfig = new DialogConfig(getContext());
        CAMERA = dialogConfig.CAMERA_REQUEST_CODE;
        GALLERY = dialogConfig.GALLERY_REQUEST_CODE;
        DELETE = dialogConfig.DELETE_PHOTO_REQUEST_CODE;

        //определяем, что было выбрано камера/Галлерея/удалить
        if (i == dialogConfig.CAMERA_REQUEST_CODE) {

            //если "Камера", то подготавливаем место для сохранения файла и запускаем камеру
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Create the File where the photo should go
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(getActivity(),
                            "com.example.android.provider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_ID_IMAGE_CAPTURE);
                }
            }
        } else if(i ==dialogConfig.GALLERY_REQUEST_CODE){
            //Если "Галлерея, то запускаем Галлерею
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, i);
        } else if(i ==dialogConfig.DELETE_PHOTO_REQUEST_CODE){
            delegate.forResult(null);//TODO: стоит заглушка, если нет фото
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        // Note: If request is cancelled, the result arrays are empty.
        // Permissions granted (read/write).
        if (requestCode == REQUEST_ID_READ_WRITE_PERMISSION) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getContext(), "Permission granted!", Toast.LENGTH_LONG).show();
            }
            // Cancelled or denied.
            else {
                Toast.makeText(getContext(), "Permission denied!", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Получние результатов интента
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        //если код запроса совпадает и запрос выполнен успешно (resultCode == RESULT_OK)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            //считываем с эксиф-данных ориентацию и при необходимости поворчиваем на нужный градус (Samsung)
            int rotate = 0;
            ExifInterface exif = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                        Uri.fromFile(photoFile));
                exif = new ExifInterface(photoFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);

            int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
            delegate.forResult(scaled);
        } else if (requestCode == dialogConfig.GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            //получаем Bitmap из интента (data.getData())
            try {
                bitmap = MediaStore.Images.Media.getBitmap(
                        this.getActivity().getContentResolver(), data.getData());

                //извлекаем путь к картинке
                Uri selectedImageUri = data.getData();
                int orientation = getOrientation(getContext(), selectedImageUri);

                //Создаем Matrix, вносим данные о повороте на нужное кол-во градусов и применяем к bitmap
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                //отпавляем Bitmap в делегат
                delegate.forResult(scaled);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    //Получение ориетации картинки
    private static int getOrientation(Context context, Uri photoUri) {
        Cursor cursor = context.getContentResolver().query(photoUri, new String[]{MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
        int i = cursor.getColumnCount();
        if (cursor.getColumnCount() != 1) {
            cursor.close();
            return -1;
        }
        cursor.moveToFirst();
        int orientation = 0;
        if(cursor.moveToFirst()) {
            orientation = cursor.getInt(0);
        }
        cursor.close();
        return orientation;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_ID_IMAGE_CAPTURE);
        }
    }

    //создание файла фото
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}


