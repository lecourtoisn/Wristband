package com.polytech.al.wristband;

        import android.app.Activity;
        import android.content.Context;
        import android.graphics.*;
        import android.hardware.SensorListener;
        import android.hardware.SensorManager;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.os.SystemClock;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Config;
        import android.util.Log;
        import android.view.View;


/**
 * Récupération de l'orientation du téléphone :
 *
 * dans onSensorListener
 */
public class Compass extends AppCompatActivity {

    private static final String TAG = "Compass";

    private SensorManager mSensorManager;
    private SampleView mView;
    private float[] mValues;

    private final SensorListener mListener = new SensorListener() {

        public void onSensorChanged(int sensor, float[] values) {
            if (Config.LOGD) Log.d(TAG, "sensorChanged (" + values[0] + ", " + values[1] + ", " + values[2] + ")");
            mValues = values;
            if (mView != null) {
                mView.invalidate();
            }
        }

        public void onAccuracyChanged(int sensor, int accuracy) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume()
    {
        if (Config.LOGD) Log.d(TAG, "onResume");
        super.onResume();
        mSensorManager.registerListener(mListener,
                SensorManager.SENSOR_ORIENTATION,
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onStop()
    {
        if (Config.LOGD) Log.d(TAG, "onStop");
        mSensorManager.unregisterListener(mListener);
        super.onStop();
    }

    private class SampleView extends View {
        private Paint   mPaint = new Paint();
        private Path    mPath = new Path();
        private boolean mAnimate;
        private long    mNextTime;

        public SampleView(Context context) {
            super(context);

            // Construct a wedge-shaped path
            mPath.moveTo(0, -50);
            mPath.lineTo(-20, 60);
            mPath.lineTo(0, 50);
            mPath.lineTo(20, 60);
            mPath.close();
        }

        @Override protected void onDraw(Canvas canvas) {
            Paint paint = mPaint;

            canvas.drawColor(Color.WHITE);

            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);

            int w = canvas.getWidth();
            int h = canvas.getHeight();
            int cx = w / 2;
            int cy = h / 2;

            canvas.translate(cx, cy);
            if (mValues != null) {
                canvas.rotate(-mValues[0]);
            }
            canvas.drawPath(mPath, mPaint);
        }

        @Override
        protected void onAttachedToWindow() {
            mAnimate = true;
            super.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            mAnimate = false;
            super.onDetachedFromWindow();
        }
    }
}