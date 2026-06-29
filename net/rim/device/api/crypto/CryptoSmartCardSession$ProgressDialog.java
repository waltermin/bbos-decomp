package net.rim.device.api.crypto;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.DialogFieldManager;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

class CryptoSmartCardSession$ProgressDialog extends PopupScreen {
   GaugeField _gaugeField;

   CryptoSmartCardSession$ProgressDialog(String title, int numSteps) {
      super(new DialogFieldManager());
      DialogFieldManager dfm = (DialogFieldManager)this.getDelegate();
      dfm.setMessage(new RichTextField(title, 36028797018963968L));
      dfm.setIcon(new BitmapField(Bitmap.getPredefinedBitmap(3)));
      this._gaugeField = new GaugeField(null, 0, numSteps, 0, 4);
      VerticalFieldManager vfm = new VerticalFieldManager();
      vfm.add(new SeparatorField());
      vfm.add(this._gaugeField);
      dfm.addCustomField(vfm);
   }

   void show() {
      Ui.getUiEngine().queueStatus(this, -2147483642, true);
   }

   void step(int delta) {
      synchronized (Application.getApplication().getAppEventLock()) {
         this._gaugeField.setValue(this._gaugeField.getValue() + delta);
      }
   }

   void set(int value) {
      synchronized (Application.getApplication().getAppEventLock()) {
         this._gaugeField.setValue(value);
      }
   }

   void dismiss() {
      synchronized (Application.getApplication().getAppEventLock()) {
         Ui.getUiEngine().dismissStatus(this);
      }
   }
}
