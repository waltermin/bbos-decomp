package net.rim.device.api.crypto.asn1;

import java.io.InputStream;
import java.util.Calendar;
import java.util.TimeZone;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.io.SharedInputStream;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DateTimeUtilities;
import net.rim.vm.Array;

public class ASN1InputStream {
   private SharedInputStream _sharedStream;
   private ASN1Field _lastFieldRead;
   private ASN1Field _creator;
   private static final int _arraySizeLimit = 8192;
   private static final int _timeSizeLimit = 64;
   private static final int _asn1SizeLimit = 1024;
   private static final int RECURSIVE_LIMIT = 32;

   public ASN1InputStream(InputStream inputStream) {
      if (inputStream == null) {
         throw new Object();
      }

      if (inputStream instanceof Object) {
         this._sharedStream = (SharedInputStream)inputStream;
      } else {
         this._sharedStream = SharedInputStream.getSharedInputStream(inputStream);
      }

      this._lastFieldRead = new ASN1Field(this._sharedStream.getCurrentPosition(), 0);
      this._creator = null;
   }

   ASN1InputStream(SharedInputStream inputStream, ASN1Field creator) {
      if (inputStream == null) {
         throw new Object();
      }

      this._sharedStream = SharedInputStream.getSharedInputStream(inputStream);
      this._lastFieldRead = new ASN1Field(this._sharedStream.getCurrentPosition(), 0);
      this._creator = creator;
   }

   public ASN1InputStream(byte[] data) {
      this((InputStream)(data == null ? null : new Object(data)));
   }

   public ASN1InputStream(byte[] data, int offset, int length) {
      this((InputStream)(data == null ? null : new Object(data, offset, length)));
   }

   public boolean isNextTagUniversal() {
      return (this.peekNextByte() & 192) == 0;
   }

   public boolean isNextTagApplicationSpecific() {
      return (this.peekNextByte() & 192) == 64;
   }

   public boolean isNextTagContextSpecific() {
      return (this.peekNextByte() & 192) == 128;
   }

   public boolean isNextTagPrivate() {
      return (this.peekNextByte() & 192) == 192;
   }

   public int peekNextTag() {
      int tag = this.peekNextByte();
      if (tag != -1) {
         tag &= 31;
      }

      return tag;
   }

   private ASN1Field checkStreamTag(int taggingType, int classFlag, int tag, int baseTag, boolean useDefault) {
      return this.checkStreamTag(taggingType, classFlag, tag, baseTag, useDefault, -1);
   }

   private ASN1Field checkStreamTag(int taggingType, int classFlag, int tag, int baseTag, boolean useDefault, int lengthLimit) {
      tag &= 31;
      classFlag &= 192;
      int explicitTag;
      if (taggingType == 1) {
         explicitTag = tag | classFlag | 32;
      } else {
         explicitTag = -1;
      }

      ASN1Field field = new ASN1Field(this._lastFieldRead, this._sharedStream.readInputStream(), lengthLimit, explicitTag);
      if (taggingType == 1) {
         if (!field.isValidExplicit()) {
            if (useDefault) {
               return null;
            }

            throw new ASN1EncodingException();
         }

         tag = baseTag;
      }

      if (taggingType == 3) {
         tag = baseTag;
      }

      if ((field.getTag() & 31) != tag) {
         if (useDefault) {
            return null;
         } else {
            throw new ASN1EncodingException();
         }
      } else {
         return field;
      }
   }

   public boolean readBoolean() {
      return this.readBoolean(3, 1, 0, false, false);
   }

   public boolean readBoolean(int tagging, int tag) {
      return this.readBoolean(tagging, tag, 128, false, false);
   }

   public boolean readBoolean(int tagging, int tag, boolean defaultValue) {
      return this.readBoolean(tagging, tag, 128, true, defaultValue);
   }

   public boolean readBoolean(int tagging, int tag, int clsFlags) {
      return this.readBoolean(tagging, tag, clsFlags, false, false);
   }

   public boolean readBoolean(int tagging, int tag, int clsFlags, boolean defaultValue) {
      return this.readBoolean(tagging, tag, clsFlags, true, defaultValue);
   }

   private boolean readBoolean(int tagging, int tag, int clsFlags, boolean useDefault, boolean defaultValue) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, 1, useDefault);
      if (field == null) {
         return defaultValue;
      }

      byte[] returnValue = field.getValueAsByteArray();
      if (returnValue.length != 1) {
         throw new ASN1EncodingException();
      }

      this._lastFieldRead = field;
      return returnValue[0] == -1;
   }

   public boolean readNull() {
      return this.readNull(3, 5, 0);
   }

   public boolean readNull(int tagging, int tag) {
      return this.readNull(tagging, tag, 128, false, false);
   }

   public boolean readNull(int tagging, int tag, boolean defaultValue) {
      return this.readNull(tagging, tag, 128, true, defaultValue);
   }

   public boolean readNull(int tagging, int tag, int clsFlags) {
      return this.readNull(tagging, tag, clsFlags, false, false);
   }

   public boolean readNull(int tagging, int tag, int clsFlags, boolean defaultValue) {
      return this.readNull(tagging, tag, clsFlags, true, defaultValue);
   }

   private boolean readNull(int tagging, int tag, int clsFlags, boolean useDefault, boolean defaultValue) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, 5, useDefault);
      if (field == null) {
         return defaultValue;
      }

      if (field.getValueLength() != 0) {
         throw new ASN1EncodingException();
      }

      this._lastFieldRead = field;
      return true;
   }

   public int readInteger() {
      return this.readInteger(3, 2, 0, false, 0, 2);
   }

   public int readInteger(int tagging, int tag) {
      return this.readInteger(tagging, tag, 128, false, -1, 2);
   }

   public int readInteger(int tagging, int tag, int defaultValue) {
      return this.readInteger(tagging, tag, 128, true, defaultValue, 2);
   }

   public int readInteger(int tagging, int tag, int clsFlags, int defaultValue) {
      return this.readInteger(tagging, tag, clsFlags, true, defaultValue, 2);
   }

   private int readInteger(int tagging, int tag, int clsFlags, boolean useDefault, int defaultValue, int baseTag) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, baseTag, useDefault);
      if (field == null) {
         return defaultValue;
      }

      int length = field.getValueLength();
      if (length > 4) {
         throw new ASN1EncodingException();
      }

      this._lastFieldRead = field;
      byte[] valueArray = field.getValueAsByteArray();
      int value = 0;
      if ((valueArray[0] & -128) != 0) {
         value = -1;
      }

      for (int j = 0; j < length; j++) {
         value = value << 8 | valueArray[j] & 255;
      }

      return value;
   }

   public int readEnumerated() {
      return this.readInteger(3, 10, 0, false, 0, 10);
   }

   public int readEnumerated(int tagging, int tag) {
      return this.readInteger(tagging, tag, 128, false, -1, 10);
   }

   public int readEnumerated(int tagging, int tag, int defaultValue) {
      return this.readInteger(tagging, tag, 128, true, defaultValue, 10);
   }

   public int readEnumerated(int tagging, int tag, int clsFlags, int defaultValue) {
      return this.readInteger(tagging, tag, clsFlags, true, defaultValue, 10);
   }

   public byte[] readIntegerAsByteArray() {
      return this.readIntegerAsByteArray(3, 2, 0, false, null);
   }

   public byte[] readIntegerAsByteArray(int tagging, int tag) {
      return this.readIntegerAsByteArray(tagging, tag, 128, false, null);
   }

   public byte[] readIntegerAsByteArray(int tagging, int tag, byte[] defaultValue) {
      return this.readIntegerAsByteArray(tagging, tag, 128, true, defaultValue);
   }

   public byte[] readIntegerAsByteArray(int tagging, int tag, int clsFlags) {
      return this.readIntegerAsByteArray(tagging, tag, clsFlags, false, null);
   }

   public byte[] readIntegerAsByteArray(int tagging, int tag, int clsFlags, byte[] defaultValue) {
      return this.readIntegerAsByteArray(tagging, tag, clsFlags, true, defaultValue);
   }

   private byte[] readIntegerAsByteArray(int tagging, int tag, int clsFlags, boolean useDefault, byte[] defaultValue) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, 2, useDefault, 1024);
      if (field == null) {
         return defaultValue;
      }

      this._lastFieldRead = field;
      byte[] valueArray = field.getValueAsByteArray();
      return valueArray.length > 1 && valueArray[0] == 0 ? Arrays.copy(valueArray, 1, valueArray.length - 1) : valueArray;
   }

   public ASN1SignedByteArray readIntegerAsSignedByteArray() {
      return this.readIntegerAsSignedByteArray(3, 2, 0, null, true, false);
   }

   private ASN1SignedByteArray readIntegerAsSignedByteArray(
      int tagType, int tag, int clsFlags, byte[] defaultValue, boolean defaultIsPositive, boolean useDefault
   ) {
      ASN1Field field = this.checkStreamTag(tagType, clsFlags, tag, 2, useDefault);
      if (field == null) {
         return new ASN1SignedByteArray(defaultValue, defaultIsPositive);
      }

      this._lastFieldRead = field;
      byte[] valueArray = field.getValueAsByteArray();
      boolean isPositive = true;
      if (valueArray.length > 1 && valueArray[0] == 0) {
         valueArray = Arrays.copy(valueArray, 1, valueArray.length - 1);
      } else if (valueArray.length > 0 && (valueArray[0] & 128) != 0) {
         isPositive = false;
      }

      return new ASN1SignedByteArray(valueArray, isPositive);
   }

   public ASN1InputStream readSequence() {
      return this.readSequence(3, 16, 0);
   }

   public ASN1InputStream readSequence(int tagging, int tag) {
      return this.readSequence(tagging, tag, 128);
   }

   public ASN1InputStream readSequence(int tagging, int tag, int clsFlags) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, 16, false);
      this._lastFieldRead = field;
      return new ASN1InputStream((SharedInputStream)field.getInputStream(), field);
   }

   public ASN1InputStream readSet() {
      return this.readSet(3, 17, 0);
   }

   public ASN1InputStream readSet(int tagging, int tag) {
      return this.readSet(tagging, tag, 128);
   }

   public ASN1InputStream readSet(int tagging, int tag, int clsFlags) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, 17, false);
      this._lastFieldRead = field;
      return new ASN1InputStream((SharedInputStream)field.getInputStream(), field);
   }

   public String readPrintableString() {
      return this.readPrintableString(3, 19, null);
   }

   public String readPrintableString(int tagging, int tag) {
      return this.readPrintableString(tagging, tag, 128, false, null);
   }

   public String readPrintableString(int tagging, int tag, String defaultValue) {
      return this.readPrintableString(tagging, tag, 128, true, defaultValue);
   }

   public String readPrintableString(int tagging, int tag, int clsFlags) {
      return this.readPrintableString(tagging, tag, clsFlags, false, null);
   }

   public String readPrintableString(int tagging, int tag, int clsFlags, String defaultValue) {
      return this.readPrintableString(tagging, tag, clsFlags, true, defaultValue);
   }

   private String readPrintableString(int tagging, int tag, int clsFlags, boolean useDefault, String defaultValue) {
      return this.readString(tagging, tag, clsFlags, useDefault, defaultValue, 19, 0);
   }

   public String readIA5String() {
      return this.readIA5String(3, 22, null);
   }

   public String readIA5String(int tagging, int tag) {
      return this.readIA5String(tagging, tag, 128, false, null);
   }

   public String readIA5String(int tagging, int tag, String defaultValue) {
      return this.readIA5String(tagging, tag, 128, true, defaultValue);
   }

   public String readIA5String(int tagging, int tag, int clsFlags) {
      return this.readIA5String(tagging, tag, clsFlags, false, null);
   }

   public String readIA5String(int tagging, int tag, int clsFlags, String defaultValue) {
      return this.readIA5String(tagging, tag, clsFlags, true, defaultValue);
   }

   private String readIA5String(int tagging, int tag, int clsFlags, boolean useDefault, String defaultValue) {
      return this.readString(tagging, tag, clsFlags, useDefault, defaultValue, 22, 0);
   }

   public String readT61String() {
      return this.readT61String(3, 20, null);
   }

   public String readT61String(int tagging, int tag) {
      return this.readT61String(tagging, tag, 128, false, null);
   }

   public String readT61String(int tagging, int tag, String defaultValue) {
      return this.readT61String(tagging, tag, 128, true, defaultValue);
   }

   public String readT61String(int tagging, int tag, int clsFlags) {
      return this.readT61String(tagging, tag, clsFlags, false, null);
   }

   public String readT61String(int tagging, int tag, int clsFlags, String defaultValue) {
      return this.readT61String(tagging, tag, clsFlags, true, defaultValue);
   }

   private String readT61String(int tagging, int tag, int clsFlags, boolean useDefault, String defaultValue) {
      return this.readString(tagging, tag, clsFlags, useDefault, defaultValue, 20, 0);
   }

   public String readUTF8String() {
      return this.readUTF8String(3, 12, null);
   }

   public String readUTF8String(int tagging, int tag) {
      return this.readUTF8String(tagging, tag, 128, false, null);
   }

   public String readUTF8String(int tagging, int tag, String defaultValue) {
      return this.readUTF8String(tagging, tag, 128, true, defaultValue);
   }

   public String readUTF8String(int tagging, int tag, int clsFlags) {
      return this.readUTF8String(tagging, tag, clsFlags, false, null);
   }

   public String readUTF8String(int tagging, int tag, int clsFlags, String defaultValue) {
      return this.readUTF8String(tagging, tag, clsFlags, true, defaultValue);
   }

   private String readUTF8String(int tagging, int tag, int clsFlags, boolean useDefault, String defaultValue) {
      return this.readString(tagging, tag, clsFlags, useDefault, defaultValue, 12, 0);
   }

   public String readBMPString() {
      return this.readBMPString(3, 30, null);
   }

   public String readBMPString(int tagging, int tag) {
      return this.readBMPString(tagging, tag, 128, false, null);
   }

   public String readBMPString(int tagging, int tag, String defaultValue) {
      return this.readBMPString(tagging, tag, 128, true, defaultValue);
   }

   public String readBMPString(int tagging, int tag, int clsFlags) {
      return this.readBMPString(tagging, tag, clsFlags, false, null);
   }

   public String readBMPString(int tagging, int tag, int clsFlags, String defaultValue) {
      return this.readBMPString(tagging, tag, clsFlags, true, defaultValue);
   }

   private String readBMPString(int tagging, int tag, int clsFlags, boolean useDefault, String defaultValue) {
      return this.readString(tagging, tag, clsFlags, useDefault, defaultValue, 30, 0);
   }

   private String readString(int tagging, int tag, int clsFlags, boolean useDefault, String defaultValue, int baseTag, int recursionLevel) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, baseTag, useDefault);
      if (field == null) {
         return defaultValue;
      }

      this._lastFieldRead = field;
      byte[] value = field.getValueAsByteArray();
      if ((field.getTag() & 32) != 0) {
         if (recursionLevel >= 32) {
            throw new ASN1EncodingException();
         }

         String returnValue = "";
         ASN1InputStream recurse = new ASN1InputStream(value);

         while (!recurse.endOfStream()) {
            returnValue = ((StringBuffer)(new Object()))
               .append(returnValue)
               .append(recurse.readString(3, baseTag, 0, useDefault, defaultValue, baseTag, recursionLevel + 1))
               .toString();
         }

         return returnValue;
      } else {
         switch (baseTag) {
            case 12:
               return (String)(new Object(value, "UTF8"));
            case 30:
               char[] data = new char[value.length >> 1];

               for (int i = 0; i < data.length; i++) {
                  data[i] = (char)(value[2 * i] << 8 | value[2 * i + 1]);
               }

               return (String)(new Object(data));
            default:
               return (String)(new Object(value));
         }
      }
   }

   public InputStream readOctetString() {
      return this.readOctetString(3, 4, 0, false, null);
   }

   public InputStream readOctetString(int tagging, int tag) {
      return this.readOctetString(tagging, tag, 128, false, null);
   }

   public InputStream readOctetString(int tagging, int tag, byte[] defaultValue) {
      return this.readOctetString(tagging, tag, 128, true, defaultValue);
   }

   public InputStream readOctetString(int tagging, int tag, int clsFlags) {
      return this.readOctetString(tagging, tag, clsFlags, false, null);
   }

   public InputStream readOctetString(int tagging, int tag, int clsFlags, byte[] defaultValue) {
      return this.readOctetString(tagging, tag, clsFlags, true, defaultValue);
   }

   private InputStream readOctetString(int tagging, int tag, int clsFlags, boolean useDefault, byte[] defaultValue) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, 4, useDefault);
      if (field == null) {
         return (InputStream)(new Object(defaultValue));
      }

      this._lastFieldRead = field;
      SharedInputStream fieldInputStream = (SharedInputStream)field.getInputStream();
      if ((field.getTag() & 32) != 0) {
         return new ConstructedOctetStringInputStream(fieldInputStream);
      }

      SharedInputStream octetStringInputStream = (SharedInputStream)(new Object(fieldInputStream));
      octetStringInputStream.setLength(field.getValueLength());
      return octetStringInputStream;
   }

   public byte[] readOctetStringAsByteArray() {
      return this.readOctetStringAsByteArray(3, 4, 0, false, null);
   }

   public byte[] readOctetStringAsByteArray(int tagging, int tag) {
      return this.readOctetStringAsByteArray(tagging, tag, 128, false, null);
   }

   public byte[] readOctetStringAsByteArray(int tagging, int tag, byte[] defaultValue) {
      return this.readOctetStringAsByteArray(tagging, tag, 128, true, defaultValue);
   }

   public byte[] readOctetStringAsByteArray(int tagging, int tag, int clsFlags) {
      return this.readOctetStringAsByteArray(tagging, tag, clsFlags, false, null);
   }

   public byte[] readOctetStringAsByteArray(int tagging, int tag, int clsFlags, byte[] defaultValue) {
      return this.readOctetStringAsByteArray(tagging, tag, clsFlags, true, defaultValue);
   }

   private byte[] readOctetStringAsByteArray(int tagging, int tag, int clsFlags, boolean useDefault, byte[] defaultValue) {
      InputStream stream = this.readOctetString(tagging, tag, clsFlags, useDefault, defaultValue);
      if (stream == null) {
         return null;
      }

      byte[] octet = new byte[100];
      int offset = 0;

      while (true) {
         int len = stream.read(octet, offset, 100);
         if (len == -1) {
            Array.resize(octet, offset);
            return octet;
         }

         offset += len;
         Array.resize(octet, offset + 100);
      }
   }

   public ASN1BitSet readBitString() {
      return this.readBitString(3, 3, 0, false, null);
   }

   public ASN1BitSet readBitString(int tagging, int tag) {
      return this.readBitString(tagging, tag, 128, false, null);
   }

   public ASN1BitSet readBitString(int tagging, int tag, byte[] defaultValue) {
      return this.readBitString(tagging, tag, 128, true, defaultValue);
   }

   public ASN1BitSet readBitString(int tagging, int tag, int clsFlags) {
      return this.readBitString(tagging, tag, clsFlags, false, null);
   }

   public ASN1BitSet readBitString(int tagging, int tag, int clsFlags, byte[] defaultValue) {
      return this.readBitString(tagging, tag, clsFlags, true, defaultValue);
   }

   private ASN1BitSet readBitString(int tagging, int tag, int clsFlags, boolean useDefault, byte[] defaultValue) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, 3, useDefault);
      if (field == null) {
         return defaultValue == null ? null : new ASN1BitSet(defaultValue, defaultValue.length << 3);
      }

      this._lastFieldRead = field;
      return (field.getTag() & 32) != 0 ? this.recurseReadBitString((SharedInputStream)field.getInputStream(), 0) : this.readPrimitiveBitString(field);
   }

   private ASN1BitSet recurseReadBitString(SharedInputStream stream, int recursionLevel) {
      if (recursionLevel >= 32) {
         throw new ASN1EncodingException();
      }

      ASN1BitSet bitSet = new ASN1BitSet(new byte[0], 0);

      while (stream.available() > 0 || stream.peek() != -1) {
         ASN1Field field = new ASN1Field(stream, stream.getCurrentPosition(), -1);
         if (field.getTag() == 0) {
            return bitSet;
         }

         if ((field.getTag() & 32) != 0) {
            bitSet = ASN1BitSet.append(bitSet, this.recurseReadBitString(field.getUnderlyingStream(), recursionLevel + 1));
         } else {
            bitSet = ASN1BitSet.append(bitSet, this.readPrimitiveBitString(field));
         }
      }

      return bitSet;
   }

   private ASN1BitSet readPrimitiveBitString(ASN1Field field) {
      byte[] data = field.getValueAsByteArray();
      int length = data.length - 1;
      byte[] realData = new byte[length];
      System.arraycopy(data, 1, realData, 0, length);
      return new ASN1BitSet(realData, (length << 3) - data[0]);
   }

   public OID readOID() {
      return this.readOID(3, 6, 0, false, null);
   }

   public OID readOID(int tagging, int tag) {
      return this.readOID(tagging, tag, 128, false, null);
   }

   public OID readOID(int tagging, int tag, OID defaultValue) {
      return this.readOID(tagging, tag, 128, true, defaultValue);
   }

   public OID readOID(int tagging, int tag, int clsFlags) {
      return this.readOID(tagging, tag, clsFlags, false, null);
   }

   public OID readOID(int tagging, int tag, int clsFlags, OID defaultValue) {
      return this.readOID(tagging, tag, clsFlags, true, defaultValue);
   }

   private OID readOID(int tagging, int tag, int clsFlags, boolean useDefault, OID defaultValue) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, 6, useDefault);
      if (field == null) {
         return defaultValue;
      }

      this._lastFieldRead = field;
      byte[] derEncoding = field.getValueAsByteArray();
      return OIDs.internOID(derEncoding, 0, derEncoding.length);
   }

   public long readGeneralizedTime() {
      return this.readGeneralizedTime(3, 24, 0, false, 0);
   }

   public long readGeneralizedTime(int tagging, int tag) {
      return this.readGeneralizedTime(tagging, tag, 128, false, 0);
   }

   public long readGeneralizedTime(int tagging, int tag, long defaultValue) {
      return this.readGeneralizedTime(tagging, tag, 128, true, defaultValue);
   }

   public long readGeneralizedTime(int tagging, int tag, int clsFlags) {
      return this.readGeneralizedTime(tagging, tag, clsFlags, false, 0);
   }

   public long readGeneralizedTime(int tagging, int tag, int clsFlags, long defaultValue) {
      return this.readGeneralizedTime(tagging, tag, clsFlags, true, defaultValue);
   }

   private long readGeneralizedTime(int tagging, int tag, int clsFlags, boolean useDefault, long defaultValue) {
      return this.readTime(tagging, tag, clsFlags, useDefault, defaultValue, 24);
   }

   public long readUTCTime() {
      return this.readUTCTime(3, 23, 0, false, 0);
   }

   public long readUTCTime(int tagging, int tag) {
      return this.readUTCTime(tagging, tag, 128, false, 0);
   }

   public long readUTCTime(int tagging, int tag, long defaultValue) {
      return this.readUTCTime(tagging, tag, 128, true, defaultValue);
   }

   public long readUTCTime(int tagging, int tag, int clsFlags) {
      return this.readUTCTime(tagging, tag, clsFlags, false, 0);
   }

   public long readUTCTime(int tagging, int tag, int clsFlags, long defaultValue) {
      return this.readUTCTime(tagging, tag, clsFlags, true, defaultValue);
   }

   private long readUTCTime(int tagging, int tag, int clsFlags, boolean useDefault, long defaultValue) {
      return this.readTime(tagging, tag, clsFlags, useDefault, defaultValue, 23);
   }

   private long readTime(int tagging, int tag, int clsFlags, boolean useDefault, long defaultValue, int baseTag) {
      ASN1Field field = this.checkStreamTag(tagging, clsFlags, tag, baseTag, useDefault);
      if (field == null) {
         return defaultValue;
      }

      if ((tag & 32) != 0) {
         if (useDefault) {
            return defaultValue;
         } else {
            throw new ASN1EncodingException();
         }
      } else {
         this._lastFieldRead = field;
         byte[] value = field.getValueAsByteArray();
         return parseTime(value, (baseTag & 31) == 23);
      }
   }

   static long parseTime(byte[] time, boolean utcTime) {
      if (time.length < 11) {
         throw new ASN1EncodingException();
      }

      for (int i = 0; i < time.length; i++) {
         if (time[i] >= 48 && time[i] <= 57) {
            time[i] = (byte)(time[i] - 48);
         }
      }

      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
      int var7 = 0;
      int value;
      if (utcTime) {
         value = 10 * time[var7] + time[var7 + 1];
         var7 += 2;
         value += value < 50 ? 2000 : 1900;
      } else {
         value = 1000 * time[var7] + 100 * time[var7 + 1] + 10 * time[var7 + 2] + time[var7 + 3];
         var7 += 4;
      }

      calendar.set(1, value);
      value = 10 * time[var7] + time[var7 + 1];
      var7 += 2;
      if (value <= 12 && value > 0) {
         int[] monthTranslation = new int[]{
            0,
            1,
            2,
            3,
            4,
            5,
            6,
            7,
            8,
            9,
            10,
            11,
            -805044213,
            775162112,
            774909491,
            3420721,
            -805044199,
            1699878656,
            1918985587,
            1226860643,
            1867325550,
            1852795252,
            1685343264,
            46,
            -805044220,
            843596800,
            -805044220,
            877151232,
            -804913145,
            2818092,
            6029346,
            4063292,
            59,
            -805043774,
            -1107197392,
            654410288,
            16909216,
            16843266,
            151391536,
            -2042067414,
            16846327,
            805307653,
            806564117,
            1426261521,
            169018116,
            1953654083,
            1836016489,
            506479136
         };
         calendar.set(2, monthTranslation[value - 1]);
         value = 10 * time[var7] + time[var7 + 1];
         var7 += 2;
         if (value <= 31 && value > 0) {
            calendar.set(5, value);
            value = 10 * time[var7] + time[var7 + 1];
            var7 += 2;
            if (value >= 24) {
               throw new ASN1EncodingException();
            }

            calendar.set(11, value);
            value = 10 * time[var7] + time[var7 + 1];
            var7 += 2;
            if (value >= 60) {
               throw new ASN1EncodingException();
            }

            calendar.set(12, value);
            if (time[var7] != 90 && time[var7] != 43 && time[var7] != 45 && var7 <= time.length - 2) {
               value = 10 * time[var7] + time[var7 + 1];
               var7 += 2;
               if (value >= 60) {
                  throw new ASN1EncodingException();
               }

               calendar.set(13, value);
            } else {
               calendar.set(13, 0);
            }

            calendar.set(14, 0);
            if (time[var7] == 43) {
               value = 10 * time[var7 + 1] + time[var7 + 2];
               var7 += 3;
               if (value >= 24) {
                  throw new ASN1EncodingException();
               }

               calendar.set(10, (calendar.get(10) - value) % 24);
               value = 10 * time[var7] + time[var7 + 1];
               if (value >= 60) {
                  throw new ASN1EncodingException();
               }

               calendar.set(12, (calendar.get(12) - value) % 60);
            } else if (time[var7] == 45) {
               value = 10 * time[var7 + 1] + time[var7 + 2];
               var7 += 3;
               if (value >= 24) {
                  throw new ASN1EncodingException();
               }

               calendar.set(10, (calendar.get(10) + value) % 24);
               value = 10 * time[var7] + time[var7 + 1];
               if (value >= 60) {
                  throw new ASN1EncodingException();
               }

               calendar.set(12, (calendar.get(12) + value) % 60);
            } else if (time[var7] != 90) {
               throw new ASN1EncodingException();
            }

            return calendar.getTime().getTime();
         } else {
            throw new ASN1EncodingException();
         }
      } else {
         throw new ASN1EncodingException();
      }
   }

   public boolean endOfStream() {
      if (this._creator != null) {
         return this._lastFieldRead.getEndPosition() >= this._creator.getValueEndPosition();
      }

      int currentPosition = this._sharedStream.getCurrentPosition();
      this._sharedStream.setCurrentPosition(this._lastFieldRead.getEndPosition());
      int data = this._sharedStream.read();
      if (data == 0) {
         data = this._sharedStream.read();
         if (data == 0) {
            data = -1;
         }
      }

      this._sharedStream.setCurrentPosition(currentPosition);
      return data == -1;
   }

   private int peekNextByte() {
      try {
         if (this.endOfStream()) {
            return -1;
         }

         this._sharedStream.setCurrentPosition(this._lastFieldRead.getEndPosition());
         return this._sharedStream.peek();
      } catch (ASN1EncodingException e) {
         throw new Object();
      }
   }

   public byte[] readFieldAsByteArray() {
      ASN1Field field = new ASN1Field(this._lastFieldRead, this._sharedStream.readInputStream());
      this._lastFieldRead = field;
      return field.getFieldAsByteArray();
   }

   public void skipField() {
      ASN1Field field = new ASN1Field(this._lastFieldRead, this._sharedStream.readInputStream());
      this._lastFieldRead = field;
   }

   public void skipField(int tag) {
      tag &= 31;
      ASN1Field field = new ASN1Field(this._lastFieldRead, this._sharedStream.readInputStream());
      if (tag != (field.getTag() & 31)) {
         throw new ASN1EncodingException();
      }

      this._lastFieldRead = field;
   }

   public InputStream readStreamWithTag(int tag) {
      tag &= 31;
      ASN1Field field = new ASN1Field(this._lastFieldRead, this._sharedStream.readInputStream());
      if (tag != (field.getTag() & 31)) {
         throw new ASN1EncodingException();
      }

      this._lastFieldRead = field;
      return field.getInputStream();
   }
}
