package net.rim.device.api.crypto;

import net.rim.device.api.crypto.asn1.ASN1InputByteArray;

public class SafeContentsBag extends SafeBag {
   private SafeBag[] _safeBags;

   public SafeContentsBag(byte[] data, byte[] attributes, PKCS12ContentInfo parent) {
      super(data, attributes, parent);
   }

   @Override
   protected void parse() {
      if (!super._parsed) {
         try {
            PKCS12SafeContents safeContents = new PKCS12SafeContents(super._bagData, super._parent);
            this._safeBags = (SafeBag[])safeContents.getChildrenContentInfos();
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
      return this._safeBags;
   }

   public SafeBag[] getSafeBags() {
      this.parse();
      return this._safeBags;
   }
}
