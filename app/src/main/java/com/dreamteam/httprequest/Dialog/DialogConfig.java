package com.dreamteam.httprequest.Dialog;

import android.content.Context;
import android.util.SparseArray;

import com.dreamteam.httprequest.R;

import java.util.ArrayList;

public class DialogConfig {

    private Context context;
    public final int CAMERA_REQUEST_CODE        = 0b1;
    public final int GALLERY_REQUEST_CODE       = 0b10;
    public final int DELETE_PHOTO_REQUEST_CODE  = 0b100;
    public final int OK_CODE                    = 0b1000;

    public DialogConfig (Context context){
        this.context = context;
    }

    private SparseArray array = new SparseArray();

    void addInArray(){
        String[] str = {(String) array.get(GALLERY_REQUEST_CODE), (String) array.get(CAMERA_REQUEST_CODE), (String) array.get(DELETE_PHOTO_REQUEST_CODE)};
        int i = array.keyAt(0);
        i = Integer.parseInt(null);
    }

    ArrayList<String> getStringArray(int[] intArray){
        array.put(CAMERA_REQUEST_CODE, context.getResources().getString(R.string.to_make_a_photo));
        array.put(GALLERY_REQUEST_CODE, context.getResources().getString(R.string.choose_from_gallery));
        array.put(OK_CODE, context.getResources().getString(R.string.ok));
        array.put(DELETE_PHOTO_REQUEST_CODE, context.getResources().getString(R.string.delete));
        ArrayList<String> stringArrayList = new ArrayList<>();
        for (int i = 0; i < intArray.length; i++){//TODO какая-то хрень с ForEach, изучить и разобраться!!!
            for (int j = 0; j<array.size(); j++){
                if (intArray[i] == array.keyAt(j)){
                    stringArrayList.add(array.valueAt(j).toString());
                }
            }
        }
        return stringArrayList;
    }

    int getAnswer(String answer){
        int requestCode = 0;
            for (int i = 0; i < array.size(); i++){
                if (answer.equals(array.valueAt(i))){
                    requestCode= array.keyAt(i);
                }
            }
        return requestCode;
    }
}
