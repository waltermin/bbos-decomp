package net.rim.device.api.crypto;

import net.rim.device.api.crypto.oid.OID;

public final class PKCS12Attribute {
   private OID _oid;
   private byte[] _value;

   public PKCS12Attribute(OID oid, byte[] value) {
      this._oid = oid;
      this._value = value;
   }

   public final OID getOID() {
      return this._oid;
   }

   public final byte[] getValue() {
      return this._value;
   }

   public final int getLength() {
      return this._oid.toByteArray().length + this._value.length;
   }
}
