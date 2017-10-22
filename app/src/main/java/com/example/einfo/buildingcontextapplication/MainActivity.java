package com.example.einfo.buildingcontextapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.FloatMath;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    final Locale myLocale = new Locale("pt", "BR");
    final int BRIGHT_SCREEN = 100;
    final int DARK_SCREEN = 25;

    ContentResolver cResolver;
    Window mWindow;

    long lastShakeTimestamp;
    boolean isPlayingMusic;

    AudioManager mAudioManager;
    SensorManager mSensorManager;
    Sensor mAccelerometerSensor;
    TextView mAccXAxis, mAccYAxis, mAccZAxis;
    AppCompatButton mStartMusic;
    MediaPlayer mMediaPlayer;

    float[] mAccelerometerValues;
    float mAccelerometer, mAccelerometerLast, mAccelerometerCurrent;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
//                case R.id.navigation_dashboard:
//                    mTextMessage.setText(R.string.title_dashboard);
//                    return true;
                case R.id.navigation_settings:
                    Toast.makeText(MainActivity.this, "Settings option not available yet", Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }

    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (isPlayingMusic) {
                mMediaPlayer.pause();

                mStartMusic.setText("TOCAR MÚSICA");
                isPlayingMusic = false;
            } else {
                mMediaPlayer.start();

                mStartMusic.setText("PARAR MÚSICA");
                isPlayingMusic = true;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }

        cResolver = getContentResolver();
        mWindow = getWindow();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.megaman);
        mMediaPlayer.setLooping(true);

        try {
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mAccXAxis = (TextView) findViewById(R.id.acc_x_axis);
        mAccYAxis = (TextView) findViewById(R.id.acc_y_axis);
        mAccZAxis = (TextView) findViewById(R.id.acc_z_axis);

        mStartMusic = (AppCompatButton) findViewById(R.id.start_music);
        mStartMusic.setText("TOCAR MÚSICA");
        mStartMusic.setOnClickListener(mOnClickListener);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        isPlayingMusic = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccXAxis.setText(String.format(myLocale, "X-Axis: %.02f", event.values[0]));
            mAccYAxis.setText(String.format(myLocale, "Y-Axis: %.02f", event.values[1]));
            mAccZAxis.setText(String.format(myLocale, "Z-Axis: %.02f", event.values[2]));

            mAccelerometerValues = event.values.clone();
            mAccelerometerLast = mAccelerometerCurrent;
            mAccelerometerCurrent = (float) Math.sqrt(Math.pow(mAccelerometerValues[0], 2) +
                    Math.pow(mAccelerometerValues[1], 2) + Math.pow(mAccelerometerValues[2], 2));
            final float delta = mAccelerometerCurrent - mAccelerometerLast;
            mAccelerometer = mAccelerometer * 0.9f + delta;

            if (mAccelerometer > 3) {
                Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, BRIGHT_SCREEN);
                WindowManager.LayoutParams layoutpars = mWindow.getAttributes();
                layoutpars.screenBrightness = BRIGHT_SCREEN / 255f;
                mWindow.setAttributes(layoutpars);

                mAudioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                        0);

                lastShakeTimestamp = System.currentTimeMillis();
                Log.e("SHAKE", "true");
            } else {
                if (System.currentTimeMillis() > lastShakeTimestamp + 1500) {
                    Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, DARK_SCREEN);
                    WindowManager.LayoutParams layoutpars = mWindow.getAttributes();
                    layoutpars.screenBrightness = DARK_SCREEN / 255f;
                    mWindow.setAttributes(layoutpars);

                    mAudioManager.setStreamVolume(
                            AudioManager.STREAM_MUSIC,
                            4,
                            0);
                    Log.e("SHAKE", "false");
                }
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

}
