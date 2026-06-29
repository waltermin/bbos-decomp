package net.rim.device.apps.internal.iota;

public final class IOTARequest {
   private int _mode;
   private String _url;

   public IOTARequest(int mode, String url) {
      this._url = url;
      this._mode = mode;
   }

   public final String getUrl() {
      return this._url;
   }

   public final int getMode() {
      return this._mode;
   }
}
