package net.rim.device.apps.internal.phone;

import net.rim.device.apps.internal.phone.api.ui.GetPhoneNumberDialog;
import net.rim.device.apps.internal.phone.pattern.SmartDialingOptions;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

final class VoiceApp$1 implements Runnable {
   @Override
   public final void run() {
      String defaultPhoneNumber = new GetPhoneNumberDialog(PhoneResources.getString(150)).getPhoneNumber();
      if (defaultPhoneNumber != null && defaultPhoneNumber.length() > 0) {
         SmartDialingOptions smartDialingOptions = SmartDialingOptions.getOptions();
         smartDialingOptions.setCorporatePhoneNumber(defaultPhoneNumber.trim());
         smartDialingOptions.commit();
      }
   }
}
