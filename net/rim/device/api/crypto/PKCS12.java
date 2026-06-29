package net.rim.device.api.crypto;

import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public class PKCS12 {
   private byte[] _buffer;
   private AuthenticatedSafe _safe;
   private int _version;
   private boolean _isSigned;
   private boolean _parsed;

   public PKCS12(byte[] data) {
      this._buffer = data;
      this._isSigned = false;
   }

   public void parse() {
      if (this._buffer != null) {
         if (!this._parsed) {
            try {
               ASN1InputByteArray pfx = (ASN1InputByteArray)(new Object(this._buffer));
               pfx.readSequence();
               this._version = pfx.readInteger();
               if (this._version != 3) {
                  throw new PKCS12UnsupportedOperationException();
               }

               pfx.readSequence();
               OID contentType = pfx.readOID();
               byte[] data = pfx.readOctetString(1, 0);
               if (contentType.equals(OIDs.getOID(541859388))) {
                  this._safe = new AuthenticatedSafe(data, null);
                  this._parsed = true;
               } else if (contentType.equals(OIDs.getOID(542121532))) {
                  this._safe = (AuthenticatedSafe)new PKCS12SignedData(data, null).getData();
                  this._isSigned = true;
                  this._parsed = true;
               } else {
                  throw new PKCS12ParsingException();
               }
            } finally {
               throw new PKCS12ParsingException();
            }
         }
      }
   }

   public AuthenticatedSafe getAuthenticatedSafe() {
      this.parse();
      return this._safe;
   }

   public boolean isSigned() {
      this.parse();
      return this._isSigned;
   }

   public int getVersion() {
      this.parse();
      return this._version;
   }
}
