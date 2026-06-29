package net.rim.device.api.crypto;

import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public class PKCS12Data extends PKCS12ContentInfo {
   private PKCS12ContentInfo _content;

   public PKCS12Data(byte[] data, PKCS12ContentInfo parent) {
      super(data, parent);
   }

   @Override
   public void parse() {
      if (!super._parsed) {
         try {
            ASN1InputByteArray dataStream = (ASN1InputByteArray)(new Object(super._buffer));
            int next = dataStream.peekNextTag();
            if (next != 16) {
               throw new PKCS12ParsingException();
            }

            dataStream.readSequence();
            byte[] content = dataStream.readFieldAsByteArray();
            next = dataStream.peekNextTag();
            if (next == -1) {
               this._content = SafeBag.getSafeBag(content, this);
               super._parsed = true;
            } else {
               ASN1InputByteArray array = (ASN1InputByteArray)(new Object(content));
               array.readSequence();
               OID oid = array.readOID();
               if (!oid.equals(OIDs.getOID(541859388)) && !oid.equals(OIDs.getOID(543170108)) && !oid.equals(OIDs.getOID(542383676))) {
                  if (!oid.equals(OIDs.getOID(312467715))
                     && !oid.equals(OIDs.getOID(312467716))
                     && !oid.equals(OIDs.getOID(312467713))
                     && !oid.equals(OIDs.getOID(312467714))
                     && !oid.equals(OIDs.getOID(314564870))
                     && !oid.equals(OIDs.getOID(312467717))) {
                     throw new PKCS12ParsingException();
                  }

                  this._content = new PKCS12SafeContents(super._buffer, this);
                  super._parsed = true;
               } else {
                  this._content = new AuthenticatedSafe(super._buffer, this);
                  super._parsed = true;
               }
            }
         } finally {
            throw new PKCS12ParsingException();
         }
      }
   }

   @Override
   public PKCS12ContentInfo[] getChildrenContentInfos() {
      this.parse();
      return new PKCS12ContentInfo[]{this._content};
   }
}
