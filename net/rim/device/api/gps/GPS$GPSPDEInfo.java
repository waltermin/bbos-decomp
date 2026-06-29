package net.rim.device.api.gps;

public class GPS$GPSPDEInfo {
   private int _ip;
   private int _port;
   private GPS$AppCredential _credential;

   public GPS$GPSPDEInfo() {
   }

   public GPS$GPSPDEInfo(int ip, int port, GPS$AppCredential cred) {
      this._ip = ip;
      this._port = port;
      this._credential = cred;
   }

   public int getIP() {
      return this._ip;
   }

   public int getPort() {
      return this._port;
   }

   public GPS$AppCredential getCredential() {
      return this._credential;
   }
}
