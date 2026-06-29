package net.rim.plazmic.internal.contentpreview.dispatcher;

public final class ServiceInfo {
   private String _host;
   private int _port;
   private int _version;
   public static final String rcsid;
   private static final ServiceInfo DEFAULT_SERVICE_INFO;

   private ServiceInfo(String host, int port, int version) {
      this._host = host;
      this._port = port;
      this._version = version;
   }

   public static final ServiceInfo getDispatcherServiceInfo() {
      return DEFAULT_SERVICE_INFO;
   }

   public final String getHost() {
      return this._host;
   }

   public final int getPort() {
      return this._port;
   }

   public final int getVersion() {
      return this._version;
   }

   static {
      String host = "localhost";
      int port = 45654;
      int version = 2;
      DEFAULT_SERVICE_INFO = new ServiceInfo(host, port, version);
   }
}
