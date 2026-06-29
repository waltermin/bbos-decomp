package net.rim.device.api.crypto.asn1;

import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.crypto.oid.OIDs;
import net.rim.device.api.util.Arrays;
import net.rim.vm.Array;

public final class ASN1InputByteArray {
   private byte[] _buffer;
   private int _startPosition;
   private int _endPosition;
   private static final int RECURSIVE_LIMIT = 32;

   public ASN1InputByteArray(byte[] buffer) {
      this(buffer, 0);
   }

   public ASN1InputByteArray(byte[] buffer, int offset) {
      if (buffer != null && offset >= 0 && offset <= buffer.length) {
         this._buffer = buffer;
         this._startPosition = offset;
         this._endPosition = offset;
      } else {
         throw new Object();
      }
   }

   public final byte[] getBuffer() {
      return this._buffer;
   }

   public final int getStartPosition() {
      return this._startPosition;
   }

   public final int getEndPosition() {
      return this._endPosition;
   }

   public final void setStartPosition(int startPosition) {
      if (startPosition >= 0 && startPosition <= this._buffer.length) {
         this._startPosition = startPosition;
         if (this._endPosition < this._startPosition) {
            this._endPosition = this._startPosition;
         }
      } else {
         throw new Object();
      }
   }

   public final void setEndPosition(int endPosition) {
      if (endPosition >= 0 && endPosition <= this._buffer.length) {
         this._endPosition = endPosition;
         if (this._endPosition < this._startPosition) {
            this._endPosition = this._startPosition;
         }
      } else {
         throw new Object();
      }
   }

   public final boolean isNextTagUniversal() {
      return (this.peekNextByte() & 192) == 0;
   }

   public final boolean isNextTagApplicationSpecific() {
      return (this.peekNextByte() & 192) == 64;
   }

   public final boolean isNextTagContextSpecific() {
      return (this.peekNextByte() & 192) == 128;
   }

   public final boolean isNextTagPrivate() {
      return (this.peekNextByte() & 192) == 192;
   }

   public final int peekNextTag() {
      int tag = this.peekNextByte();
      if (tag != -1) {
         tag &= 31;
      }

      return tag;
   }

   private final int peekNextByte() {
      return this._startPosition < this._buffer.length ? this._buffer[this._startPosition] : -1;
   }

   private final int checkStreamTag(int taggingType, int classFlag, int tag, int baseTag, boolean useDefault) {
      if (taggingType == 1) {
         int streamTag = this._buffer[this._startPosition];
         if ((byte)(streamTag | 32) != (byte)(tag | classFlag | 32)) {
            if (useDefault) {
               return -1;
            }

            throw new ASN1EncodingException();
         }

         this._startPosition++;
         this.readLength(true);
         tag = baseTag;
      }

      if (taggingType == 3) {
         tag = baseTag;
      }

      int streamTag = this._buffer[this._startPosition];
      if ((streamTag & 31) != tag) {
         if (useDefault) {
            return -1;
         } else {
            throw new ASN1EncodingException();
         }
      } else {
         this._startPosition++;
         return streamTag;
      }
   }

   public final void readSequence() {
      this.readSequence(3, 16, 0);
   }

   public final void readSequence(int tagType, int tag) {
      this.readSequence(tagType, tag, 128);
   }

   public final void readSequence(int tagType, int tag, int clsFlags) {
      try {
         this.checkStreamTag(tagType, clsFlags, tag, 16, false);
         this.readLength(tagType != 1);
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final void readSet() {
      this.readSet(3, 17, 0);
   }

   public final void readSet(int tagType, int tag) {
      this.readSet(tagType, tag, 128);
   }

   public final void readSet(int tagType, int tag, int clsFlags) {
      try {
         this.checkStreamTag(tagType, clsFlags, tag, 17, false);
         this.readLength(tagType != 1);
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final boolean readBoolean() {
      return this.readBoolean(3, 1, 0, false, false);
   }

   public final boolean readBoolean(int tagType, int tag) {
      return this.readBoolean(tagType, tag, 128, false, false);
   }

   public final boolean readBoolean(int tagType, int tag, boolean defaultValue) {
      return this.readBoolean(tagType, tag, 128, defaultValue, true);
   }

   public final boolean readBoolean(int tagType, int tag, int clsFlags) {
      return this.readBoolean(tagType, tag, clsFlags, false, false);
   }

   public final boolean readBoolean(int tagType, int tag, int clsFlags, boolean defaultValue) {
      return this.readBoolean(tagType, tag, clsFlags, defaultValue, true);
   }

   private final boolean readBoolean(int tagType, int tag, int clsFlags, boolean defaultValue, boolean useDefault) {
      try {
         if (this.checkStreamTag(tagType, clsFlags, tag, 1, useDefault) == -1) {
            return defaultValue;
         } else {
            int length = this.readLength(tagType != 1);
            int offset = this._startPosition;
            this._startPosition = this._endPosition;
            if (length != 1) {
               throw new ASN1EncodingException();
            } else {
               return this._buffer[offset] == -1;
            }
         }
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final boolean readNull() {
      return this.readNull(3, 5, 0, false, false);
   }

   public final boolean readNull(int tagType, int tag) {
      return this.readNull(tagType, tag, 128, false, false);
   }

   public final boolean readNull(int tagType, int tag, boolean defaultValue) {
      return this.readNull(tagType, tag, 128, defaultValue, true);
   }

   public final boolean readNull(int tagType, int tag, int clsFlags) {
      return this.readNull(tagType, tag, clsFlags, false, false);
   }

   public final boolean readNull(int tagType, int tag, int clsFlags, boolean defaultValue) {
      return this.readNull(tagType, tag, clsFlags, defaultValue, true);
   }

   private final boolean readNull(int tagType, int tag, int clsFlags, boolean defaultValue, boolean useDefault) {
      try {
         if (this.checkStreamTag(tagType, clsFlags, tag, 5, useDefault) == -1) {
            return defaultValue;
         } else {
            int length = this.readLength(tagType != 1);
            this._startPosition = this._endPosition;
            if (length != 0) {
               throw new ASN1EncodingException();
            } else {
               return true;
            }
         }
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final int readInteger() {
      return this.readInteger(3, 2, 0, -1, false, 2);
   }

   public final int readInteger(int tagType, int tag) {
      return this.readInteger(tagType, tag, 128, -1, false, 2);
   }

   public final int readInteger(int tagType, int tag, int defaultValue) {
      return this.readInteger(tagType, tag, 128, defaultValue, true, 2);
   }

   public final int readInteger(int tagType, int tag, int clsFlags, int defaultValue) {
      return this.readInteger(tagType, tag, clsFlags, defaultValue, true, 2);
   }

   public final int readEnumerated() {
      return this.readInteger(3, 10, 0, -1, false, 10);
   }

   public final int readEnumerated(int tagType, int tag) {
      return this.readInteger(tagType, tag, 128, -1, false, 10);
   }

   public final int readEnumerated(int tagType, int tag, int defaultValue) {
      return this.readInteger(tagType, tag, 128, defaultValue, true, 10);
   }

   public final int readEnumerated(int tagType, int tag, int clsFlags, int defaultValue) {
      return this.readInteger(tagType, tag, clsFlags, defaultValue, true, 10);
   }

   private final int readInteger(int tagType, int tag, int clsFlags, int defaultValue, boolean useDefault, int baseTag) {
      try {
         if (this.checkStreamTag(tagType, clsFlags, tag, baseTag, useDefault) == -1) {
            return defaultValue;
         }

         int length = this.readLength(tagType != 1);
         int offset = this._startPosition;
         this._startPosition = this._endPosition;
         if (length > 4) {
            throw new ASN1EncodingException();
         }

         int value = 0;
         if ((this._buffer[offset] & -128) != 0) {
            value = -1;
         }

         for (int j = 0; j < length; j++) {
            value = value << 8 | this._buffer[offset + j] & 255;
         }

         return value;
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final byte[] readIntegerAsByteArray() {
      return this.readIntegerAsByteArray(3, 2, 0, null, false);
   }

   public final byte[] readIntegerAsByteArray(int tagType, int tag) {
      return this.readIntegerAsByteArray(tagType, tag, 128, null, false);
   }

   public final byte[] readIntegerAsByteArray(int tagType, int tag, byte[] defaultValue) {
      return this.readIntegerAsByteArray(tagType, tag, 128, defaultValue, true);
   }

   public final byte[] readIntegerAsByteArray(int tagType, int tag, int clsFlags) {
      return this.readIntegerAsByteArray(tagType, tag, clsFlags, null, false);
   }

   public final byte[] readIntegerAsByteArray(int tagType, int tag, int clsFlags, byte[] defaultValue) {
      return this.readIntegerAsByteArray(tagType, tag, clsFlags, defaultValue, true);
   }

   private final byte[] readIntegerAsByteArray(int tagType, int tag, int clsFlags, byte[] defaultValue, boolean useDefault) {
      try {
         if (this.checkStreamTag(tagType, clsFlags, tag, 2, useDefault) == -1) {
            return defaultValue;
         }

         int length = this.readLength(tagType != 1);
         int offset = this._startPosition;
         this._startPosition = this._endPosition;
         if (length > 1 && this._buffer[offset] == 0) {
            offset++;
            length--;
         }

         return Arrays.copy(this._buffer, offset, length);
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final ASN1SignedByteArray readIntegerAsSignedByteArray() {
      return this.readIntegerAsSignedByteArray(3, 2, 0, null, true, false);
   }

   private final ASN1SignedByteArray readIntegerAsSignedByteArray(
      int tagType, int tag, int clsFlags, byte[] defaultValue, boolean defaultIsPositive, boolean useDefault
   ) {
      try {
         if (this.checkStreamTag(tagType, clsFlags, tag, 2, useDefault) == -1) {
            return new ASN1SignedByteArray(defaultValue, defaultIsPositive);
         }

         int length = this.readLength(tagType != 1);
         int offset = this._startPosition;
         this._startPosition = this._endPosition;
         boolean isPositive = true;
         if (length > 1 && this._buffer[offset] == 0) {
            offset++;
            length--;
         } else if (length > 0 && (this._buffer[offset] & 128) != 0) {
            isPositive = false;
         }

         return new ASN1SignedByteArray(Arrays.copy(this._buffer, offset, length), isPositive);
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final String readPrintableString() {
      return this.readPrintableString(3, 19, 0, null, false);
   }

   public final String readPrintableString(int tagType, int tag) {
      return this.readPrintableString(tagType, tag, 128, null, false);
   }

   public final String readPrintableString(int tagType, int tag, String defaultValue) {
      return this.readPrintableString(tagType, tag, 128, defaultValue, true);
   }

   public final String readPrintableString(int tagType, int tag, int clsFlags) {
      return this.readPrintableString(tagType, tag, clsFlags, null, false);
   }

   public final String readPrintableString(int tagType, int tag, int clsFlags, String defaultValue) {
      return this.readPrintableString(tagType, tag, clsFlags, defaultValue, true);
   }

   private final String readPrintableString(int tagType, int tag, int clsFlags, String defaultValue, boolean useDefault) {
      return this.readString(tagType, tag, clsFlags, defaultValue, useDefault, 19, 0);
   }

   public final String readIA5String() {
      return this.readIA5String(3, 22, 0, null, false);
   }

   public final String readIA5String(int tagType, int tag) {
      return this.readIA5String(tagType, tag, 128, null, false);
   }

   public final String readIA5String(int tagType, int tag, String defaultValue) {
      return this.readIA5String(tagType, tag, 128, defaultValue, true);
   }

   public final String readIA5String(int tagType, int tag, int clsFlags) {
      return this.readIA5String(tagType, tag, clsFlags, null, false);
   }

   public final String readIA5String(int tagType, int tag, int clsFlags, String defaultValue) {
      return this.readIA5String(tagType, tag, clsFlags, defaultValue, true);
   }

   private final String readIA5String(int tagType, int tag, int clsFlags, String defaultValue, boolean useDefault) {
      return this.readString(tagType, tag, clsFlags, defaultValue, useDefault, 22, 0);
   }

   public final String readT61String() {
      return this.readT61String(3, 20, 0, null, false);
   }

   public final String readT61String(int tagType, int tag) {
      return this.readT61String(tagType, tag, 128, null, false);
   }

   public final String readT61String(int tagType, int tag, String defaultValue) {
      return this.readT61String(tagType, tag, 128, defaultValue, true);
   }

   public final String readT61String(int tagType, int tag, int clsFlags) {
      return this.readT61String(tagType, tag, clsFlags, null, false);
   }

   public final String readT61String(int tagType, int tag, int clsFlags, String defaultValue) {
      return this.readT61String(tagType, tag, clsFlags, defaultValue, true);
   }

   private final String readT61String(int tagType, int tag, int clsFlags, String defaultValue, boolean useDefault) {
      return this.readString(tagType, tag, clsFlags, defaultValue, useDefault, 20, 0);
   }

   public final String readUTF8String() {
      return this.readUTF8String(3, 12, 0, null, false);
   }

   public final String readUTF8String(int tagType, int tag) {
      return this.readUTF8String(tagType, tag, 128, null, false);
   }

   public final String readUTF8String(int tagType, int tag, String defaultValue) {
      return this.readUTF8String(tagType, tag, 128, defaultValue, true);
   }

   public final String readUTF8String(int tagType, int tag, int clsFlags) {
      return this.readUTF8String(tagType, tag, clsFlags, null, false);
   }

   public final String readUTF8String(int tagType, int tag, int clsFlags, String defaultValue) {
      return this.readUTF8String(tagType, tag, clsFlags, defaultValue, true);
   }

   private final String readUTF8String(int tagType, int tag, int clsFlags, String defaultValue, boolean useDefault) {
      return this.readString(tagType, tag, clsFlags, defaultValue, useDefault, 12, 0);
   }

   public final String readBMPString() {
      return this.readBMPString(3, 30, 0, null, false);
   }

   public final String readBMPString(int tagType, int tag) {
      return this.readBMPString(tagType, tag, 128, null, false);
   }

   public final String readBMPString(int tagType, int tag, String defaultValue) {
      return this.readBMPString(tagType, tag, 128, defaultValue, true);
   }

   public final String readBMPString(int tagType, int tag, int clsFlags) {
      return this.readBMPString(tagType, tag, clsFlags, null, false);
   }

   public final String readBMPString(int tagType, int tag, int clsFlags, String defaultValue) {
      return this.readBMPString(tagType, tag, clsFlags, defaultValue, true);
   }

   private final String readBMPString(int tagType, int tag, int clsFlags, String defaultValue, boolean useDefault) {
      return this.readString(tagType, tag, clsFlags, defaultValue, useDefault, 30, 0);
   }

   private final String readString(int tagType, int tag, int clsFlags, String defaultValue, boolean useDefault, int baseTag, int recursionLevel) {
      try {
         int streamTag = this.checkStreamTag(tagType, clsFlags, tag, baseTag, useDefault);
         if (streamTag == -1) {
            return defaultValue;
         }

         int length = this.readLength(tagType != 1);
         int offset = this._startPosition;
         if ((streamTag & 32) != 0) {
            if (recursionLevel >= 32) {
               throw new ASN1EncodingException();
            }

            String returnValue = "";
            int endOffset = this._endPosition;

            while (this._startPosition < endOffset) {
               returnValue = ((StringBuffer)(new Object()))
                  .append(returnValue)
                  .append(this.readString(3, baseTag, clsFlags, defaultValue, useDefault, baseTag, recursionLevel + 1))
                  .toString();
            }

            this._startPosition = endOffset;
            return returnValue;
         } else if (baseTag != 30) {
            if (baseTag == 12) {
               try {
                  if (this._buffer.length - length < offset) {
                     throw new ASN1EncodingException();
                  }

                  this._startPosition = this._endPosition;
                  return (String)(new Object(this._buffer, offset, length, "UTF8"));
               } finally {
                  throw new Object();
               }
            } else {
               if (this._buffer.length - length < offset) {
                  throw new ASN1EncodingException();
               }

               this._startPosition = this._endPosition;
               return (String)(new Object(this._buffer, offset, length));
            }
         } else {
            char[] data = new char[length >> 1];
            int endOffset = offset + length;
            int i = 0;

            while (offset < endOffset) {
               data[i++] = (char)(this._buffer[offset++] << 8 | this._buffer[offset++]);
            }

            this._startPosition = this._endPosition;
            return (String)(new Object(data));
         }
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final OID readOID() {
      return this.readOID(3, 6, 0, null, false);
   }

   public final OID readOID(int tagType, int tag) {
      return this.readOID(tagType, tag, 128, null, false);
   }

   public final OID readOID(int tagType, int tag, OID defaultValue) {
      return this.readOID(tagType, tag, 128, defaultValue, true);
   }

   public final OID readOID(int tagType, int tag, int clsFlags) {
      return this.readOID(tagType, tag, clsFlags, null, false);
   }

   public final OID readOID(int tagType, int tag, int clsFlags, OID defaultValue) {
      return this.readOID(tagType, tag, clsFlags, defaultValue, true);
   }

   private final OID readOID(int tagType, int tag, int clsFlags, OID defaultValue, boolean useDefault) {
      try {
         if (this.checkStreamTag(tagType, clsFlags, tag, 6, useDefault) == -1) {
            return defaultValue;
         } else {
            int length = this.readLength(tagType != 1);
            int offset = this._startPosition;
            this._startPosition = this._endPosition;
            if (this._buffer.length - length < offset) {
               throw new ASN1EncodingException();
            } else {
               return OIDs.internOID(this._buffer, offset, length);
            }
         }
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final long readGeneralizedTime() {
      return this.readGeneralizedTime(3, 24, 0, 0, false);
   }

   public final long readGeneralizedTime(int tagType, int tag) {
      return this.readGeneralizedTime(tagType, tag, 128, 0, false);
   }

   public final long readGeneralizedTime(int tagType, int tag, long defaultValue) {
      return this.readGeneralizedTime(tagType, tag, 128, defaultValue, true);
   }

   public final long readGeneralizedTime(int tagType, int tag, int clsFlags) {
      return this.readGeneralizedTime(tagType, tag, clsFlags, -1, false);
   }

   public final long readGeneralizedTime(int tagType, int tag, int clsFlags, long defaultValue) {
      return this.readGeneralizedTime(tagType, tag, clsFlags, defaultValue, true);
   }

   private final long readGeneralizedTime(int tagType, int tag, int clsFlags, long defaultValue, boolean useDefault) {
      return this.readTime(tagType, tag, clsFlags, defaultValue, useDefault, 24);
   }

   public final long readUTCTime() {
      return this.readUTCTime(3, 23, 0, 0, false);
   }

   public final long readUTCTime(int tagType, int tag) {
      return this.readUTCTime(tagType, tag, 128, 0, false);
   }

   public final long readUTCTime(int tagType, int tag, long defaultValue) {
      return this.readUTCTime(tagType, tag, 128, defaultValue, true);
   }

   public final long readUTCTime(int tagType, int tag, int clsFlags) {
      return this.readUTCTime(tagType, tag, clsFlags, -1, false);
   }

   public final long readUTCTime(int tagType, int tag, int clsFlags, long defaultValue) {
      return this.readUTCTime(tagType, tag, clsFlags, defaultValue, true);
   }

   private final long readUTCTime(int tagType, int tag, int clsFlags, long defaultValue, boolean useDefault) {
      return this.readTime(tagType, tag, clsFlags, defaultValue, useDefault, 23);
   }

   private final long readTime(int tagType, int tag, int clsFlags, long defaultValue, boolean useDefault, int baseTag) {
      try {
         int streamTag = this.checkStreamTag(tagType, clsFlags, tag, baseTag, useDefault);
         if (streamTag == -1) {
            return defaultValue;
         }

         int length = this.readLength(tagType != 1);
         int offset = this._startPosition;
         this._startPosition = this._endPosition;
         if ((streamTag & 32) != 0) {
            if (useDefault) {
               return defaultValue;
            } else {
               throw new ASN1EncodingException();
            }
         } else {
            byte[] value = Arrays.copy(this._buffer, offset, length);
            return ASN1InputStream.parseTime(value, (baseTag & 31) == 23);
         }
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final ASN1BitSet readBitString() {
      return this.readBitString(3, 3, 0, null, false, 0);
   }

   public final ASN1BitSet readBitString(int tagType, int tag) {
      return this.readBitString(tagType, tag, 128, null, false, 0);
   }

   public final ASN1BitSet readBitString(int tagType, int tag, byte[] defaultValue) {
      return this.readBitString(tagType, tag, 128, defaultValue, true, 0);
   }

   public final ASN1BitSet readBitString(int tagType, int tag, int clsFlags) {
      return this.readBitString(tagType, tag, clsFlags, null, false, 0);
   }

   public final ASN1BitSet readBitString(int tagType, int tag, int clsFlags, byte[] defaultValue) {
      return this.readBitString(tagType, tag, clsFlags, defaultValue, true, 0);
   }

   private final ASN1BitSet readBitString(int tagType, int tag, int clsFlags, byte[] defaultValue, boolean useDefault, int recursionLevel) {
      try {
         int streamTag = this.checkStreamTag(tagType, clsFlags, tag, 3, useDefault);
         if (streamTag == -1) {
            return defaultValue == null ? null : new ASN1BitSet(defaultValue, defaultValue.length << 3);
         }

         int length = this.readLength(tagType != 1);
         int offset = this._startPosition;
         this._startPosition = this._endPosition;
         if ((streamTag & 32) != 0) {
            if (recursionLevel >= 32) {
               throw new ASN1EncodingException();
            } else {
               return this.recurseReadBitString(new ASN1InputByteArray(this._buffer, offset), length, recursionLevel);
            }
         } else {
            byte[] realData = new byte[--length];
            System.arraycopy(this._buffer, offset + 1, realData, 0, length);
            return new ASN1BitSet(realData, (length << 3) - this._buffer[offset]);
         }
      } finally {
         throw new ASN1EncodingException();
      }
   }

   private final ASN1BitSet recurseReadBitString(ASN1InputByteArray context, int length, int recursionLevel) {
      ASN1BitSet bitSet = new ASN1BitSet(new byte[0], 0);

      while (length > 0) {
         int startPosition = context._startPosition;
         int endPosition = context.getFieldEndPosition();
         length -= endPosition - startPosition;
         int checkTag = context._buffer[startPosition];
         if (checkTag == 0) {
            break;
         }

         context._endPosition = endPosition;
         bitSet = ASN1BitSet.append(bitSet, context.readBitString(3, 3, 0, null, false, recursionLevel + 1));
      }

      return bitSet;
   }

   public final byte[] readOctetString() {
      return this.readOctetString(3, 4, 0, null, false, 0);
   }

   public final byte[] readOctetString(int tagType, int tag) {
      return this.readOctetString(tagType, tag, 128, null, false, 0);
   }

   public final byte[] readOctetString(int tagType, int tag, byte[] defaultValue) {
      return this.readOctetString(tagType, tag, 128, defaultValue, true, 0);
   }

   public final byte[] readOctetString(int tagType, int tag, int clsFlags) {
      return this.readOctetString(tagType, tag, clsFlags, null, false, 0);
   }

   public final byte[] readOctetString(int tagType, int tag, int clsFlags, byte[] defaultValue) {
      return this.readOctetString(tagType, tag, clsFlags, defaultValue, true, 0);
   }

   private final byte[] readOctetString(int tagType, int tag, int clsFlags, byte[] defaultValue, boolean useDefault, int recursionLevel) {
      try {
         int streamTag = this.checkStreamTag(tagType, clsFlags, tag, 4, useDefault);
         if (streamTag == -1) {
            return defaultValue;
         }

         int length = this.readLength(tagType != 1);
         int offset = this._startPosition;
         this._startPosition = this._endPosition;
         if ((streamTag & 32) != 0) {
            if (recursionLevel >= 32) {
               throw new ASN1EncodingException();
            } else {
               return this.recurseReadOctetString(new ASN1InputByteArray(this._buffer, offset), length, recursionLevel);
            }
         } else {
            return Arrays.copy(this._buffer, offset, length);
         }
      } finally {
         throw new ASN1EncodingException();
      }
   }

   private final byte[] recurseReadOctetString(ASN1InputByteArray context, int length, int recursionLevel) {
      byte[] byteArray = new byte[0];

      while (length > 0) {
         int startPosition = context._startPosition;
         int endPosition = context.getFieldEndPosition();
         length -= endPosition - startPosition;
         int checkTag = context._buffer[startPosition];
         if (checkTag == 0) {
            break;
         }

         context._endPosition = startPosition;
         byte[] nextByteArray = context.readOctetString(3, 4, 0, null, false, recursionLevel + 1);
         int oldLength = byteArray.length;
         Array.resize(byteArray, byteArray.length + nextByteArray.length);
         System.arraycopy(nextByteArray, 0, byteArray, oldLength, nextByteArray.length);
      }

      return byteArray;
   }

   public final byte[] readFieldAsByteArray() {
      int start = this._startPosition++;
      this.readLength(true);
      this._startPosition = this._endPosition;
      return Arrays.copy(this._buffer, start, this._endPosition - start);
   }

   public final void skipField() {
      this._startPosition++;
      this.readLength(true);
      this._startPosition = this._endPosition;
   }

   public final void skipField(int tag) {
      try {
         if ((this._buffer[this._startPosition] & 31) != tag) {
            throw new ASN1EncodingException();
         }

         this.skipField();
      } finally {
         throw new ASN1EncodingException();
      }
   }

   public final int getFieldEndPosition() {
      int offset = this._startPosition;
      int endOffset = this._endPosition;
      this._startPosition++;
      int length = this.readLength(true);
      int startPosition = this._startPosition;
      this._startPosition = offset;
      this._endPosition = endOffset;
      return startPosition + length;
   }

   private final int readLength(boolean setEnd) {
      if (this._startPosition >= this._buffer.length) {
         throw new ASN1EncodingException();
      } else {
         return (this._buffer[this._startPosition] & 0xFF) == 128 ? this.readIndefiniteLength(setEnd, 0) - 2 : this.readDefiniteLength(setEnd);
      }
   }

   private final int readDefiniteLength(boolean setEnd) {
      try {
         int length = this._buffer[this._startPosition++];
         if ((length & 128) != 0) {
            int numLengthOctets = length & 127;
            if (numLengthOctets > 4) {
               throw new ASN1EncodingException();
            }

            length = 0;

            for (int i = 0; i < numLengthOctets; i++) {
               length = length << 8 | this._buffer[this._startPosition++] & 255;
            }
         }

         if (setEnd) {
            this._endPosition = this._startPosition + length;
         }

         return length;
      } finally {
         throw new ASN1EncodingException();
      }
   }

   private final int readIndefiniteLength(boolean setEnd, int depth) {
      if (depth == 32) {
         throw new ASN1EncodingException();
      }

      try {
         this._startPosition++;
         int fieldStartPosition = this._startPosition;

         while (this._buffer[this._startPosition++] != 0) {
            int fieldLength;
            if (this._buffer[this._startPosition] == -128) {
               fieldLength = this.readIndefiniteLength(false, depth + 1);
            } else {
               fieldLength = this.readDefiniteLength(false);
            }

            this._startPosition += fieldLength;
         }

         if (this._buffer[this._startPosition++] != 0) {
            throw new ASN1EncodingException();
         }

         if (setEnd) {
            this._endPosition = this._startPosition;
         }

         int length = this._startPosition - fieldStartPosition;
         this._startPosition = fieldStartPosition;
         return length;
      } finally {
         throw new ASN1EncodingException();
      }
   }
}
