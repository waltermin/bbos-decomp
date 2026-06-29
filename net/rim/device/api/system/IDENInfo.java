package net.rim.device.api.system;

public final class IDENInfo {
   private byte[] _imei;
   private int _homeMCC;
   private int _homeNDC;
   private String _homeNetworkName;
   private static IDENInfo _info;

   private IDENInfo() {
   }

   private static final synchronized IDENInfo getInfo() {
      if (_info == null) {
         _info = new IDENInfo();
      }

      getInfo(_info);
      return _info;
   }

   private static final native void getInfo(IDENInfo var0);

   public static final byte[] getIMEI() {
      return getInfo()._imei;
   }

   public static final String imeiToString(byte[] imei) {
      if (imei == null) {
         return null;
      }

      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < imei.length; i++) {
         sb.append((char)(imei[i] + 48));
      }

      return sb.toString();
   }

   public static final String getRegistrationAddress() {
      return null;
   }

   public static final int getHomeMCC() {
      return getInfo()._homeMCC;
   }

   public static final int getHomeNDC() {
      return getInfo()._homeNDC;
   }

   public static final String getHomeNetworkName() {
      return getInfo()._homeNetworkName;
   }

   public static final IDENInfo$IDENCellInfo getCellInfo() {
      IDENInfo$IDENCellInfo cellInfo = new IDENInfo$IDENCellInfo(null);
      getCellInfo(cellInfo);
      return cellInfo;
   }

   public static final native int getSQELevel();

   private static final native void getCellInfo(IDENInfo$IDENCellInfo var0);
}
