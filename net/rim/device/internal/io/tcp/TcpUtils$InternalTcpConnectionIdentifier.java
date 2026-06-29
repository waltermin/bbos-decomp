package net.rim.device.internal.io.tcp;

import net.rim.device.api.util.StringUtilities;

final class TcpUtils$InternalTcpConnectionIdentifier implements TcpConnectionIdentifier {
   private int _ipAddress;
   private int _localPort;
   private int _destPort;
   private String _apn;

   private TcpUtils$InternalTcpConnectionIdentifier(int ipAddress, int localPort, int destPort, String apn) {
      this._ipAddress = ipAddress;
      this._localPort = localPort;
      this._destPort = destPort;
      this._apn = apn;
   }

   private TcpUtils$InternalTcpConnectionIdentifier(TcpConnectionIdentifier connId) {
      this._ipAddress = connId.getConnectionIpAddress();
      this._localPort = connId.getConnectionLocalPort();
      this._destPort = connId.getConnectionDestinationPort();
      this._apn = connId.getConnectionApn();
   }

   @Override
   public final boolean equals(Object o) {
      if (o instanceof TcpConnectionIdentifier) {
         if (o == this) {
            return true;
         }

         TcpConnectionIdentifier temp = (TcpConnectionIdentifier)o;
         return this._ipAddress == temp.getConnectionIpAddress()
            && this._localPort == temp.getConnectionLocalPort()
            && this._destPort == temp.getConnectionDestinationPort()
            && StringUtilities.strEqualIgnoreCase(this._apn, temp.getConnectionApn());
      } else {
         return false;
      }
   }

   @Override
   public final int getConnectionIpAddress() {
      return this._ipAddress;
   }

   @Override
   public final int getConnectionLocalPort() {
      return this._localPort;
   }

   @Override
   public final void setConnectionLocalPort(int port) {
      this._localPort = port;
   }

   @Override
   public final int getConnectionDestinationPort() {
      return this._destPort;
   }

   @Override
   public final String getConnectionApn() {
      return this._apn;
   }

   TcpUtils$InternalTcpConnectionIdentifier(int x0, int x1, int x2, String x3, TcpUtils$1 x4) {
      this(x0, x1, x2, x3);
   }

   TcpUtils$InternalTcpConnectionIdentifier(TcpConnectionIdentifier x0, TcpUtils$1 x1) {
      this(x0);
   }
}
