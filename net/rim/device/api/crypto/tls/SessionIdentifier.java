package net.rim.device.api.crypto.tls;

import net.rim.device.api.system.PersistentContent;
import net.rim.device.api.util.Persistable;

class SessionIdentifier implements Persistable {
   private String _protocol;
   private Object _domainNameEncoding;
   private int _hashCode;

   public SessionIdentifier(String domainName, String protocol, boolean encrypt) {
      if (encrypt) {
         this._domainNameEncoding = PersistentContent.encode(domainName);
      } else {
         this._domainNameEncoding = domainName;
      }

      this._protocol = protocol;
      this._hashCode = domainName.hashCode() + protocol.hashCode();
   }

   public SessionIdentifier(String domainName, String protocol) {
      this(domainName, protocol, true);
   }

   public String getDomainName() {
      return PersistentContent.decodeString(this._domainNameEncoding);
   }

   public String getProtocol() {
      return this._protocol;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj instanceof SessionIdentifier) {
         SessionIdentifier session = (SessionIdentifier)obj;
         if (this._protocol.equals(session.getProtocol()) && this.getDomainName().equals(session.getDomainName())) {
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
