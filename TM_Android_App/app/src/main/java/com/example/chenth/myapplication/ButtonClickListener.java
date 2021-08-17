package com.example.chenth.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ButtonClickListener implements View.OnClickListener{

    public void onCreate(Bundle savedInstanceState){

    }

    @Override
    public void onClick(View v) {
        if (v.isEnabled() == true){
            v.setEnabled(false);
            MainActivity.numOfNodesClicked--;
            Log.i("ButtonClick", v.toString() + " disabled");
        }

        if (v.isEnabled() == false){
            v.setEnabled(true);
            MainActivity.numOfNodesClicked++;
            Log.i("ButtonClick", v.toString() + " enabled. N = " + MainActivity.numOfNodesClicked);
        }
    }


}
