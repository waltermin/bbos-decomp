package net.rim.device.internal.browser.wap;

import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.Phone;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SIMCard;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.api.util.StringUtilities;

public final class ClientID {
   public static final int ASSIGNED_TYPE = 0;
   public static final int NATIVE_TYPE_ICCID = 1;
   public static final int NATIVE_TYPE_MIN = 2;
   public static final int NATIVE_TYPE_ESN = 3;
   public static final int NATIVE_TYPE_MSISDN = 4;
   public static final int NATIVE_TYPE_IMSI = 5;
   public static final int NATIVE_TYPE_IPV4 = 6;
   public static final int NATIVE_TYPE_IPV6 = 7;
   public static final int NATIVE_TYPE_ITSI = 8;
   public static final int NATIVE_TYPE_MAN = 9;
   public static final int NATIVE_TYPE_SIMID = 10;
   public static final int NATIVE_TYPE_MDN = 11;

   private ClientID() {
   }

   public static final String getClientId(int type, String apn) {
      return getClientId(type, apn, true);
   }

   public static final String getClientId(int type, String apn, boolean prefixIdType) {
      String value = null;

      label120:
      try {
         int networkType = RadioInfo.getNetworkType();
         switch (type) {
            case 0:
            case 2:
            case 7:
            case 8:
            case 9:
               return null;
            case 1:
            default:
               if (networkType == 3 || networkType == 7) {
                  value = SIMCard.iccidToString(SIMCard.getICCID());
               }
            case 3:
               if (networkType == 4) {
                  value = StringUtilities.toUpperCase(Integer.toHexString(CDMAInfo.getESN()), 1701707776);
               }
               break;
            case 4:
            case 11:
               value = Phone.getInstance().getNumber(0);
               break;
            case 5:
               if (networkType == 4) {
                  byte[] imsi = CDMAInfo.getIMSI();
                  StringBuffer buff = new StringBuffer();

                  for (int i = 0; i < imsi.length; i++) {
                     NumberUtilities.appendNumber(buff, imsi[i], 16);
                  }

                  value = buff.toString();
               } else if (networkType == 3 || networkType == 7) {
                  value = SIMCard.imsiToString(SIMCard.getIMSI());
               }
               break;
            case 6:
               if (apn == null) {
                  apn = "";
               }

               int apnId = RadioInfo.getAccessPointNumber(apn);
               byte[] ipv4 = RadioInfo.getIPAddress(apnId);
               StringBuffer buff = new StringBuffer();

               for (int i = 0; i < ipv4.length; i++) {
                  NumberUtilities.appendNumber(buff, ipv4[i], 10, 3);
               }

               value = buff.toString();
               break;
            case 10:
               if (networkType == 5) {
                  value = "0-" + SIMCard.iccidToString(SIMCard.getICCID());
                  type = 0;
               }
         }
      } finally {
         break label120;
      }

      if (value == null) {
         return null;
      } else {
         return prefixIdType ? Integer.toString(type) + value : value;
      }
   }
}
