package net.rim.device.api.crypto.tls;

import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.CRC32;

class NvStoreInfo {
   private byte[] _sessionID;
   private byte[] _masterSecret;
   private int _cipherSuite;
   private String _identifier;
   private boolean _allowDelete;
   private int _hashCode;

   public NvStoreInfo(String identifier, byte[] sessionID, byte[] masterSecret, int cipherSuite, boolean allowDelete) {
      this._identifier = identifier;
      this._sessionID = Arrays.copy(sessionID);
      this._masterSecret = Arrays.copy(masterSecret);
      this._cipherSuite = cipherSuite;
      this._allowDelete = allowDelete;
      this._hashCode = 0;
      CRC32.update(this._hashCode, sessionID);
      CRC32.update(this._hashCode, masterSecret);
      CRC32.update(this._hashCode, cipherSuite);
   }

   public boolean getAllowDeleteSession() {
      return this._allowDelete;
   }

   public String getIdentifier() {
      return this._identifier;
   }

   public byte[] getSessionID() {
      return Arrays.copy(this._sessionID);
   }

   public byte[] getMasterSecret() {
      return Arrays.copy(this._masterSecret);
   }

   public int getCipherSuite() {
      return this._cipherSuite;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof NvStoreInfo) {
         NvStoreInfo info = (NvStoreInfo)obj;
         if (this._hashCode == info._hashCode
            && Arrays.equals(info.getSessionID(), this._sessionID)
            && Arrays.equals(info.getMasterSecret(), this._masterSecret)
            && info.getCipherSuite() == this._cipherSuite) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int hashCode() {
      return this._hashCode;
   }
}
