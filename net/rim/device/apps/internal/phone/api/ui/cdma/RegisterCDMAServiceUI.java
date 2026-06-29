package net.rim.device.apps.internal.phone.api.ui.cdma;

import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.apps.api.phone.VoiceServices;

public final class RegisterCDMAServiceUI {
   public static final void main(String[] args) {
      if (RadioInfo.getNetworkType() == 4) {
         ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
         ar.waitFor(5533786620429140277L);
         VoiceServices.addPhoneNumberFilter(new CDMAServiceFilter());
      }
   }
}
