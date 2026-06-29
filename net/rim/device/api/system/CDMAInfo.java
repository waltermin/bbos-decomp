package net.rim.device.api.system;

import net.rim.device.internal.system.RadioInternal;
import net.rim.vm.TraceBack;

public final class CDMAInfo {
   private int _esn;
   private byte[] _imsi;
   private int _hardwareVersion;
   private int _firmwareVersion;
   private int _prlVersion;
   private int _currentSID;
   private int _homeSID;
   private int _capabilities;
   private int _channelNumber;
   private int _bandClass;
   private int _modelNumber;
   private int _nvVersion;
   private short[] _homeSystemSIDs = new short[20];
   private short[] _homeSystemNIDs = new short[20];
   private boolean _smsStatusReportRequest;
   private boolean _smsDeliveryTimerSupport;
   private String _aKey;
   private static CDMAInfo _info;
   public static final int CAPABILITY_VOICE = 1;
   public static final int CAPABILITY_SMS = 16;
   public static final int CAPABILITY_SIP = 256;
   public static final int CAPABILITY_MIP = 512;
   public static final int CAPABILITY_GPS = 4096;

   private CDMAInfo() {
   }

   private static final synchronized CDMAInfo getInfo() {
      if (_info == null) {
         _info = new CDMAInfo();
      }

      getInfo(_info);
      return _info;
   }

   private static final native void getInfo(CDMAInfo var0);

   public static final int getESN() {
      return getInfo()._esn;
   }

   public static final String getRegistrationAddress() {
      return null;
   }

   public static final byte[] getIMSI() {
      return getInfo()._imsi;
   }

   public static final int getHardwareVersion() {
      return getInfo()._hardwareVersion;
   }

   public static final int getFirmwareVersion() {
      return getInfo()._hardwareVersion;
   }

   public static final int getPRLVersion() {
      return getInfo()._prlVersion;
   }

   public static final int getCurrentSID() {
      return getInfo()._currentSID;
   }

   public static final int getHomeSID() {
      return getInfo()._homeSID;
   }

   public static final int getCapabilities() {
      return getInfo()._capabilities;
   }

   public static final int getChannelNumber() {
      return getInfo()._channelNumber;
   }

   public static final int getBandClass() {
      return getInfo()._bandClass;
   }

   public static final int getModelNumber() {
      return getInfo()._modelNumber;
   }

   public static final int getNVVersion() {
      return getInfo()._nvVersion;
   }

   public static final short[] getHomeSystemSIDs() {
      return getInfo()._homeSystemSIDs;
   }

   public static final short[] getHomeSystemNIDs() {
      return getInfo()._homeSystemNIDs;
   }

   public static final CDMAInfo$CDMACellInfo getCellInfo() {
      CDMAInfo$CDMACellInfo cellInfo = new CDMAInfo$CDMACellInfo(null);
      getCellInfo(cellInfo);
      return cellInfo;
   }

   public static final boolean getSMSStatusReportRequest() {
      return getInfo()._smsStatusReportRequest;
   }

   public static final boolean getSMSDeliveryTimerSupport() {
      return getInfo()._smsDeliveryTimerSupport;
   }

   public static final String getAKey() {
      ControlledAccess.assertRRISignature(TraceBack.getCallingModule(0));
      return getInfo()._aKey;
   }

   public static final String getNAI(int profileIndex) {
      byte[] nai = RadioInternal.getNAI(profileIndex);
      return nai == null ? null : new String(nai);
   }

   private static final native void getCellInfo(CDMAInfo$CDMACellInfo var0);
}
