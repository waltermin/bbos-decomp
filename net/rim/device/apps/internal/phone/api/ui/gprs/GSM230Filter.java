package net.rim.device.apps.internal.phone.api.ui.gprs;

import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.apps.api.phone.PhoneNumberFilter;
import net.rim.device.apps.api.phone.VoiceServices;
import net.rim.device.apps.internal.phone.resource.PhoneResources;
import net.rim.device.internal.system.InternalServices;

public final class GSM230Filter extends PhoneNumberFilter {
   public static final int CODE_NONE = -1;
   public static final int CODE_DISPLAY_IMEI = 0;
   public static final int CODE_ENTER_PIN = 1;
   public static final int CODE_ENTER_PIN2 = 2;
   public static final int CODE_ENTER_PUK = 3;
   public static final int CODE_ENTER_PUK2 = 4;
   private static int GSM_SUPPORT_FLAGS = 0;
   private static String[] GSM_CODES = new String[]{"*#06#", "**04*", "**042*", "**05*", "**052*"};
   public static final int MIN_CODE_LENGTH = 5;

   public static final boolean isSupported() {
      return GSM_SUPPORT_FLAGS != 0;
   }

   public final void register() {
      VoiceServices.addPhoneNumberFilter(this);
   }

   public final void deregister() {
      VoiceServices.removePhoneNumberFilter(this);
   }

   public static final int getCode(String phoneNumber) {
      return GSM_SUPPORT_FLAGS != 0 && phoneNumber.length() >= 5 && phoneNumber.charAt(0) == 42 ? findMatchingCode(phoneNumber) : -1;
   }

   private static final int findMatchingCode(String phoneNumber) {
      for (int i = 0; i < GSM_CODES.length; i++) {
         if (phoneNumber.startsWith(GSM_CODES[i])) {
            return i;
         }
      }

      return -1;
   }

   private static final boolean isCodeSupported(int code) {
      return (GSM_SUPPORT_FLAGS & 1 << code) != 0;
   }

   @Override
   public final int startCall(String phoneNumber, int flags) {
      int code = getCode(phoneNumber);
      if (code != -1 && !isCodeSupported(code)) {
         String message = PhoneResources.getString(6120);
         Dialog.inform(message);
         return 0;
      }

      switch (code) {
         case -2:
            try {
               Phone.getInstance().startCall(phoneNumber, flags);
               return 0;
            } finally {
               ;
            }
         case -1:
         default:
            return this.getNextFilter().startCall(phoneNumber, flags);
         case 0:
            Dialog.inform(((StringBuffer)(new Object("IMEI: "))).append(GPRSInfo.imeiToString(GPRSInfo.getIMEI())).toString());
            return 0;
      }
   }

   private static final boolean isGSMDevice() {
      switch (RadioInfo.getNetworkType()) {
         case 3:
         case 7:
            return true;
         default:
            return false;
      }
   }

   private static final boolean isWorldDevice() {
      switch (InternalServices.getHardwareID()) {
         case 67112451:
            return false;
         case 67112452:
         default:
            return true;
      }
   }

   static {
      if (isGSMDevice()) {
         GSM_SUPPORT_FLAGS = 31;
      } else {
         if (isWorldDevice()) {
            GSM_SUPPORT_FLAGS = 27;
         }
      }
   }
}
