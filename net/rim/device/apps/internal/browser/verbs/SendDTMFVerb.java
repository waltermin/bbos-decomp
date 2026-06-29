package net.rim.device.apps.internal.browser.verbs;

import net.rim.device.api.system.Phone;
import net.rim.device.apps.api.framework.registration.DeviceFeature;
import net.rim.device.apps.internal.browser.resources.BrowserResources;

public final class SendDTMFVerb extends BrowserVerb {
   private byte[] _tone;
   private static final int DESCRIPTION;

   public SendDTMFVerb(byte[] tone) {
      super(341248);
      this._tone = tone;
   }

   @Override
   public final String toString() {
      return BrowserResources.getString(622);
   }

   @Override
   public final Object invoke(Object context) {
      if (this.isEnabled() && this._tone != null) {
         Phone phone = Phone.getInstance();
         int callId = phone.getActiveCallId();
         int duration = 1000;
         if (callId != 0) {
            try {
               int state = phone.getCallState(callId);
               if (state == 1 || state == 3) {
                  for (int i = 0; i < this._tone.length; i++) {
                     phone.startDTMF(callId, this._tone[i]);

                     label76:
                     try {
                        Thread.sleep(duration);
                     } finally {
                        break label76;
                     }

                     phone.stopDTMF(callId);
                  }
               }
            } finally {
               return null;
            }
         }
      }

      return null;
   }

   @Override
   public final boolean isEnabled() {
      return DeviceFeature.isPhoneEnabled();
   }
}
