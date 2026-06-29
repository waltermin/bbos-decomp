package net.rim.device.api.crypto;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;

public class SafeBag extends PKCS12ContentInfo {
   protected byte[] _bagData;
   protected byte[] _bagAttributes;
   protected Vector _attributes;
   protected boolean _parsed;

   protected SafeBag(byte[] data, byte[] attributes, PKCS12ContentInfo parent) {
      super(null, parent);
      this._bagData = data;
      this._bagAttributes = attributes;
      this._attributes = (Vector)(new Object());
   }

   public static SafeBag getSafeBag(byte[] encoding, PKCS12ContentInfo parent) {
      return getSafeBag((ASN1InputByteArray)(new Object(encoding)), parent);
   }

   public static SafeBag getSafeBag(ASN1InputByteArray stream, PKCS12ContentInfo parent) {
      SafeBag bag = null;

      try {
         int next = stream.peekNextTag();
         if (next != 16) {
            return null;
         }

         stream.readSequence();
         OID bagId = stream.readOID();
         byte[] data = stream.readFieldAsByteArray();
         byte[] attributes = null;
         next = stream.peekNextTag();
         if (next == 17) {
            stream.readSet();
            next = stream.peekNextTag();
            if (next != -1) {
               attributes = stream.readFieldAsByteArray();
            }
         }

         if (bagId.equals(OIDs.getOID(312467713))) {
            bag = new KeyBag(data, attributes, parent);
         } else if (bagId.equals(OIDs.getOID(312467714))) {
            bag = new PKCS8ShroudedKeyBag(data, attributes, parent);
         } else if (bagId.equals(OIDs.getOID(312467715))) {
            bag = new CertificateBag(data, attributes, parent);
         } else if (bagId.equals(OIDs.getOID(312467716))) {
            bag = new CRLBag(data, attributes, parent);
         } else if (bagId.equals(OIDs.getOID(312467717))) {
            bag = new SecretBag(data, attributes, parent);
         } else {
            if (!bagId.equals(OIDs.getOID(314564870))) {
               throw new PKCS12ParsingException();
            }

            bag = new SafeContentsBag(data, attributes, parent);
         }

         return bag;
      } finally {
         throw new PKCS12ParsingException();
      }
   }

   public Enumeration getAttributes() {
      return this._attributes != null ? this._attributes.elements() : null;
   }

   public PKCS12Attribute getAttribute(OID oid) {
      int size = this._attributes.size();

      for (int i = 0; i < size; i++) {
         PKCS12Attribute attribute = (PKCS12Attribute)this._attributes.elementAt(i);
         if (oid.equals(attribute.getOID())) {
            return attribute;
         }
      }

      return null;
   }

   public String getFriendlyName() {
      this.parse();
      String id = null;

      try {
         PKCS12Attribute attribute = this.getAttribute(OIDs.getOID(546842172));
         if (attribute != null) {
            ASN1InputByteArray attr = (ASN1InputByteArray)(new Object(attribute.getValue()));
            if (attr.peekNextTag() != 17) {
               throw new PKCS12ParsingException();
            }

            attr.readSet();
            id = attr.readBMPString();
         }

         return id;
      } finally {
         throw new PKCS12ParsingException();
      }
   }

   public byte[] getLocalKeyId() {
      this.parse();
      byte[] id = null;

      try {
         PKCS12Attribute attribute = this.getAttribute(OIDs.getOID(547104316));
         if (attribute != null) {
            ASN1InputByteArray attr = (ASN1InputByteArray)(new Object(attribute.getValue()));
            if (attr.peekNextTag() != 17) {
               throw new PKCS12ParsingException();
            }

            attr.readSet();
            id = attr.readOctetString();
         }

         return id;
      } finally {
         throw new PKCS12ParsingException();
      }
   }
}
