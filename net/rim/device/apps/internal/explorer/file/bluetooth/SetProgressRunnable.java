package net.rim.device.apps.internal.explorer.file.bluetooth;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.component.GaugeField;

final class SetProgressRunnable implements Runnable {
   private GaugeField _gaugeField;
   private int _value;

   public SetProgressRunnable(GaugeField field) {
      this._gaugeField = field;
   }

   final void setProgress(int value) {
      this._value = value;
      Application.getApplication().invokeAndWait(this);
   }

   @Override
   public final void run() {
      this._gaugeField.setValue(this._value);
   }
}
