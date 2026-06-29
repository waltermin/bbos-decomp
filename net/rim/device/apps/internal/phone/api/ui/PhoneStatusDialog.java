package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;

public final class PhoneStatusDialog extends PopupScreen implements Runnable {
   private GaugeField _gaugeField;
   private int _count;
   private int _numProgressUpdates;

   public PhoneStatusDialog(String message) {
      this(message, 0);
   }

   public PhoneStatusDialog(String message, int numStages) {
      super((Manager)(new Object()));
      this._numProgressUpdates = numStages;
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setMessage((RichTextField)(new Object(message, 36028797018963968L)));
      BitmapField bmpField = (BitmapField)(new Object(Bitmap.getPredefinedBitmap(3)));
      dfm.setIcon(bmpField);
      if (this._numProgressUpdates > 1) {
         this._gaugeField = (GaugeField)(new Object(null, 0, 100, 5, 2));
         dfm.addCustomField(this._gaugeField);
      }
   }

   public final void updateProgress() {
      this._count++;
      Application.getApplication().invokeLater(this);
   }

   @Override
   protected final boolean keyChar(char key, int status, int time) {
      return true;
   }

   @Override
   protected final boolean trackwheelClick(int status, int time) {
      return true;
   }

   @Override
   protected final boolean stylusTap(int x, int y, int status, int time) {
      return true;
   }

   @Override
   public final void run() {
      if (this._count == this._numProgressUpdates) {
         this._gaugeField.setValue(100);
      } else {
         this._gaugeField.setValue(this._count * (100 / this._numProgressUpdates));
      }
   }
}
