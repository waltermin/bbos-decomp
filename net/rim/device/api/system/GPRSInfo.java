package net.rim.device.api.system;

import net.rim.device.internal.system.SE13NetworkTable;

public final class GPRSInfo {
   private byte[] _imei;
   private byte[] _imeisv;
   private int _gprsState;
   private String _zoneName;
   public static final int GPRS_STATE_IDLE;
   public static final int GPRS_STATE_STANDBY;
   public static final int GPRS_STATE_READY;
   private static GPRSInfo _info;

   private GPRSInfo() {
   }

   public static final GPRSInfo$GPRSCellInfo[] createCellInfoArray() {
      GPRSInfo$GPRSCellInfo[] cellInfoArray = new GPRSInfo$GPRSCellInfo[11];

      for (int index = cellInfoArray.length - 1; index >= 0; index--) {
         cellInfoArray[index] = new GPRSInfo$GPRSCellInfo(null);
      }

      return cellInfoArray;
   }

   private static final synchronized GPRSInfo getInfo() {
      if (_info == null) {
         _info = new GPRSInfo();
      }

      getInfo(_info);
      return _info;
   }

   private static final native void getInfo(GPRSInfo var0);

   public static final byte[] getIMEI() {
      return getInfo()._imei;
   }

   public static final byte[] getIMEISV() {
      return getInfo()._imeisv;
   }

   public static final String imeiToString(byte[] imei) {
      return imeiToString(imei, true);
   }

   public static final int getHomeMCC() {
      int mcc = -1;

      try {
         byte[] imsi = SIMCard.getIMSI();
         if (imsi != null) {
            mcc = imsi[0] << 8;
            mcc |= imsi[1] << 4;
            return mcc | imsi[2];
         }
      } catch (SIMCardException var2) {
      }

      return mcc;
   }

   public static final int getHomeMNC() {
      int mnc = -1;

      try {
         byte[] imsi = SIMCard.getIMSI();
         if (imsi != null) {
            mnc = imsi[3] << 4;
            mnc |= imsi[4];
            int mcc = imsi[0] << 8;
            mcc |= imsi[1] << 4;
            mcc |= imsi[2];
            SE13NetworkTable se13NetTable = (SE13NetworkTable)ApplicationRegistry.getApplicationRegistry().waitFor(-7927117593081548760L);
            if (DeviceInfo.isSimulator() || se13NetTable != null && se13NetTable.is3DigitMNC(mnc << 16 | mcc)) {
               mnc <<= 4;
               return mnc | imsi[5];
            }
         }
      } catch (SIMCardException var4) {
      }

      return mnc;
   }

   public static final String imeiToString(byte[] imei, boolean addPeriods) {
      if (imei == null) {
         return null;
      }

      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < imei.length; i++) {
         sb.append((char)(imei[i] + 48));
         if (addPeriods && (i == 5 || i == 7 || i == 13)) {
            sb.append('.');
         }
      }

      return sb.toString();
   }

   public static final String imeisvToString(byte[] imeisv, boolean addPeriods) {
      if (imeisv == null) {
         return null;
      }

      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < imeisv.length - 2; i++) {
         sb.append((char)(imeisv[i] + 48));
         if (addPeriods && (i == 5 || i == 7 || i == 13)) {
            sb.append('.');
         }
      }

      sb.append((char)((imeisv[imeisv.length - 1] & 15) + 48));
      sb.append((char)((imeisv[imeisv.length - 1] >> 4 & 15) + 48));
      return sb.toString();
   }

   public static final int getGPRSState() {
      return getInfo()._gprsState;
   }

   public static final String getRegistrationAddress() {
      return null;
   }

   public static final String getZoneName() {
      return getInfo()._zoneName;
   }

   public static final GPRSInfo$GPRSCellInfo getCellInfo() {
      GPRSInfo$GPRSCellInfo cellInfo = new GPRSInfo$GPRSCellInfo(null);
      getCellInfo(cellInfo);
      return cellInfo;
   }

   private static final native void getCellInfo(GPRSInfo$GPRSCellInfo var0);

   public static final native int getCellInfo(GPRSInfo$GPRSCellInfo[] var0);
}
