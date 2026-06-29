package net.rim.device.apps.internal.bis.utils.system;

import net.rim.device.api.system.DeviceInfo;

public final class DeviceIDs {
   private String _pin = Integer.toHexString(DeviceInfo.getDeviceId());
   private String _imeiESN = DeviceUtils.getIMEIESN();
   private String _imsi = DeviceUtils.getIMSI();
   private String _iccid = DeviceUtils.getICCID();
   private String _msisdn = DeviceUtils.getMSISDN();
   private static DeviceIDs _instance;

   private DeviceIDs() {
   }

   public static final DeviceIDs getInstance() {
      if (_instance == null) {
         _instance = new DeviceIDs();
      }

      return _instance;
   }

   public final String getPIN() {
      return this._pin;
   }

   public final String getIMEIESN() {
      return this._imeiESN;
   }

   public final String getIMSI() {
      return this._imsi;
   }

   public final String getICCID() {
      return this._iccid;
   }

   public final String getMSISDN() {
      return this._msisdn;
   }
}
