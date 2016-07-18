/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.imaginativeworld.fan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

public class colorPicker extends AppCompatActivity implements View.OnClickListener {

    Button[] colorBtn = new Button[21];
    Button btnOK, colorPreview;
    String color;
    int __id=0;
    int __margin;

    LinearLayout.LayoutParams layoutParams;
    LinearLayout layoutMain;

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_picker);

        //Make window fill full width
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutMain = (LinearLayout) findViewById(R.id.layoutMain);
        btnOK = (Button) findViewById(R.id.btnOk);
        btnOK.setOnClickListener(colorPicker.this);
        colorPreview = (Button) findViewById(R.id.colorPreview);


        // Get Display size in pixel
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int __width = size.x - (dpToPx(48)*2+10*2);
        __margin = dpToPx(2);

        layoutParams = new LinearLayout.LayoutParams(__width/5, __width/5);
        layoutParams.setMargins(__margin, __margin, __margin, __margin);

        Class cls = R.id.class;

        for (int i = 1; i <= 20; i++) {
            colorBtn[i] = (Button) findViewById(getResId("color" + String.valueOf(i), cls));
            colorBtn[i].setOnClickListener(colorPicker.this);
            colorBtn[i].setLayoutParams(layoutParams);
        }

        // Preview Setting
        LinearLayout.LayoutParams lpColorPreview = new LinearLayout.LayoutParams(__width+10*2, __width/2);
        lpColorPreview.setMargins(__margin, __margin, __margin, __margin);

        /**
         * Get Intent Extras
         */
        Intent intent = getIntent();
        color = intent.getStringExtra(getString(R.string.Color));

        colorPreview.setLayoutParams(lpColorPreview);
        colorPreview.setBackgroundColor(Color.parseColor(color));
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    void setButton(int _id)
    {

        if(__id!=0) {

            colorBtn[__id].setPadding(__margin,__margin,__margin,__margin);
        }

        __id=_id;

        colorPreview.setBackgroundColor(Color.parseColor(color));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color1:

                color = "#f44336"; //Red

                setButton(1);


                break;
            case R.id.color2:
                color = "#e91e63"; //Pink

                setButton(2);

                break;
            case R.id.color3:
                color = "#9c27b0"; //Purple
                setButton(3);

                break;
            case R.id.color4:
                color = "#673ab7"; //Deep Purple
                setButton(4);

                break;
            case R.id.color5:
                color = "#3f51b5"; //Indigo
                setButton(5);

                break;
            case R.id.color6:
                color = "#2196f3"; //Blue
                setButton(6);

                break;
            case R.id.color7:
                color = "#03a9f4"; //Light Blue
                setButton(7);

                break;
            case R.id.color8:
                color = "#00bcd4"; //Cyan
                setButton(8);

                break;
            case R.id.color9:
                color = "#009688"; //Teal
                setButton(9);

                break;
            case R.id.color10:
                color = "#4caf50"; //Green
                setButton(10);

                break;
            case R.id.color11:
                color = "#8bc34a"; //Light Green
                setButton(11);

                break;
            case R.id.color12:
                color = "#cddc39"; //Lime
                setButton(12);

                break;
            case R.id.color13:
                color = "#ffeb3b"; //Yellow
                setButton(13);

                break;
            case R.id.color14:
                color = "#ffc107"; //Amber
                setButton(14);

                break;
            case R.id.color15:
                color = "#ff9800"; //Orange
                setButton(15);

                break;
            case R.id.color16:
                color = "#ff5722"; //Deep Orange
                setButton(16);

                break;
            case R.id.color17:
                color = "#795548"; //Brown
                setButton(17);

                break;
            case R.id.color18:
                color = "#9e9e9e"; //Grey
                setButton(18);

                break;
            case R.id.color19:
                color = "#607d8b"; //Blue Grey
                setButton(19);

                break;
            case R.id.color20:
                color = "#000000"; //Black
                setButton(20);

                break;

            case R.id.btnOk:
                finishWithResult(color);
                break;
        }

    }

    private void finishWithResult(String color) {
        Bundle conData = new Bundle();

        conData.putString("color", color);

        Intent intent = new Intent();
        intent.putExtras(conData);
        setResult(RESULT_OK, intent);
        finish();
    }
}
