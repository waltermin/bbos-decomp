package net.rim.device.api.crypto.cms;

import net.rim.device.api.crypto.oid.OID;

public final class CMSAttribute {
   private OID _oid;
   private byte[] _value;
   private boolean _isSigned;

   public CMSAttribute(OID oid, byte[] value, boolean isSigned) {
      this._oid = oid;
      this._value = value;
      this._isSigned = isSigned;
   }

   public final OID getOID() {
      return this._oid;
   }

   public final byte[] getValue() {
      return this._value;
   }

   public final boolean isSigned() {
      return this._isSigned;
   }

   public final int getLength() {
      return this._oid.toByteArray().length + this._value.length;
   }
}
