/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.imaginativeworld.fan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements PreviewSurface.Callback, View.OnClickListener, View.OnTouchListener {

    final int TOTAL_FAN_MODEL = 7;

    ImageView imgFan;
    boolean animOn, hasFlash, isSoundOn;
    RotateAnimation anim;
    FloatingActionButton fab, fabLight, fab_fan_sound;
    int delay;
    String strColor;

    SeekBar seekBar;

    /**
     * Flash Light variables/objects
     */
    PreviewSurface mSurface;
    boolean on = false;
    boolean mCameraReady = false; // to make sure we don't turn on light when preview surface resizes

    MediaPlayer mp;

    //CheckedTextView chkBoxSound;
    TextView txtRPM;

    SharedPreferences sharedPref;

    int _pref_fan_speed;
    String _pref_fan_color;
    Boolean _pref_fan_sound;
    int _pref_fan_model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Lets Initialize all elements
         */
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        imgFan = (ImageView) findViewById(R.id.imgFan);
        imgFan.setOnTouchListener(MainActivity.this);

        fab_fan_sound = (FloatingActionButton) findViewById(R.id.fab_fan_sound);
        fab_fan_sound.setOnClickListener(MainActivity.this);

        txtRPM = (TextView) findViewById(R.id.txtRPM);
        txtRPM.setOnClickListener(MainActivity.this);

        seekBar = (SeekBar) findViewById(R.id.seekBar);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fabLight = (FloatingActionButton) findViewById(R.id.fabLight);

        /**
         * Needed For camera
         */
        mSurface = (PreviewSurface) findViewById(R.id.surfaceView);
        mSurface.setCallback(this);

        /**
         * Now Load settings form preference ans set
         */
        getSettings();

        /**
         * Set Audio File
         *
         * Will be needed:
         * http://stackoverflow.com/questions/16381721/issue-with-looping-an-audio-file
         */
        mp = MediaPlayer.create(MainActivity.this, R.raw.fan_sound);
        mp.setLooping(true);

        /**
         * Fan rotate animation
         */
        animOn = false;

        anim =
                new RotateAnimation(
                        0.0f,
                        360.0f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f
                );

        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(delay);


        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                animOn = true;
                fab.setImageResource(android.R.drawable.ic_media_pause);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animOn = false;
                fab.setImageResource(android.R.drawable.ic_media_play);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getProgress() == 0)
                    delay = 1000;
                else
                    delay = 1000 / seekBar.getProgress();

                txtRPM.setText(String.format(getString(R.string.txt_fan_rpm), 1000 / delay * 60));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                anim.setDuration(delay);

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!animOn) {

                    imgFan.startAnimation(anim);

                    try {
//                        if (mp.isPlaying()) {
//                            mp.stop();
//                            mp.release();
//                            mp = MediaPlayer.create(MainActivity.this, R.raw.sound02);
//                        }

                        if (isSoundOn)
                            mp.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else {

                    imgFan.clearAnimation();

                    try {
                        if (mp.isPlaying()) {
                            mp.pause();
                        }
//                        mp.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        /**
         * Check flash feature exists or not
         */
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        fabLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasFlash) {
                    Snackbar.make(view, "Flashlight not found! Please let us know if you think this is a Bug.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    if (!on) turnOn();
                    else turnOff();
                }

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * Don't need to turnoff
         */
//        turnOff();
//        mSurface.releaseCamera();
//        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Don't need to turnoff
         */
//        if (paused) {
//            mSurface.initCamera();
//        }
//        mCameraReady = false;
    }

    @Override
    protected void onStop() {
        super.onStop();

        setSettings();

    }

    /**
     * Save Preferences
     */
    void setSettings() {
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putInt(getString(R.string.pref_fan_speed), seekBar.getProgress());
        editor.putBoolean(getString(R.string.pref_fan_sound), isSoundOn);
        editor.putString(getString(R.string.pref_fan_color), strColor);
        editor.putInt(getString(R.string.pref_fan_model), _pref_fan_model);

        editor.apply();
    }

    /**
     * Load Preferences
     */
    void getSettings() {
        /**
         * Fan Speed
         */
        _pref_fan_speed = sharedPref.getInt(getString(R.string.pref_fan_speed), 5);

        if (_pref_fan_speed == 0)
            delay = 1000;
        else
            delay = 1000 / _pref_fan_speed;

        seekBar.setProgress(_pref_fan_speed);

        txtRPM.setText(String.format(getString(R.string.txt_fan_rpm), 1000 / delay * 60));

        /**
         * Fan Sound
         */
        _pref_fan_sound = sharedPref.getBoolean(getString(R.string.pref_fan_sound), true);
        isSoundOn = _pref_fan_sound;

        if (isSoundOn) {
            fab_fan_sound.setImageResource(R.drawable.ic_volume_up_black_24dp);
        } else {
            fab_fan_sound.setImageResource(R.drawable.ic_volume_off_black_24dp);
        }

        /**
         * Fan Color
         */
        _pref_fan_color = sharedPref.getString(getString(R.string.pref_fan_color), "#009688");
        imgFan.setColorFilter(Color.parseColor(_pref_fan_color));
        strColor = _pref_fan_color;

        /**
         * Fan Model
         */
        _pref_fan_model = sharedPref.getInt(getString(R.string.pref_fan_model), 1);
        setFanModel(_pref_fan_model);
    }

    void setFanModel(int modelNo) {
        switch (modelNo) {
            case 1:
                imgFan.setImageResource(R.drawable.fan1);
                break;
            case 2:
                imgFan.setImageResource(R.drawable.fan2);
                break;
            case 3:
                imgFan.setImageResource(R.drawable.fan3);
                break;
            case 4:
                imgFan.setImageResource(R.drawable.fan4);
                break;
            case 5:
                imgFan.setImageResource(R.drawable.fan5);
                break;
            case 6:
                imgFan.setImageResource(R.drawable.fan6);
                break;
            case 7:
                imgFan.setImageResource(R.drawable.fan7);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_change_fan_color) {
            Intent color_intent = new Intent(MainActivity.this, colorPicker.class);
            startActivityForResult(color_intent, 100);

            return true;
        } else if (id == R.id.action_about) {

            Intent about_intent = new Intent(MainActivity.this, aboutActivity.class);
            startActivity(about_intent);

            return true;
        } else if (id == R.id.action_contact_us) {
            sendEmail();
        } else if (id == R.id.action_rate) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getResources().getString(R.string.url_market)));
            startActivity(intent);
        } else if (id == R.id.action_change_fan_model) {

            if (_pref_fan_model == TOTAL_FAN_MODEL) {
                _pref_fan_model = 1;
            } else {
                _pref_fan_model++;
            }
            setFanModel(_pref_fan_model);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: // color activity
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    strColor = res.getString("color");

                    imgFan.setColorFilter(Color.parseColor(strColor));

                }
                break;

        }

    }

    private void turnOn() {
        if (!on) {
            on = true;
            mSurface.lightOn();
            fabLight.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            fabLight.setColorFilter(getResources().getColor(android.R.color.white));

        }
    }

    private void turnOff() {
        if (on) {
            on = false;
            mSurface.lightOff();
            fabLight.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
            fabLight.setColorFilter(Color.parseColor("#263238"));
        }
    }


    @Override
    public void cameraReady() {
        if (!mCameraReady) {
            mCameraReady = true;
            //turnOn();
        }
    }

    @Override
    public void cameraNotAvailable() {

    }

    protected void sendEmail() {
        //Log.i("Send email", "");
        String[] TO = {"info@imaginativeworld.org"};
        //String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Feedback message goes here...");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email"));
            //finish();
            //Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, "Sorry, there is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fab_fan_sound:

                if (isSoundOn) {
                    isSoundOn = false;
                    fab_fan_sound.setImageResource(R.drawable.ic_volume_off_black_24dp);

                    if (mp.isPlaying()) {
                        mp.pause();
                    }
                } else {
                    isSoundOn = true;
                    fab_fan_sound.setImageResource(R.drawable.ic_volume_up_black_24dp);

                    if (animOn)
                        mp.start();

                }

                break;
            case R.id.txtRPM:

                Toast.makeText(MainActivity.this, "Revolutions Per Minute", Toast.LENGTH_LONG).show();

                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        if (v.getId() == R.id.imgFan) {
            switch (action) {
                case (MotionEvent.ACTION_UP):
                    if (!animOn) {
                        RotateAnimation anim;
                        anim =
                                new RotateAnimation(
                                        0.0f,
                                        360.0f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f,
                                        Animation.RELATIVE_TO_SELF,
                                        0.5f
                                );

                        anim.setInterpolator(new AccelerateDecelerateInterpolator());
                        anim.setDuration(1000);

                        imgFan.startAnimation(anim);
                        return true;
                    }

                default:
                    return true;
            }
        }
        return true;
    }
}
