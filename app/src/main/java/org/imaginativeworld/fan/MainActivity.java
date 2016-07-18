/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

/**
 * Here Flashlight codes are brought from:
 * https://github.com/jbutewicz/Flashlight-by-Joe
 */

package org.imaginativeworld.fan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback, View.OnTouchListener {
    //PreviewSurface.Callback,
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
    boolean on = false;
    //boolean mCameraReady = false; // to make sure we don't turn on light when preview surface resizes
    String mCameraId;
    //PreviewSurface mSurface;
    boolean torchRequest = false;

    int count = 0;
    static Camera mCameraActivity;
    Parameters params;
    SurfaceView preview;
    SurfaceHolder mHolder;

    MediaPlayer mp;

    //CheckedTextView chkBoxSound;
    TextView txtRPM;

    SharedPreferences sharedPref;

    int _pref_fan_speed;
    String _pref_fan_color;
    Boolean _pref_fan_sound;
    int _pref_fan_model;

    /**
     * The View
     */
    ViewGroup rootView;


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

        rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        /**
         * Needed For camera
         */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            RelativeLayout context_main_layout = (RelativeLayout) findViewById(R.id.context_main_layout);

            RelativeLayout.LayoutParams lpView = new RelativeLayout.LayoutParams(dpToPx(1), dpToPx(1));
            preview = new SurfaceView(MainActivity.this);
            preview.setLayoutParams(lpView);
            context_main_layout.addView(preview);

            try {
                setmCameraActivity(Camera.open());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Last
            //mSurface.setCallback(MainActivity.this);
        }

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

        /**
         * Fan Switch
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!animOn) {

                    imgFan.startAnimation(anim);

                    try {

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

        /**
         * Flashlight switch
         */
        fabLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    turnOnOffTourchForMplus();

                } else {

                    if (torchRequest) {

                        torchRequest = !torchRequest;
                        processOffClick();
                        setTorchIconStyle(false);

                    } else {

                        torchRequest = !torchRequest;
                        processOnClick();
                        setTorchIconStyle(true);

                    }

                }

            }
        });

    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    void turnOnOffTourchForMplus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            torchRequest = !torchRequest;

            try {
                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//                for (String id : cameraManager.getCameraIdList()) {
                mCameraId = cameraManager.getCameraIdList()[0];

                // Turn on the flash if camera has one
//                if (cameraManager.getCameraCharacteristics(mCameraId).get(CameraCharacteristics.FLASH_INFO_AVAILABLE)) {
                cameraManager.setTorchMode(mCameraId, torchRequest);
//                }

                setTorchIconStyle(torchRequest);
//                }
            } catch (Exception e) {
                Log.e(getString(R.string.TAG), "Failed to interact with camera.", e);
                Toast.makeText(MainActivity.this, "Torch Failed: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    void setTorchIconStyle(boolean isOn) {
        if (isOn) {
            fabLight.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            fabLight.setColorFilter(getResources().getColor(android.R.color.white));
        } else {
            fabLight.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.white)));
            fabLight.setColorFilter(Color.parseColor("#263238"));
        }
    }


    /**
     * Turns the LED on when the button on the app is pressed.
     */
    // Any time the widget or the button in the app is pressed to turn the LED
    // on we process this off click. On method that is deprecated is needed for
    // earlier than Android 3.0 devices.
    //@SuppressWarnings("deprecation")
    private void processOnClick() {

        //flashlight_button.setBackgroundResource(R.drawable.light_off);
        //setWidgetTo(R.drawable.light_off);

        if (getmCameraActivity() == null) {
            try {
                mHolder.addCallback(this);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                }
                setmCameraActivity(Camera.open());
                try {
                    if (mHolder != null) {
                        getmCameraActivity().setPreviewDisplay(mHolder);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (getmCameraActivity() != null) {
            flashOnApp();
        }

        turnMotorolaOn();

    }

    private void flashOnApp() {

        Log.d(getString(R.string.TAG), "Clicked");

        setParams(getmCameraActivity().getParameters());

        List<String> flashModes = getParams().getSupportedFlashModes();

        if (flashModes == null) {
            return;
        } else {
            if (count == 0) {
                getParams().setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                getmCameraActivity().setParameters(getParams());
                //preview = (SurfaceView) findViewById(R.id.preview);
                mHolder = preview.getHolder();
                mHolder.addCallback(MainActivity.this);

                try {
                    getmCameraActivity().startPreview();
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }

            String flashMode = getParams().getFlashMode();

            if (!Camera.Parameters.FLASH_MODE_TORCH.equals(flashMode)) {

                if (flashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                    getParams().setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    getmCameraActivity().setParameters(getParams());
                    Log.d(getString(R.string.TAG), "Turned On");
                } else {
                    getParams().setFlashMode(Camera.Parameters.FLASH_MODE_ON);

                    getmCameraActivity().setParameters(getParams());

                    Log.d(getString(R.string.TAG), "Turned Off");

                    try {
                        getmCameraActivity().autoFocus(new Camera.AutoFocusCallback() {
                            public void onAutoFocus(boolean success,
                                                    Camera camera) {
                                count = 1;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }


    static void turnMotorolaOn() {
        DroidLED led;
        try {
            led = new DroidLED();
            led.enable(true);
            //AppGlobals.setIsFlashOn(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Any time the widget or the button in the app is pressed to turn the LED
    // off we process this off click.
    private void processOffClick() {

        if (getmCameraActivity() != null) {
            count = 0;
            flashOffApp();
        }

        turnMotorolaOff();

    }

    // Turns the LED off when the button on the app is pressed.
    private void flashOffApp() {
        getmCameraActivity().stopPreview();
        getmCameraActivity().release();
        setmCameraActivity(null);
    }


    // Turns the LED off for some Motorola phones.
    static void turnMotorolaOff() {
        DroidLED led;
        try {
            led = new DroidLED();
            led.enable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // Getters and setters for mCameraActivity.
    public static Camera getmCameraActivity() {
        return mCameraActivity;
    }

    public static void setmCameraActivity(Camera mCameraActivity) {
        MainActivity.mCameraActivity = mCameraActivity;
    }

    // Getters and setters for params.
    public Parameters getParams() {
        return params;
    }

    public void setParams(Parameters params) {
        this.params = params;
    }

    @Override
    protected void onPause() {
        super.onPause();
        /**
         * Don't need to turnoff camera
         */
//        turnOff();
//        mSurface.releaseCamera();
//        paused = true;

        /**
         * Stop Fan Turned Off
         */
        imgFan.clearAnimation();

        try {
            if (mp.isPlaying()) {
                mp.pause();
            }
        } catch (Exception e) {
            // NOTE: didn't think need any exception here
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /**
         * Don't need to turnoff camera
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
            color_intent.putExtra(getString(R.string.Color), strColor);
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

    // The following three methods are needed to implement SurfaceView.Callback.
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mHolder = holder;
        mHolder.addCallback(this);

        if (getmCameraActivity() != null) {

            try {
                getmCameraActivity().setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        mHolder = holder;
        mHolder.addCallback(this);

        if (getmCameraActivity() != null) {
            try {
                getmCameraActivity().setPreviewDisplay(mHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder = null;
    }
}
