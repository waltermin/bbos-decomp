package net.rim.device.apps.api.ui;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.PersistentInteger;

public final class WorldPhoneDisclaimerDialog implements DialogClosedListener {
   private Dialog _disclaimerDialog;
   private int _previousMode;
   private static long KEY = 9108868554380525753L;
   private static int ID = PersistentInteger.getId(KEY, 0);

   public final void showDialog() {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      synchronized (this) {
         appReg.put(KEY, new Object());
      }

      Ui.getUiEngine().pushGlobalScreen(this._disclaimerDialog, 100, 2);
   }

   @Override
   public final void dialogClosed(Dialog dialog, int choice) {
      if (dialog == this._disclaimerDialog) {
         int newMode = 0;
         int persistVal = 0;
         byte var9;
         switch (choice) {
            case 4:
               newMode = 3;
               var9 = 1;
               break;
            default:
               var9 = 0;
               newMode = this._previousMode;
         }

         this.setNewMode(newMode);
         PersistentInteger.set(ID, var9);
      }

      ApplicationRegistry app = ApplicationRegistry.getApplicationRegistry();
      synchronized (this) {
         app.remove(KEY);
      }
   }

   private final void setNewMode(int mode) {
      RadioInternal.setEnabledRadios(mode);
      if (RadioInternal.getActiveRadios() != 0) {
         RadioInternal.activateRadios(mode);
      }
   }

   public WorldPhoneDisclaimerDialog(String msg, int previousMode) {
      this._disclaimerDialog = (Dialog)(new Object(3, msg, 4, Bitmap.getPredefinedBitmap(1), 0));
      this._disclaimerDialog.setEscapeEnabled(true);
      this._previousMode = previousMode;
      this._disclaimerDialog.setDialogClosedListener(this);
   }

   public static final boolean isDisclaimerNeeded() {
      ApplicationRegistry appReg = ApplicationRegistry.getApplicationRegistry();
      Object o = appReg.get(KEY);
      return PersistentInteger.get(ID) == 0 && o == null;
   }
}
