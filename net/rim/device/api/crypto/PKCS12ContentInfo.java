package net.rim.device.api.crypto;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;

public class PKCS12ContentInfo {
   protected boolean _parsed;
   protected byte[] _buffer;
   protected PKCS12ContentInfo _parent;
   protected Vector _passwords;

   protected PKCS12ContentInfo(byte[] data, PKCS12ContentInfo parent) {
      this._buffer = data;
      this._parent = parent;
      this._parsed = false;
   }

   public static PKCS12ContentInfo getInstance(byte[] encoding, PKCS12ContentInfo parent) {
      return getInstance((ASN1InputByteArray)(new Object(encoding)), parent);
   }

   public static PKCS12ContentInfo getInstance(ASN1InputByteArray stream, PKCS12ContentInfo parent) {
      try {
         stream.readSequence();
         OID childOID = stream.readOID();
         if (childOID.equals(OIDs.getOID(541859388))) {
            return new PKCS12Data(stream.readOctetString(1, 0), parent);
         } else if (childOID.equals(OIDs.getOID(543170108))) {
            return new PKCS12EncryptedData(stream.readFieldAsByteArray(), parent);
         } else if (childOID.equals(OIDs.getOID(542383676))) {
            stream.readSequence(1, 0);
            return new PKCS12EnvelopedData(stream.readFieldAsByteArray(), parent);
         } else {
            throw new PKCS12ParsingException();
         }
      } finally {
         throw new PKCS12ParsingException();
      }
   }

   public byte[] getEncoding() {
      return this._buffer;
   }

   protected void addPassword(byte[] password) {
      if (this._parent != null) {
         this._parent.addPassword(password);
      } else {
         if (this._passwords == null) {
            this._passwords = (Vector)(new Object());
         }

         int size = this._passwords.size();
         boolean found = false;

         for (int i = 0; i < size; i++) {
            if (Arrays.equals(password, (byte[])this._passwords.elementAt(i))) {
               found = true;
               break;
            }
         }

         if (!found) {
            this._passwords.addElement(password);
         }
      }
   }

   protected Enumeration getPasswords() {
      if (this._parent != null) {
         return this._parent.getPasswords();
      } else {
         return this._passwords == null ? null : this._passwords.elements();
      }
   }

   protected void parse() {
      throw null;
   }

   public PKCS12ContentInfo[] getChildrenContentInfos() {
      throw null;
   }
}
