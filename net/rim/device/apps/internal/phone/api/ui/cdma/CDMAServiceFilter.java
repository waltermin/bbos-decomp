package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Branding;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;
import net.rim.device.apps.api.phone.PhoneNumberFilter;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.api.PhoneUtilities;
import net.rim.device.internal.EScreens.EScreenModel;
import net.rim.device.internal.system.EngineeringDataListener;
import net.rim.device.internal.system.InternalServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.USBPortInternal$Internal;
import net.rim.device.internal.ui.component.SimpleInputDialog;

final class CDMAServiceFilter extends PhoneNumberFilter implements EngineeringDataListener {
   private boolean _rtnRequested;
   private static final String DESKTOP_USB = "##3375867";
   private static final String MODEM_USB = "##66336";
   private static final String RTN = "786";
   private static final String RTN_RESTORE = "Restoring the device to factory defaults will erase all the personal information.";
   private static final String RTN_RESTORE_FAILED = "Device Restore failed...";
   private static final String FAST_100_CONFIG = "##266344";

   @Override
   public final int startCall(String phoneNumber, int flags) {
      if (phoneNumber.compareTo("#4357*") == 0) {
         VoiceServices.broadcastEvent(190000);
         UiApplication.getUiApplication().pushScreen(new CDMAStatusScreen());
         return 0;
      }

      if (phoneNumber.compareTo("##3375867") == 0) {
         try {
            VoiceServices.broadcastEvent(190000);
            USBPortInternal$Internal.setMode(0);
            return 0;
         } finally {
            ;
         }
      } else if (phoneNumber.compareTo("##66336") == 0) {
         try {
            VoiceServices.broadcastEvent(190000);
            USBPortInternal$Internal.setMode(1);
            return 0;
         } finally {
            ;
         }
      } else {
         if (phoneNumber.compareTo("##266344") == 0) {
            Application.getApplication().addRadioListener(this);
            RadioInternal.serviceProgramSetup(7, new byte[0]);
            return 0;
         }

         if (phoneNumber.length() > 2
            && phoneNumber.charAt(0) == '#'
            && phoneNumber.charAt(1) == '#'
            && phoneNumber.indexOf(35, 2) < 0
            && phoneNumber.indexOf(42, 2) < 0) {
            PhoneUtilities.setLastNumberDialed("");
            VoiceServices.broadcastEvent(190000);
            String password = phoneNumber.substring(2, phoneNumber.length());
            if (password.compareTo("786") == 0) {
               if (Branding.getVendorId() != 104 && Branding.getVendorId() != 225 && Branding.getVendorId() != 213) {
                  return this.getNextFilter().startCall(phoneNumber, flags);
               }

               SimpleInputDialog dlg = (SimpleInputDialog)(new Object(3, "Enter the MSL: ", 0, 6, 0));
               dlg.show();
               password = dlg.getText();
               if (password == null) {
                  return 0;
               }

               this._rtnRequested = true;
            }

            if (password.length() != 6) {
               Status.show("MSL password must contain 6 digits");
               return 0;
            } else {
               Application.getApplication().addRadioListener(this);
               RadioInternal.serviceProgramSetup(0, password.getBytes());
               return 0;
            }
         } else {
            return this.getNextFilter().startCall(phoneNumber, flags);
         }
      }
   }

   @Override
   public final void engDataInitialized() {
   }

   @Override
   public final void engDataChanged() {
   }

   @Override
   public final void engDataLogworthy(int type) {
   }

   @Override
   public final void engResponseMasterReset(int code) {
   }

   @Override
   public final void engOTASPResponse(byte[] response) {
   }

   @Override
   public final void engServiceProgramEvent(int code) {
      switch (code >> 8) {
         case 2:
            Dialog.alert("Parameter Read failed!");
            return;
         case 3:
            if (this._rtnRequested) {
               Application app = Application.getApplication();
               app.invokeLater(new CDMAServiceFilter$2(this));
            }

            this._rtnRequested = false;
            return;
         case 4:
            if (this._rtnRequested) {
               Dialog.inform("Device Restore failed...");
               this._rtnRequested = false;
            }
         case 1:
            return;
         case 6:
            if (this._rtnRequested) {
               ResourceBundle srb = ResourceBundle.getBundle(-1488627819050031640L, "net.rim.device.apps.internal.resource.Security");
               Dialog dlg = (Dialog)(new Object(
                  "Restoring the device to factory defaults will erase all the personal information.",
                  srb.getStringArray(702),
                  new int[]{0, 1, -805044213, 775162112, 774909491, 3420721, -805044199, 1699878656},
                  1,
                  Bitmap.getPredefinedBitmap(2),
                  33554432
               ));
               dlg.setEscapeEnabled(true);
               int result = dlg.doModal();
               if (result == 0) {
                  if (RadioInternal.processReturnRequest(1) != 1) {
                     Dialog.inform(srb.getString(705));
                     this._rtnRequested = false;
                     return;
                  }

                  UiApplication.getUiApplication().invokeLater(new CDMAServiceFilter$1(this));
                  return;
               }

               Dialog.inform(srb.getString(705));
               this._rtnRequested = false;
               return;
            }
         case 5:
            Application.getApplication().removeRadioListener(this);
            EScreenModel model = (EScreenModel)(new Object(0));
            model.setScreen(65534, 0);
            UiApplication.getUiApplication().pushModalScreen(new ServiceProgramUI(model));
            return;
         case 7:
         default:
            this._rtnRequested = false;
            Status.show("Invalid Password!");
            return;
         case 8:
            Status.show("Usage of programming lock code has exceeded the limit");
            return;
         case 9:
            Application.getApplication().removeRadioListener(this);
            Status.show("Too many password attempts, resetting device");
            InternalServices.initiateReset("CDMASrvPgm");
      }
   }
}
