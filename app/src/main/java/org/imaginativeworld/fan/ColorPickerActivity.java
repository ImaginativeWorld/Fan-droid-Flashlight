/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.imaginativeworld.fan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Field;

public class ColorPickerActivity extends AppCompatActivity implements View.OnClickListener {

    Button[] colorBtn = new Button[20];
    Button btnOK, colorPreview;
    String color;
    int __id = 0;
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
        btnOK.setOnClickListener(ColorPickerActivity.this);
        colorPreview = (Button) findViewById(R.id.colorPreview);


        // Get Display size in pixel
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int __width = size.x - (dpToPx(48) * 2 + 10 * 2);
        __margin = dpToPx(2);

        layoutParams = new LinearLayout.LayoutParams(__width / 5, __width / 5);
        layoutParams.setMargins(__margin, __margin, __margin, __margin);

        colorBtn[0] = (Button) findViewById(R.id.color1);
        colorBtn[1] = (Button) findViewById(R.id.color2);
        colorBtn[2] = (Button) findViewById(R.id.color3);
        colorBtn[3] = (Button) findViewById(R.id.color4);
        colorBtn[4] = (Button) findViewById(R.id.color5);
        colorBtn[5] = (Button) findViewById(R.id.color6);
        colorBtn[6] = (Button) findViewById(R.id.color7);
        colorBtn[7] = (Button) findViewById(R.id.color8);
        colorBtn[8] = (Button) findViewById(R.id.color9);
        colorBtn[9] = (Button) findViewById(R.id.color10);
        colorBtn[10] = (Button) findViewById(R.id.color11);
        colorBtn[11] = (Button) findViewById(R.id.color12);
        colorBtn[12] = (Button) findViewById(R.id.color13);
        colorBtn[13] = (Button) findViewById(R.id.color14);
        colorBtn[14] = (Button) findViewById(R.id.color15);
        colorBtn[15] = (Button) findViewById(R.id.color16);
        colorBtn[16] = (Button) findViewById(R.id.color17);
        colorBtn[17] = (Button) findViewById(R.id.color18);
        colorBtn[18] = (Button) findViewById(R.id.color19);
        colorBtn[19] = (Button) findViewById(R.id.color20);

        for (int i = 0; i <= 19; i++) {
            colorBtn[i].setOnClickListener(ColorPickerActivity.this);
            colorBtn[i].setLayoutParams(layoutParams);
        }

        // Preview Setting
        LinearLayout.LayoutParams lpColorPreview = new LinearLayout.LayoutParams(__width + 10 * 2, __width / 2);
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

    void setButton(int _id) {

        if (__id != 0) {

            colorBtn[__id].setPadding(__margin, __margin, __margin, __margin);
        }

        __id = _id;

        colorPreview.setBackgroundColor(Color.parseColor(color));
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.color1) {
            color = "#f44336"; //Red

            setButton(1);
        } else if (id == R.id.color2) {
            color = "#e91e63"; //Pink

            setButton(2);
        } else if (id == R.id.color3) {
            color = "#9c27b0"; //Purple
            setButton(3);
        } else if (id == R.id.color4) {
            color = "#673ab7"; //Deep Purple
            setButton(4);
        } else if (id == R.id.color5) {
            color = "#3f51b5"; //Indigo
            setButton(5);
        } else if (id == R.id.color6) {
            color = "#2196f3"; //Blue
            setButton(6);
        } else if (id == R.id.color7) {
            color = "#03a9f4"; //Light Blue
            setButton(7);
        } else if (id == R.id.color8) {
            color = "#00bcd4"; //Cyan
            setButton(8);
        } else if (id == R.id.color9) {
            color = "#009688"; //Teal
            setButton(9);
        } else if (id == R.id.color10) {
            color = "#4caf50"; //Green
            setButton(10);
        } else if (id == R.id.color11) {
            color = "#8bc34a"; //Light Green
            setButton(11);
        } else if (id == R.id.color12) {
            color = "#cddc39"; //Lime
            setButton(12);
        } else if (id == R.id.color13) {
            color = "#ffeb3b"; //Yellow
            setButton(13);
        } else if (id == R.id.color14) {
            color = "#ffc107"; //Amber
            setButton(14);
        } else if (id == R.id.color15) {
            color = "#ff9800"; //Orange
            setButton(15);
        } else if (id == R.id.color16) {
            color = "#ff5722"; //Deep Orange
            setButton(16);
        } else if (id == R.id.color17) {
            color = "#795548"; //Brown
            setButton(17);
        } else if (id == R.id.color18) {
            color = "#9e9e9e"; //Grey
            setButton(18);
        } else if (id == R.id.color19) {
            color = "#607d8b"; //Blue Grey
            setButton(19);
        } else if (id == R.id.color20) {
            color = "#000000"; //Black
            setButton(20);
        } else if (id == R.id.btnOk) {
            finishWithResult(color);
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
