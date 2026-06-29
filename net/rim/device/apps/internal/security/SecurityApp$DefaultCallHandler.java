package net.rim.device.apps.internal.security;

import net.rim.device.api.itpolicy.ITPolicy;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.framework.registration.DeviceFeature;
import net.rim.device.apps.api.phone.VoiceApplication;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.system.Security;
import net.rim.device.internal.system.SecurityCallHandler;

public final class SecurityApp$DefaultCallHandler implements SecurityCallHandler {
   boolean _hotkeyInvoked;
   private final SecurityApp this$0;

   protected SecurityApp$DefaultCallHandler(SecurityApp _1) {
      this.this$0 = _1;
   }

   @Override
   public final boolean isEnabled() {
      return DeviceFeature.isPhoneEnabled();
   }

   @Override
   public final boolean emergencyCallSupported() {
      VoiceApplication voiceApp = this.getVoiceApp();
      return voiceApp != null && !VoiceServices.isPhoneActive();
   }

   @Override
   public final boolean outgoingCallSupported() {
      return this.emergencyCallSupported() && Security.getInstance().getAllowOutgoingCallWhileLocked();
   }

   @Override
   public final void makeEmergencyCall() {
      this.makeEmergencyCall(true);
   }

   @Override
   public final void makeEmergencyCall(boolean confirmBeforeCall) {
      VoiceApplication voiceApp = this.getVoiceApp();
      if (voiceApp != null && (!confirmBeforeCall || this.confirmEmergencyCall())) {
         String emergencyNumber = Phone.getInstance().getEmergencyNumber();
         if (emergencyNumber == null) {
            emergencyNumber = this.getEmergencyNumber();
         }

         voiceApp.startEmergencyCall(emergencyNumber, true);
      }
   }

   private final boolean confirmEmergencyCall() {
      Dialog dlg = new SecurityApp$DefaultCallHandler$1(this, 3, this.this$0._rb.getString(505), -1, null, 0);
      dlg.doModal();
      return dlg.getSelectedValue() == 4;
   }

   @Override
   public final void placeCall() {
      VoiceApplication voiceApp = this.getVoiceApp();
      if (voiceApp != null) {
         voiceApp.placeCall();
      }
   }

   private final VoiceApplication getVoiceApp() {
      if (this.isEnabled()) {
         try {
            return VoiceServices.getVoiceApplication();
         } finally {
            return null;
         }
      } else {
         return null;
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final String getEmergencyNumber() {
      if (RadioInfo.getNetworkType() == 4) {
         boolean var9 = false /* VF: Semaphore variable */;

         try {
            var9 = true;
            String id = RadioInternal.readNVString(2);
            if (id != null) {
               if (id.length() > 0) {
                  return id;
               }

               var9 = false;
            } else {
               var9 = false;
            }
         } finally {
            if (var9) {
               label109: {
                  String[] emergencyCallNumbers = new String[]{"911", "000", "08", "999"};

                  for (int i = emergencyCallNumbers.length - 1; i >= 0; i--) {
                     String num = emergencyCallNumbers[i];
                     if (Phone.getInstance().isEmergencyNumber(num)) {
                        return num;
                     }
                  }
                  break label109;
               }
            }
         }
      }

      int id = 507;
      switch (RadioInfo.getNetworkType()) {
         case 3:
            break;
         case 4:
         default:
            id = 508;
            break;
         case 5:
            id = 510;
            break;
         case 6:
            try {
               String retVal = ITPolicy.getString(36, 17);
               return retVal != null && retVal.length() != 0 ? retVal : "911";
            } finally {
               ;
            }
      }

      String number = this.this$0._rb.getString(id);
      return Phone.getInstance().isEmergencyNumber(number) ? number : Phone.getInstance().getEmergencyNumber();
   }
}
