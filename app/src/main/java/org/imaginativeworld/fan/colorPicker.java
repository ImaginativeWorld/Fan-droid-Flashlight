/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.imaginativeworld.fan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.lang.reflect.Field;

public class colorPicker extends AppCompatActivity implements View.OnClickListener {

    Button[] colorBtn = new Button[21];
    String color;

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

        Class cls = R.id.class;

        for (int i = 1; i <= 20; i++) {
            colorBtn[i] = (Button) findViewById(getResId("color" + String.valueOf(i), cls));
            colorBtn[i].setOnClickListener(colorPicker.this);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color1:
                color = "#f44336"; //Red
                break;
            case R.id.color2:
                color = "#e91e63"; //Pink
                break;
            case R.id.color3:
                color = "#9c27b0"; //Purple
                break;
            case R.id.color4:
                color = "#673ab7"; //Deep Purple
                break;
            case R.id.color5:
                color = "#3f51b5"; //Indigo
                break;
            case R.id.color6:
                color = "#2196f3"; //Blue
                break;
            case R.id.color7:
                color = "#03a9f4"; //Light Blue
                break;
            case R.id.color8:
                color = "#00bcd4"; //Cyan
                break;
            case R.id.color9:
                color = "#009688"; //Teal
                break;
            case R.id.color10:
                color = "#4caf50"; //Green
                break;
            case R.id.color11:
                color = "#8bc34a"; //Light Green
                break;
            case R.id.color12:
                color = "#cddc39"; //Lime
                break;
            case R.id.color13:
                color = "#ffeb3b"; //Yellow
                break;
            case R.id.color14:
                color = "#ffc107"; //Amber
                break;
            case R.id.color15:
                color = "#ff9800"; //Orange
                break;
            case R.id.color16:
                color = "#ff5722"; //Deep Orange
                break;
            case R.id.color17:
                color = "#795548"; //Brown
                break;
            case R.id.color18:
                color = "#9e9e9e"; //Grey
                break;
            case R.id.color19:
                color = "#607d8b"; //Blue Grey
                break;
            case R.id.color20:
                color = "#000000"; //Black
                break;
        }

        finishWithResult(color);
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
