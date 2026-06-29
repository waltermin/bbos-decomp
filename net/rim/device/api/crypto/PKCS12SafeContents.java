package net.rim.device.api.crypto;

import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.util.Arrays;

public class PKCS12SafeContents extends PKCS12ContentInfo {
   private SafeBag[] _contents;

   public PKCS12SafeContents(byte[] data, PKCS12ContentInfo parent) {
      super(data, parent);
   }

   @Override
   protected void parse() {
      if (!super._parsed) {
         try {
            ASN1InputByteArray bagsArray = (ASN1InputByteArray)(new Object(super._buffer));
            bagsArray.readSequence();
            int next = bagsArray.peekNextTag();
            SafeBag bag = null;

            while (next != -1) {
               if (next != 16) {
                  throw new PKCS12ParsingException();
               }

               bag = SafeBag.getSafeBag(bagsArray.readFieldAsByteArray(), this);
               if (this._contents == null) {
                  this._contents = new SafeBag[]{bag};
               } else {
                  Arrays.add(this._contents, bag);
               }

               next = bagsArray.peekNextTag();
            }

            super._parsed = true;
         } finally {
            throw new PKCS12ParsingException();
         }
      }
   }

   @Override
   public PKCS12ContentInfo[] getChildrenContentInfos() {
      this.parse();
      return this._contents;
   }
}
