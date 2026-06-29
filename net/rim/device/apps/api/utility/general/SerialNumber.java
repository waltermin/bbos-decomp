package net.rim.device.apps.api.utility.general;

import net.rim.device.api.system.CDMAInfo;
import net.rim.device.api.system.GPRSInfo;
import net.rim.device.api.system.IDENInfo;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLAN;
import net.rim.device.internal.bluetooth.BluetoothME;

public final class SerialNumber {
   public static final int MAC_ADDRESS_WLAN = 0;
   public static final int MAC_ADDRESS_BLUETOOTH = 1;
   private static final int HEAD_LEN = 3;
   private static final int MIDDLE_LEN = 2;
   private static final int TAIL_LEN = 6;

   private SerialNumber() {
   }

   public static final String getSerialNumber() {
      switch (RadioInfo.getNetworkType()) {
         case 2:
            return null;
         case 3:
         case 7:
            return GPRSInfo.imeiToString(GPRSInfo.getIMEI());
         case 4:
         default:
            return Integer.toHexString(CDMAInfo.getESN());
         case 5:
            return IDENInfo.imeiToString(IDENInfo.getIMEI());
         case 6:
            return getMACAddress(0);
      }
   }

   public static final String getSerialNumber(int waf) {
      switch (waf) {
         case 1:
            return GPRSInfo.imeiToString(GPRSInfo.getIMEI());
         case 2:
            return Integer.toHexString(CDMAInfo.getESN());
         case 4:
            return getMACAddress(0);
         case 8:
            return IDENInfo.imeiToString(IDENInfo.getIMEI());
         default:
            return null;
      }
   }

   public static final String getDecimalSerialNumber() {
      if (RadioInfo.getNetworkType() != 4) {
         throw new IllegalStateException();
      }

      int sn = CDMAInfo.getESN();
      int firstPart = sn >> 24 & 0xFF;
      int secondPart = sn & 16777215;
      StringBuffer esnStringBuffer = new StringBuffer();
      String firstPartString = String.valueOf(firstPart);
      String secondPartString = String.valueOf(secondPart);
      padESN(esnStringBuffer, 3 - firstPartString.length());
      esnStringBuffer.append(firstPartString);
      padESN(esnStringBuffer, 8 - secondPartString.length());
      esnStringBuffer.append(secondPartString);
      return esnStringBuffer.toString();
   }

   public static final String getDecimalSerialNumber(int waf) {
      if (waf != 2) {
         throw new IllegalStateException();
      }

      int sn = CDMAInfo.getESN();
      int firstPart = sn >> 24 & 0xFF;
      int secondPart = sn & 16777215;
      StringBuffer esnStringBuffer = new StringBuffer();
      String firstPartString = String.valueOf(firstPart);
      String secondPartString = String.valueOf(secondPart);
      padESN(esnStringBuffer, 3 - firstPartString.length());
      esnStringBuffer.append(firstPartString);
      padESN(esnStringBuffer, 8 - secondPartString.length());
      esnStringBuffer.append(secondPartString);
      return esnStringBuffer.toString();
   }

   private static final void padESN(StringBuffer esnStringBuffer, int padCount) {
      for (int i = 0; i < padCount; i++) {
         esnStringBuffer.append('0');
      }
   }

   public static final String getMACAddress(int macType) {
      String macAddress = null;
      switch (macType) {
         case -1:
            break;
         case 0:
         default:
            if (WLAN.isSupported()) {
               byte[] mac = WLAN.getMACAddress();
               if (mac != null && mac.length > 0) {
                  return WLAN.bssidToString(mac);
               }
            }
            break;
         case 1:
            if (BluetoothME.isSupported() && BluetoothME.isPowerOn()) {
               byte[] mac = BluetoothME.getLocalDeviceAddress();
               if (mac != null) {
                  macAddress = BluetoothME.deviceAddressToString(mac);
               }
            }
      }

      return macAddress;
   }
}
