package net.rim.device.api.crypto.certificate.wtls;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import net.rim.device.api.crypto.certificate.CertificateParsingException;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;

public final class WTLSDistinguishedName implements DistinguishedName, Persistable {
   private Hashtable _attributes;
   private byte[] _encoding;
   private byte[] _serialNumber;
   private int _hashCode;
   private String _commonName;
   private boolean _isCertificateAuthority;
   private byte[] _buffer;
   private int _sectionLength;
   private int _valueOffset;
   private byte[] _identifier;
   private int _identifierOffset;
   private int _identifierEnd;
   private static final OID _commonNameOID = OIDs.getOID(-1253056853);
   private static final int STATE_READ_CHAR = 0;
   private static final int STATE_READ_TERMINATOR = 1;
   private static final String CA_ATTRIBUTE = "T";
   private static final String CA_VALUE = "ca";
   private static final String SN_ATTRIBUTE = "SN";

   public final byte[] getSerialNumber() {
      return this._serialNumber == null ? null : Arrays.copy(this._serialNumber);
   }

   public final boolean isCertificateAuthority() {
      return this._isCertificateAuthority;
   }

   public final String getServiceName() {
      return this.getString(OIDs.getOID(-1252532565));
   }

   @Override
   public final String getSurname() {
      return this.getString(OIDs.getOID(-1252991317));
   }

   @Override
   public final String getLocality() {
      return this.getString(OIDs.getOID(-1252794709));
   }

   @Override
   public final String getStateOrProvince() {
      return this.getString(OIDs.getOID(-1252729173));
   }

   @Override
   public final String getOrganizationalUnit() {
      return this.getServiceName();
   }

   @Override
   public final Enumeration getOIDs() {
      return this._attributes.keys();
   }

   @Override
   public final String getEmailAddress() {
      return this.getString(OIDs.getOID(541861436));
   }

   @Override
   public final String getOrganization() {
      return this.getString(OIDs.getOID(-1252598101));
   }

   @Override
   public final String getCountry() {
      return this.getString(OIDs.getOID(-1252860245));
   }

   @Override
   public final String getCommonName() {
      return this._commonName;
   }

   @Override
   public final String getString(OID oid) {
      if (oid == null) {
         throw new IllegalArgumentException();
      } else {
         return (String)this._attributes.get(oid);
      }
   }

   @Override
   public final byte[] getEncoding() {
      return this._encoding == null ? null : Arrays.copy(this._encoding);
   }

   private final void addAttributeValue(OID attributeType, String attributeValue) {
      this._hashCode = this._hashCode ^ attributeType.hashCode();
      this._hashCode = this._hashCode ^ attributeValue.hashCode();
      String temp = (String)this._attributes.get(attributeType);
      if (temp != null) {
         attributeValue = attributeValue + ',' + temp;
      }

      this._attributes.put(attributeType, attributeValue);
   }

   private final void readNextSection() throws CertificateParsingException {
      int state = 0;
      boolean complete = false;
      int bufferOffset = 0;
      this._valueOffset = -1;

      while (!complete) {
         if (this._identifierOffset == this._identifierEnd) {
            complete = true;
         } else {
            byte data = this._identifier[this._identifierOffset];
            switch (state) {
               case -1:
                  break;
               case 0:
               default:
                  if (data == 59) {
                     state = 1;
                  } else if (data == 61) {
                     this._buffer[bufferOffset++] = data;
                     this._valueOffset = bufferOffset;
                  } else {
                     this._buffer[bufferOffset++] = data;
                  }
                  break;
               case 1:
                  if (data == 59) {
                     this._buffer[bufferOffset++] = 59;
                     state = 0;
                  } else {
                     if (data != 32) {
                        throw new CertificateParsingException();
                     }

                     complete = true;
                  }
            }

            this._identifierOffset++;
         }
      }

      this._sectionLength = bufferOffset;
   }

   public WTLSDistinguishedName(InputStream in) throws CertificateParsingException {
      if (in == null) {
         throw new IllegalArgumentException();
      }

      this._attributes = new Hashtable(5);
      this._serialNumber = null;
      this._hashCode = 0;
      this._isCertificateAuthority = false;
      this._encoding = null;
      switch (in.read()) {
         case 1:
            int characterSet = in.read() << 8 & in.read();
            int length = in.read();
            this._encoding = new byte[4 + length];
            this._encoding[0] = 1;
            this._encoding[1] = (byte)(characterSet >> 8);
            this._encoding[2] = (byte)(characterSet & 0xFF);
            this._encoding[3] = (byte)length;
            in.read(this._encoding, 4, length);
            this.parseUTF8TextIdentifier(this._encoding, 4, length);
            this._commonName = (String)this._attributes.get(_commonNameOID);
            return;
         default:
            throw new CertificateParsingException();
      }
   }

   private final void parseUTF8TextIdentifier(byte[] identifier, int offset, int length) {
      if (identifier != null && offset >= 0 && length >= 0 && identifier.length - length >= offset) {
         this._buffer = new byte[length];
         this._identifier = identifier;
         this._identifierOffset = offset;
         this._identifierEnd = offset + length;
         this.readNextSection();
         if (this._sectionLength > 0) {
            this.addAttributeValue(OIDs.getOID(-1252532565), new String(this._buffer, 0, this._sectionLength));
         }

         this.readNextSection();
         if (this._sectionLength > 0) {
            this.addAttributeValue(OIDs.getOID(-1252598101), new String(this._buffer, 0, this._sectionLength));
         }

         this.readNextSection();
         if (this._sectionLength > 0) {
            this.addAttributeValue(OIDs.getOID(-1252860245), new String(this._buffer, 0, this._sectionLength));
         }

         this.readNextSection();
         if (this._sectionLength > 0) {
            this.addAttributeValue(OIDs.getOID(-1253056853), new String(this._buffer, 0, this._sectionLength));
         }

         this.readNextSection();

         for (; this._sectionLength > 0; this.readNextSection()) {
            OID oid = null;
            String value = null;
            if (this._valueOffset == -1) {
               oid = OIDs.getOID(-1252532565);
               value = new String(this._buffer, 0, this._sectionLength);
            } else {
               String attribute = new String(this._buffer, 0, this._valueOffset - 1);
               value = new String(this._buffer, this._valueOffset, this._sectionLength - this._valueOffset);
               if (StringUtilities.strEqualIgnoreCase(attribute, "T", 1701707776) && StringUtilities.strEqualIgnoreCase(value, "ca", 1701707776)) {
                  this._isCertificateAuthority = true;
                  continue;
               }

               if (StringUtilities.strEqualIgnoreCase(attribute, "SN", 1701707776)) {
                  this._serialNumber = value.getBytes();
                  continue;
               }

               oid = OIDs.getAssociatedOID(-1803797844404304836L, attribute);
            }

            this.addAttributeValue(oid, value);
         }

         this._buffer = null;
      } else {
         throw new IllegalArgumentException();
      }
   }

   @Override
   public final String toString() {
      StringBuffer distinguishedName = new StringBuffer();
      this.writeData(distinguishedName, OIDs.getOID(-1253056853));
      this.writeData(distinguishedName, OIDs.getOID(-1252532565));
      this.writeData(distinguishedName, OIDs.getOID(-1252598101));
      this.writeData(distinguishedName, OIDs.getOID(-1252794709));
      this.writeData(distinguishedName, OIDs.getOID(-1252729173));
      this.writeData(distinguishedName, OIDs.getOID(-1252860245));
      return distinguishedName.toString();
   }

   private final void writeData(StringBuffer distinguishedName, OID oid) {
      String value = (String)this._attributes.get(oid);
      if (value != null) {
         if (distinguishedName.length() > 0) {
            distinguishedName.append(';');
         }

         distinguishedName.append(OIDs.getAssociatedString(-1803797844404304836L, oid));
         distinguishedName.append('=');
         distinguishedName.append(value);
      }
   }

   public WTLSDistinguishedName(byte[] distinguishedName, int offset, int length) {
      this(new ByteArrayInputStream(distinguishedName, offset, length));
   }

   public WTLSDistinguishedName(byte[] distinguishedName) {
      this(distinguishedName, 0, distinguishedName.length);
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      }

      if (!(other instanceof WTLSDistinguishedName)) {
         if (!(other instanceof DistinguishedName)) {
            return false;
         }

         DistinguishedName distinguishedName = (DistinguishedName)other;
         return CertificateUtilities.compareDistinguishedNames(this, distinguishedName);
      } else {
         WTLSDistinguishedName distinguishedName = (WTLSDistinguishedName)other;
         if (this._commonName != null) {
            if (!this._commonName.equals(distinguishedName._commonName)) {
               return false;
            }
         } else if (distinguishedName._commonName != null) {
            return false;
         }

         if (Arrays.equals(distinguishedName._encoding, this._encoding)) {
            return true;
         }

         if (this._attributes.size() != distinguishedName._attributes.size()) {
            return false;
         }

         Enumeration keys = this._attributes.keys();

         while (keys.hasMoreElements()) {
            Object currentKey = keys.nextElement();
            String value1 = (String)this._attributes.get(currentKey);
            String value2 = (String)distinguishedName._attributes.get(currentKey);
            if (!value1.equals(value2)) {
               return false;
            }
         }

         return true;
      }
   }
}
