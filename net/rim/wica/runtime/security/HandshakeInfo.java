package net.rim.wica.runtime.security;

public class HandshakeInfo {
   private String _agURL;
   private byte[] _deviceVersions;
   private int _devicePIN;
   private long _agId;
   private int _securityVersion;
   private boolean _resetState;
   private long _deviceId;
   private byte[] _serverVersions;

   public HandshakeInfo(String agURL, int devicePIN, long agId, byte[] deviceVersions) {
      this(agURL, devicePIN, agId, deviceVersions, 0);
   }

   public HandshakeInfo(String agURL, int devicePIN, long agId, int securityVersion) {
      this(agURL, devicePIN, agId, null, securityVersion);
   }

   private HandshakeInfo(String agURL, int devicePIN, long agId, byte[] deviceVersions, int securityVersion) {
      this._agURL = agURL;
      this._devicePIN = devicePIN;
      this._agId = agId;
      this._deviceVersions = deviceVersions;
      this._securityVersion = securityVersion;
   }

   public String getAGURL() {
      return this._agURL;
   }

   public boolean getResetState() {
      return this._resetState;
   }

   public void setResetState(boolean reset) {
      this._resetState = reset;
   }

   public byte[] getDeviceVersions() {
      return this._deviceVersions;
   }

   public int getDevicePIN() {
      return this._devicePIN;
   }

   public long getAGId() {
      return this._agId;
   }

   public long getDeviceId() {
      return this._deviceId;
   }

   public int getSecurityVersion() {
      return this._securityVersion;
   }

   public byte[] getServerVersions() {
      return this._serverVersions;
   }

   public void setSecurityVersion(int version) {
      this._securityVersion = version;
   }

   public void setDeviceId(long id) {
      this._deviceId = id;
   }

   public void setAGId(long id) {
      this._agId = id;
   }

   public void setServerVersions(byte[] versions) {
      this._serverVersions = versions;
   }
}
