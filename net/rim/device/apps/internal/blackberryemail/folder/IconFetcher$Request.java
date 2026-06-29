package net.rim.device.apps.internal.blackberryemail.folder;

class IconFetcher$Request {
   public String _appName;
   public String _serviceUID;
   public String _iconURL;
   public String _iconFileType;
   private int _version;
   public String _iconFolder;
   private byte _retryCount;
   private long _nextRetryTime;

   public IconFetcher$Request(String appName, String serviceUID, String iconURL, int version, String iconFolder, String iconFileType) {
      this._appName = appName;
      this._serviceUID = serviceUID;
      this._iconURL = iconURL;
      this._version = version;
      this._iconFolder = iconFolder;
      this._iconFileType = iconFileType;
      this._nextRetryTime = System.currentTimeMillis();
   }

   static byte access$208(IconFetcher$Request x0) {
      return x0._retryCount++;
   }
}
