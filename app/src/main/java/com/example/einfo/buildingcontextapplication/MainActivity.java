package com.example.einfo.buildingcontextapplication;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final Locale myLocale = new Locale("pt", "BR");
    private final int brightness = 80;
    private ContentResolver cResolver;
    private Window mWindow;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer, mLinearAcceleration, mGyroscope;
    private TextView mAccXAxis, mAccYAxis, mAccZAxis;
    private TextView mLaccXAxis, mLaccYAxis, mLaccZAxis;
    private TextView mGyroXAxis, mGyroYAxis, mGyroZAxis;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cResolver = getContentResolver();
        mWindow = getWindow();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mAccXAxis = (TextView) findViewById(R.id.acc_x_axis);
        mAccYAxis = (TextView) findViewById(R.id.acc_y_axis);
        mAccZAxis = (TextView) findViewById(R.id.acc_z_axis);

        mLaccXAxis = (TextView) findViewById(R.id.lacc_x_axis);
        mLaccYAxis = (TextView) findViewById(R.id.lacc_y_axis);
        mLaccZAxis = (TextView) findViewById(R.id.lacc_z_axis);

        mGyroXAxis = (TextView) findViewById(R.id.gyro_x_axis);
        mGyroYAxis = (TextView) findViewById(R.id.gyro_y_axis);
        mGyroZAxis = (TextView) findViewById(R.id.gyro_z_axis);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAccXAxis.setText(String.format(myLocale, "X-Axis: %.02f", event.values[0]));
            mAccYAxis.setText(String.format(myLocale, "Y-Axis: %.02f", event.values[1]));
            mAccZAxis.setText(String.format(myLocale, "Z-Axis: %.02f", event.values[2]));
        }

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            mLaccXAxis.setText(String.format(myLocale, "X-Axis: %.02f", event.values[0]));
            mLaccYAxis.setText(String.format(myLocale, "Y-Axis: %.02f", event.values[1]));
            mLaccZAxis.setText(String.format(myLocale, "Z-Axis: %.02f", event.values[2]));
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            mGyroXAxis.setText(String.format(myLocale, "X-Axis: %.02f", event.values[0]));
            mGyroYAxis.setText(String.format(myLocale, "Y-Axis: %.02f", event.values[1]));
            mGyroZAxis.setText(String.format(myLocale, "Z-Axis: %.02f", event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

}
