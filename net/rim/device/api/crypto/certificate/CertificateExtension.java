package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.util.Persistable;

public class CertificateExtension implements Persistable {
   private OID _oid;
   private boolean _critical;
   private byte[] _value;

   public CertificateExtension(OID oid, boolean critical, byte[] value) {
      this._oid = oid;
      this._critical = critical;
      this._value = value;
   }

   public OID getOID() {
      return this._oid;
   }

   public boolean getCritical() {
      return this._critical;
   }

   public byte[] getValue() {
      return CryptoUtilities.getImmutableOrCopiedByteArray(this._value);
   }
}
