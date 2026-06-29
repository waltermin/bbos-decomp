package net.rim.wica.runtime.authentication.internal.credentialstore;

import net.rim.device.api.system.PersistentContent;
import net.rim.vm.Memory;
import net.rim.wica.runtime.persistence.Recryptable;

class TransientCredentials implements Credentials, Recryptable {
   private long _wicletId;
   private int _scheme;
   private String _username;
   private String _domain;
   private byte[] _secretToken;
   private boolean _ramOnly;

   long getWicletId() {
      return this._wicletId;
   }

   public boolean isEncoded() {
      return false;
   }

   @Override
   public String getUsername() {
      return this._username;
   }

   @Override
   public String getDomain() {
      return this._domain;
   }

   @Override
   public byte[] getSecretToken() {
      return this._secretToken;
   }

   @Override
   public int getScheme() {
      return this._scheme;
   }

   @Override
   public void recrypt() {
      if (PersistentContent.isEncryptionEnabled()) {
         if (!this._ramOnly) {
            this._secretToken = allocateRAMOnlyBytes(this._secretToken);
            this._ramOnly = true;
            return;
         }
      } else if (this._ramOnly) {
         this._secretToken = deallocateRAMOnlyBytes(this._secretToken);
         this._ramOnly = false;
      }
   }

   TransientCredentials(long wicletId, int scheme, String username, String domain, byte[] secretToken) {
      this._wicletId = wicletId;
      this._scheme = scheme;
      this._username = username;
      this._secretToken = secretToken;
      this._domain = domain;
      this.recrypt();
   }

   private static byte[] allocateRAMOnlyBytes(byte[] data) {
      byte[] copy = Memory.copyToRAMOnlyBytes(data);
      PersistentContent.markAsPlaintext(data);
      if (copy != null) {
         PersistentContent.markAsPlaintext(copy);
         return copy;
      } else {
         return data;
      }
   }

   private static byte[] deallocateRAMOnlyBytes(byte[] data) {
      byte[] copy = new byte[data.length];
      System.arraycopy(data, 0, copy, 0, data.length);
      PersistentContent.markAsPlaintext(data);
      return copy;
   }
}
