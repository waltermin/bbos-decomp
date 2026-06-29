package net.rim.device.api.smartcard;

import net.rim.device.api.system.PersistentContent;
import net.rim.vm.Memory;

class CachedPasswordManagerImpl$CachedObject {
   private long _timeLastAccessed;
   private byte[] _passwordEncodingBytes;

   CachedPasswordManagerImpl$CachedObject(String password) {
      Object passwordEncoding = PersistentContent.encode(password);
      byte[] passwordEncodingBytes = PersistentContent.convertEncodingToByteArray(passwordEncoding);
      this._passwordEncodingBytes = Memory.allocRAMOnlyBytes(passwordEncodingBytes.length);
      System.arraycopy(passwordEncodingBytes, 0, this._passwordEncodingBytes, 0, passwordEncodingBytes.length);
      this._timeLastAccessed = System.currentTimeMillis();
   }

   String getPassword() {
      try {
         Object passwordEncoding = PersistentContent.convertByteArrayToEncoding(this._passwordEncodingBytes);
         return PersistentContent.decodeString(passwordEncoding);
      } finally {
         ;
      }
   }

   long getTimeLastAccessed() {
      return this._timeLastAccessed;
   }

   void setTimeLastAccessed() {
      this._timeLastAccessed = System.currentTimeMillis();
   }
}
