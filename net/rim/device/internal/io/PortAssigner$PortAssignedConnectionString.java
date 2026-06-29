package net.rim.device.internal.io;

public class PortAssigner$PortAssignedConnectionString {
   private boolean _portWasAssigned;
   private boolean _localPortWasAssigned;
   private int _port = -1;
   private int _localPort = -1;
   private String _connectionString;
   private static String COMPARISON_STRING = "://";
   private static String SLASH_SLASH = "//";

   public PortAssigner$PortAssignedConnectionString(String connectionString, int port, boolean portWasAssigned) {
      this._portWasAssigned = portWasAssigned;
      this._port = port;
      this._connectionString = connectionString;
   }

   public boolean getPortAssigned() {
      return this._portWasAssigned;
   }

   public boolean getLocalPortAssigned() {
      return this._localPortWasAssigned;
   }

   public void setPortAssigned(boolean portAssigned) {
      this._portWasAssigned = portAssigned;
   }

   public String getConnectionString() {
      return this._connectionString;
   }

   public void setConnectionString(String cs) {
      this._connectionString = cs;
   }

   public int getPort() {
      return this._port;
   }

   public void setPort(int port) {
      this._port = port;
   }

   public int getLocalPort() {
      return this._localPort;
   }

   void setLocalPort(int localPort) {
      this._localPort = localPort;
      this._localPortWasAssigned = true;
   }
}
