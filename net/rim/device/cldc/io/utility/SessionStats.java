package net.rim.device.cldc.io.utility;

public final class SessionStats {
   private int _bytesSent;
   private int _bytesReceived;
   private long _creationTime = System.currentTimeMillis();
   private String _connectedHost;
   private int _connectedPort;

   public final int getBytesSent() {
      return this._bytesSent;
   }

   public final int getBytesReceived() {
      return this._bytesReceived;
   }

   public final long getDuration() {
      return System.currentTimeMillis() - this._creationTime;
   }

   public final void addToSent(int count) {
      this._bytesSent += count;
   }

   public final void addToReceived(int count) {
      this._bytesReceived += count;
   }

   public final void setConnectedHost(String host, int port) {
      this._connectedHost = host;
      this._connectedPort = port;
   }

   public final String getConnectedHost() {
      return this._connectedHost;
   }

   public final int getConnectedPort() {
      return this._connectedPort;
   }
}
