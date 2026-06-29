package net.rim.device.internal.system;

import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.GlobalEventListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.ui.component.SimpleChoiceDialog;

public class USBPasswordRedirectManager$MessageStatusDialog extends SimpleChoiceDialog implements GlobalEventListener, SystemListener2 {
   private Application _app;

   public USBPasswordRedirectManager$MessageStatusDialog(String message) {
      super(message, new String[]{CommonResource.getString(100)}, 0, Bitmap.getPredefinedBitmap(0), 134217728);
      this.setStatusPriority(-2147483644);
      this._app = Application.getApplication();
      this._app.addGlobalEventListener(this);
      this._app.addSystemListener(this);
   }

   @Override
   protected void close(int closeReason) {
      this._app.removeGlobalEventListener(this);
      this._app.removeSystemListener(this);
      super.close(closeReason);
   }

   @Override
   public void eventOccurred(long guid, int data0, int data1, Object object0, Object object1) {
      if (guid == 6345609069135580235L) {
         this.closeDialogLater();
      }
   }

   private void closeDialogLater() {
      this._app.invokeLater(new USBPasswordRedirectManager$MessageStatusDialog$1(this));
   }

   @Override
   public void usbConnectionStateChange(int state) {
      if (state == 4) {
         this.closeDialogLater();
      }
   }

   @Override
   public void powerOff() {
   }

   @Override
   public void powerUp() {
   }

   @Override
   public void batteryLow() {
   }

   @Override
   public void batteryGood() {
   }

   @Override
   public void batteryStatusChange(int status) {
   }

   @Override
   public void powerOffRequested(int reason) {
   }

   @Override
   public void cradleMismatch(boolean mismatch) {
   }

   @Override
   public void fastReset() {
   }

   @Override
   public void backlightStateChange(boolean on) {
   }
}
