package net.rim.device.apps.internal.browser.util;

import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.internal.browser.util.IdEncryptor;

public final class BrowserIdEncryptor {
   public static final String RIM_ID_NAME = "XXX_RIM_ID";

   public static final String getEncryptedId() {
      String imeiESN = null;
      int wafs = RadioInfo.getSupportedWAFs();
      if ((wafs & 2) != 0) {
         imeiESN = String.valueOf(CDMAInfo.getESN());
      } else if ((wafs & 1) != 0) {
         imeiESN = GPRSInfo.imeiToString(GPRSInfo.getIMEI());
      } else if ((wafs & 8) != 0) {
         imeiESN = GPRSInfo.imeiToString(IDENInfo.getIMEI());
      } else {
         imeiESN = "UNKNOWN_NETWORK_TYPE_" + wafs;
      }

      String str = "0x" + Integer.toString(DeviceInfo.getDeviceId(), 16) + ' ' + imeiESN + ' ' + System.currentTimeMillis();
      return IdEncryptor.encrypt(str, 0);
   }
}
