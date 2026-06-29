package net.rim.device.api.crypto.certificate.x509;

import java.util.Enumeration;
import net.rim.device.api.crypto.CryptoUtilities;
import net.rim.device.api.crypto.asn1.ASN1EncodingException;
import net.rim.device.api.crypto.asn1.ASN1InputByteArray;
import net.rim.device.api.crypto.asn1.ASN1InputStream;
import net.rim.device.api.crypto.asn1.ASN1OutputStream;
import net.rim.device.api.crypto.certificate.CertificateUtilities;
import net.rim.device.api.crypto.certificate.DistinguishedName;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MultiMap;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;

public final class X509DistinguishedName implements DistinguishedName, Persistable {
   private MultiMap _attributes;
   private String _commonName;
   private byte[] _derEncoding;
   private int _hashCode;
   private static final int INITIAL_NUM_TYPES = 6;
   private static final int INITIAL_NUM_VALUES_PER_TYPE = 2;
   private static final OID[] RFC2253_OIDS = new Object[]{
      OIDs.getOID(-1250238803),
      OIDs.getOID(-1253056853),
      OIDs.getOID(-1252532565),
      OIDs.getOID(-1252598101),
      OIDs.getOID(-1252663637),
      OIDs.getOID(-1252794709),
      OIDs.getOID(-1252729173),
      OIDs.getOID(-1250238804),
      OIDs.getOID(-1252860245)
   };
   private static final char[] RFC2253_ESCAPE_CHARACTERS = new char[]{',', '+', '"', '\\', '<', '>', ';', '\u0000', 'ǂ', '퀄', '舰', '븁', '舰', '✁'};
   public static final int STRING_FORMAT_DEFAULT = 0;
   public static final int STRING_FORMAT_RFC2253 = 1;

   public final String toString(int stringFormat) {
      StringBuffer outputBuffer = (StringBuffer)(new Object());
      int numRFC2253OIDs = RFC2253_OIDS.length;

      for (int i = 0; i < numRFC2253OIDs; i++) {
         this.writeAttribute(outputBuffer, RFC2253_OIDS[i], stringFormat, false);
      }

      boolean outputDottedDecimal = stringFormat == 1;
      Enumeration attributeTypes = this._attributes.keys();

      while (attributeTypes.hasMoreElements()) {
         OID currentOID = (OID)attributeTypes.nextElement();
         if (!Arrays.contains(RFC2253_OIDS, currentOID)) {
            this.writeAttribute(outputBuffer, currentOID, stringFormat, outputDottedDecimal);
         }
      }

      return outputBuffer.toString();
   }

   public final String toRFC2253CompatibleString() {
      return this.toString(1);
   }

   public final String[] getValues(OID oid) {
      String[] values = new Object[0];
      Enumeration attributeValues = this._attributes.elements(oid);

      while (attributeValues.hasMoreElements()) {
         Arrays.add(values, attributeValues.nextElement());
      }

      return values;
   }

   @Override
   public final String getCommonName() {
      return this._commonName;
   }

   @Override
   public final String getEmailAddress() {
      return this.getString(541861436);
   }

   @Override
   public final String getSurname() {
      return this.getString(-1252991317);
   }

   @Override
   public final String getCountry() {
      return this.getString(-1252860245);
   }

   @Override
   public final String getLocality() {
      return this.getString(-1252794709);
   }

   @Override
   public final String getStateOrProvince() {
      return this.getString(-1252729173);
   }

   @Override
   public final String getOrganization() {
      return this.getString(-1252598101);
   }

   @Override
   public final String getOrganizationalUnit() {
      return this.getString(-1252532565);
   }

   @Override
   public final Enumeration getOIDs() {
      return this._attributes.keys();
   }

   @Override
   public final String getString(OID oid) {
      StringBuffer attributeValueString = (StringBuffer)(new Object());
      Enumeration attributeValues = this._attributes.elements(oid);

      while (attributeValues.hasMoreElements()) {
         String currentValue = (String)attributeValues.nextElement();
         if (attributeValueString.length() > 0) {
            attributeValueString.append(',');
         }

         attributeValueString.append(currentValue);
      }

      return attributeValueString.length() > 0 ? attributeValueString.toString() : null;
   }

   @Override
   public final byte[] getEncoding() {
      return CryptoUtilities.getImmutableOrCopiedByteArray(this._derEncoding);
   }

   private final String getString(int oid) {
      return this.getString(OIDs.getOID(oid));
   }

   private final int extractAttributeValue(String inputString, char[] delimiters, int attributeValueStart, StringBuffer attributeValue) {
      boolean escaped = false;
      boolean inQuotations = false;
      int inputStringLength = inputString.length();

      for (int i = attributeValueStart; i < inputStringLength; i++) {
         char currentChar = inputString.charAt(i);
         if (escaped) {
            attributeValue.append(currentChar);
            escaped = false;
         } else if (currentChar == '\\') {
            escaped = true;
         } else if (currentChar == '"') {
            inQuotations = !inQuotations;
         } else {
            if (Arrays.getIndex(delimiters, currentChar) >= 0 && !inQuotations) {
               return i;
            }

            attributeValue.append(currentChar);
         }
      }

      return inputStringLength;
   }

   private final byte[] hexStringToByteArray(String value) {
      if (value.charAt(0) != '#') {
         throw new ASN1EncodingException();
      }

      byte[] data = new byte[value.length() - 1 >> 1];

      for (int i = 0; i < data.length; i++) {
         data[i] = (byte)this.toHex(value.charAt(2 * i + 1), value.charAt(2 * i + 2));
      }

      return data;
   }

   private final int toHex(int a, int b) {
      if (a >= 48 && a <= 57) {
         a -= 48;
      } else if (a >= 97 && a <= 102) {
         a = a - 97 + 10;
      } else {
         if (a < 65 || a > 70) {
            throw new ASN1EncodingException();
         }

         a = a - 65 + 10;
      }

      if (b >= 48 && b <= 57) {
         b -= 48;
      } else if (b >= 97 && b <= 102) {
         b = b - 97 + 10;
      } else {
         if (b < 65 || b > 70) {
            throw new ASN1EncodingException();
         }

         b = b - 65 + 10;
      }

      return a << 4 & 240 | b & 15;
   }

   public X509DistinguishedName(ASN1InputStream input) {
      this(input != null ? input.readFieldAsByteArray() : null);
   }

   public X509DistinguishedName(byte[] encoding) {
      this(encoding != null ? encoding : null, 0, encoding != null ? encoding.length : 0);
   }

   public X509DistinguishedName(byte[] encoding, int offset, int length) {
      if (encoding != null && encoding.length - length >= offset && offset >= 0 && length >= 0) {
         this._attributes = (MultiMap)(new Object(6, 2));
         this._derEncoding = Arrays.copy(encoding, offset, length);
         ASN1InputByteArray context = new ASN1InputByteArray(this._derEncoding);
         context.readSequence();
         this._hashCode = 0;
         int endOffset = context.getEndPosition();

         while (context.getStartPosition() < endOffset) {
            context.readSet();
            int setEndOffset = context.getEndPosition();

            while (context.getStartPosition() < setEndOffset) {
               context.readSequence();
               OID attributeType = context.readOID();
               String attributeValue = null;
               switch (context.peekNextTag()) {
                  case 12:
                     attributeValue = context.readUTF8String();
                     break;
                  case 19:
                     attributeValue = context.readPrintableString();
                     break;
                  case 20:
                     attributeValue = context.readT61String();
                     break;
                  case 22:
                     attributeValue = context.readIA5String();
                     break;
                  case 30:
                     attributeValue = context.readBMPString();
                     break;
                  default:
                     attributeValue = (String)(new Object(context.readFieldAsByteArray()));
               }

               this.addAttributeValue(attributeType, attributeValue);
            }
         }

         this._commonName = this.getString(-1253056853);
      } else {
         throw new Object();
      }
   }

   private final void addAttributeValue(OID attributeType, String attributeValue) {
      this._hashCode = this._hashCode ^ attributeType.hashCode();
      this._hashCode = this._hashCode ^ attributeValue.hashCode();
      this._attributes.add(attributeType, attributeValue);
   }

   private final int extractAttributeType(String inputString, int attributeTypeStart, StringBuffer attributeType) {
      int inputStringLength = inputString.length();

      for (int i = attributeTypeStart; i < inputStringLength; i++) {
         char currentChar = inputString.charAt(i);
         if (currentChar == '=') {
            return i;
         }

         attributeType.append(currentChar);
      }

      return inputStringLength;
   }

   @Override
   public final boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof X509DistinguishedName)) {
         return obj instanceof Object ? CertificateUtilities.compareDistinguishedNames(this, (DistinguishedName)obj) : false;
      } else {
         X509DistinguishedName distinguishedName = (X509DistinguishedName)obj;
         if (!StringUtilities.strEqual(this._commonName, distinguishedName._commonName)) {
            return false;
         } else {
            return Arrays.equals(this._derEncoding, distinguishedName._derEncoding)
               ? true
               : CertificateUtilities.compareDistinguishedNames(this, distinguishedName);
         }
      }
   }

   @Override
   public final String toString() {
      return this.toString(0);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public X509DistinguishedName(String input, char delimiter) {
      if (input == null) {
         throw new Object();
      }

      try {
         this._attributes = (MultiMap)(new Object(6, 2));
         this._hashCode = 0;
         ASN1OutputStream outerSequence = new ASN1OutputStream();
         char[] delimiters = new char[]{delimiter, '+'};
         int inputStringLength = input.length();
         int currentPos = 0;

         while (currentPos < inputStringLength) {
            StringBuffer attributeType = (StringBuffer)(new Object());
            StringBuffer attributeValue = (StringBuffer)(new Object());
            currentPos = this.extractAttributeType(input, currentPos, attributeType);
            currentPos = this.extractAttributeValue(input, delimiters, ++currentPos, attributeValue);
            currentPos++;
            String typeString = attributeType.toString().trim();
            OID typeOID = OIDs.getAssociatedOID(-1803797844404304836L, typeString);
            if (typeOID == null) {
               if (!typeString.startsWith("OID.") && !typeString.startsWith("oid.")) {
                  if (typeString.indexOf(46) == -1) {
                     continue;
                  }

                  typeOID = (OID)(new Object(typeString));
               } else {
                  typeOID = (OID)(new Object(typeString.substring(4)));
               }
            }

            String valueString = attributeValue.toString().trim();
            this.addAttributeValue(typeOID, valueString);
            ASN1OutputStream innerSet = new ASN1OutputStream();
            ASN1OutputStream innerSequence = new ASN1OutputStream();
            innerSequence.writeOID(typeOID);
            if (valueString.charAt(0) == '#') {
               innerSequence.writeRawByteArray(this.hexStringToByteArray(valueString));
            } else {
               innerSequence.writeUTF8String(valueString);
            }

            innerSet.writeSequence(innerSequence);
            outerSequence.writeSet(innerSet);
         }

         this._commonName = this.getString(-1253056853);
         ASN1OutputStream finish = new ASN1OutputStream();
         finish.writeSequence(outerSequence);
         this._derEncoding = finish.toByteArray();
      } catch (Throwable var15) {
         throw new Object(e.toString());
      }
   }

   public X509DistinguishedName(String input) {
      this(input, ',');
   }

   private final void writeAttribute(StringBuffer outputBuffer, OID oid, int stringFormat, boolean outputDottedDecimal) {
      char delimiter;
      boolean escapeCharacters;
      switch (stringFormat) {
         case -1:
            throw new Object();
         case 0:
            delimiter = ';';
            escapeCharacters = false;
            break;
         case 1:
         default:
            delimiter = ',';
            escapeCharacters = true;
      }

      String attributeTypeString;
      if (outputDottedDecimal) {
         attributeTypeString = oid.toString();
      } else {
         attributeTypeString = OIDs.getAssociatedString(-1803797844404304836L, oid);
         if (attributeTypeString == null) {
            return;
         }
      }

      Enumeration attributeValues = this._attributes.elements(oid);

      while (attributeValues.hasMoreElements()) {
         String currentValue = (String)attributeValues.nextElement();
         String attributeValueString;
         if (!escapeCharacters) {
            attributeValueString = currentValue;
         } else {
            StringBuffer escapedValueStringBuffer = (StringBuffer)(new Object());
            int currentValueLength = currentValue.length();

            for (int i = 0; i < currentValueLength; i++) {
               char currentChar = currentValue.charAt(i);
               if (Arrays.getIndex(RFC2253_ESCAPE_CHARACTERS, currentChar) >= 0) {
                  escapedValueStringBuffer.append('\\');
               }

               escapedValueStringBuffer.append(currentChar);
            }

            attributeValueString = escapedValueStringBuffer.toString();
         }

         if (outputBuffer.length() > 0) {
            outputBuffer.append(delimiter);
         }

         outputBuffer.append(attributeTypeString);
         outputBuffer.append('=');
         outputBuffer.append(attributeValueString);
      }
   }

   @Override
   public final int hashCode() {
      return this._hashCode;
   }
}
