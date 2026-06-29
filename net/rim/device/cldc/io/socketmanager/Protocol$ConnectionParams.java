package net.rim.device.cldc.io.socketmanager;

final class Protocol$ConnectionParams {
   private String _apn = "blackberry.net";
   private String _username;
   private String _password;
   private byte[] _primaryDNS;
   private byte[] _secondaryDNS;
   String _hostName;
   int _port;
   String _interface = "cellular";
   String _retryNoContext;
   int _sessionTimeout = -1;

   private Protocol$ConnectionParams() {
   }

   Protocol$ConnectionParams(Protocol$1 x0) {
      this();
   }
}
