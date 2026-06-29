package net.rim.device.apps.api.ui;

import net.rim.device.api.ui.component.GaugeField;

final class ProgressIndicator$SetProgressRunnable implements Runnable {
   private GaugeField _gaugeField;
   private int _value;

   private ProgressIndicator$SetProgressRunnable() {
   }

   final void setParameters(GaugeField gaugeField, int value) {
      this._gaugeField = gaugeField;
      this._value = value;
   }

   @Override
   public final void run() {
      this._gaugeField.setValue(this._value);
   }

   ProgressIndicator$SetProgressRunnable(ProgressIndicator$1 x0) {
      this();
   }
}
