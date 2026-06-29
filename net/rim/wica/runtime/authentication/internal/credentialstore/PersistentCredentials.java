package net.rim.wica.runtime.authentication.internal.credentialstore;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;
import net.rim.wica.runtime.persistence.Recryptable;

class PersistentCredentials implements Credentials, Persistable, Recryptable {
   private String _url;
   private int _scheme;
   private Object _username;
   private String _domain;
   private Object _secretToken;

   PersistentCredentials(String url, int scheme, String username, String domain, byte[] secretToken) {
      this._url = url;
      this._scheme = scheme;
      this._domain = domain;
      this._username = PersistentContent.encode(username);
      this._secretToken = PersistentContent.encode(secretToken);
   }

   String getUrl() {
      return this._url;
   }

   @Override
   public int getScheme() {
      return this._scheme;
   }

   @Override
   public String getUsername() {
      return PersistentContent.decodeString(this._username);
   }

   @Override
   public String getDomain() {
      return this._domain;
   }

   @Override
   public byte[] getSecretToken() {
      return PersistentContent.decodeByteArray(this._secretToken);
   }

   @Override
   public void recrypt() {
      if (!PersistentContent.checkEncoding(this._secretToken)) {
         this._secretToken = PersistentContent.reEncode(this._secretToken, false, true);
      }
   }
}
