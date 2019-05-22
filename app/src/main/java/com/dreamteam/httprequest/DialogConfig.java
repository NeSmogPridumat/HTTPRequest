package com.dreamteam.httprequest;

import android.util.Log;
import android.util.SparseArray;

import java.util.ArrayList;

public class DialogConfig {
    public final int CAMERA_REQUEST_CODE        = 0b1;
    public final int GALLERY_REQUEST_CODE       = 0b10;
    public final int DELETE_PHOTO_REQUEST_CODE  = 0b100;

    SparseArray array = new SparseArray();

    void addInArray(){

        String[] str = {(String) array.get(GALLERY_REQUEST_CODE), (String) array.get(CAMERA_REQUEST_CODE), (String) array.get(DELETE_PHOTO_REQUEST_CODE)};
        int i = array.keyAt(0);
        i = Integer.parseInt(null);
    }

    ArrayList<String> getStringArray(int[] intArray){
        array.put(CAMERA_REQUEST_CODE, "Сделать фото");
        array.put(GALLERY_REQUEST_CODE, "Выбрать из Галлереи");
//        array.put(DELETE_PHOTO_REQUEST_CODE, "Удалить");
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i<array.size(); i++ ){
            for (int j = 0; j<array.size(); j++){
                if (intArray[i] == array.keyAt(j)){
                    stringArrayList.add(array.valueAt(j).toString());
                }
            }
        }
        return stringArrayList;
    }

    public int getAnswer(String answer){

        int requestCode = 0;
            for (int i = 0; i < array.size(); i++){
                if (answer.equals(array.valueAt(i))){
                    requestCode= array.keyAt(i);
                }
            }
        return requestCode;
    }
}
