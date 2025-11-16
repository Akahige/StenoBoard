// 23oct24abu
// (c) Software Lab. Alexander Burger

package de.software_lab.stenoboard;

import android.view.*;
import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.view.inputmethod.EditorInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class StenoIME extends InputMethodService implements SensorEventListener {
   StenoView SV;
   Sensor Gyroscope;
   SensorManager SMan;
   boolean Click;
   int GyroX, GyroY, GyroZ;

   @Override public View onCreateCandidatesView() {
      SV = (StenoView)getLayoutInflater().inflate(R.layout.input, null);
      SV.Ime = this;
      SV.reset();
      return SV;
   }

   @Override public void onInitializeInterface() {
      super.onInitializeInterface();
      setCandidatesViewShown(true);
   }

   public void onAccuracyChanged(Sensor sensor, int accuracy) {}

   public void onSensorChanged(SensorEvent event) {
      int x = (int)(event.values[0] * 1000.0);
      int y = (int)(event.values[1] * 1000.0);
      int z = (int)(event.values[2] * 1000.0);

      if (x != GyroX  ||  y != GyroY  ||  z != GyroZ) {
         SV.text("Δ" +
            Math.abs(x) +
            (x >= 0? '+' : '-') +
            Math.abs(y) +
            (y >= 0? '+' : '-') +
            Math.abs(z) +
            (z >= 0? '+' : '-') );
         GyroX = x;
         GyroY = y;
         GyroZ = z;
      }
   }

   @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
      if (!isInputViewShown())
         return false;
      if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
         if (Click = event.getRepeatCount() == 0) {
            if (SMan == null)
               Gyroscope = (SMan = (SensorManager)getSystemService(Context.SENSOR_SERVICE)).getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            SMan.registerListener(this, Gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
         }
         return true;
      }
      return super.onKeyDown(keyCode, event);
   }

   @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
      if (!isInputViewShown())
         return false;
      if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
         if (Click)
            SV.text("Δ" + (keyCode == KeyEvent.KEYCODE_VOLUME_UP? "+" : "-"));
         SMan.unregisterListener(this);
         return true;
      }
      return super.onKeyUp(keyCode, event);
   }
}
