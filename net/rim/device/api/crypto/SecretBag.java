package net.rim.device.api.crypto;

import net.rim.device.api.crypto.asn1.ASN1InputByteArray;

public class SecretBag extends SafeBag {
   public SecretBag(byte[] data, byte[] attributes, PKCS12ContentInfo parent) {
      super(data, attributes, parent);
   }

   @Override
   protected void parse() {
      if (!super._parsed) {
         try {
            new Object(super._bagData);
            if (super._bagAttributes != null) {
               ASN1InputByteArray keyBagAttributes = (ASN1InputByteArray)(new Object(super._bagAttributes));

               for (int next = keyBagAttributes.peekNextTag(); next != -1; next = keyBagAttributes.peekNextTag()) {
                  if (next != 16) {
                     throw new PKCS12ParsingException();
                  }

                  keyBagAttributes.readSequence();
                  super._attributes.addElement(new PKCS12Attribute(keyBagAttributes.readOID(), keyBagAttributes.readFieldAsByteArray()));
               }
            }

            super._parsed = true;
         } finally {
            throw new PKCS12ParsingException();
         }
      }
   }

   @Override
   public PKCS12ContentInfo[] getChildrenContentInfos() {
      return null;
   }

   public byte[] getSecret() {
      this.parse();
      return null;
   }
}
