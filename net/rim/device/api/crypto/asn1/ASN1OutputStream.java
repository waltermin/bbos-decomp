package net.rim.device.api.crypto.asn1;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import net.rim.device.api.crypto.oid.OID;
import net.rim.device.api.util.DateTimeUtilities;

public class ASN1OutputStream {
   private OutputStream _outputStream;
   private static final int MAX_LENGTH_ENCODING_BYTES = 4;

   public ASN1OutputStream() {
      this._outputStream = new ByteArrayOutputStream();
   }

   public ASN1OutputStream(OutputStream outputStream) {
      if (outputStream == null) {
         throw new IllegalArgumentException();
      }

      this._outputStream = outputStream;
   }

   public byte[] toByteArray() {
      if (!(this._outputStream instanceof ByteArrayOutputStream)) {
         return null;
      }

      ByteArrayOutputStream temp = (ByteArrayOutputStream)this._outputStream;
      return temp.toByteArray();
   }

   private void writeTagAndLength(int taggingType, int classFlag, int tag, int baseTag, boolean constructed, int length) {
      tag &= 31;
      classFlag &= 192;
      if (taggingType == 1) {
         this.writeByte(tag | classFlag | 32);
         this.writeEncodedLength(1 + this.predictEncodedLength(length) + length);
      }

      if (taggingType == 2) {
         tag |= classFlag;
      } else {
         tag = baseTag;
      }

      if (constructed) {
         tag |= 32;
      }

      this.writeByte(tag);
      this.writeEncodedLength(length);
   }

   private void writeTagAndLengthAndByteArray(int taggingType, int classFlag, int tag, int baseTag, boolean constructed, byte[] value) {
      this.writeTagAndLength(taggingType, classFlag, tag, baseTag, constructed, value.length);
      this.writeRawByteArray(value);
   }

   public void writeBoolean(boolean value) {
      this.writeBoolean(value, 3, 1, 0);
   }

   public void writeBoolean(boolean value, int tagging, int tag) {
      this.writeBoolean(value, tagging, tag, 128);
   }

   public void writeBoolean(boolean value, int tagging, int tag, int clsFlags) {
      this.writeTagAndLength(tagging, clsFlags, tag, 1, false, 1);
      this.writeByte(value ? -1 : 0);
   }

   public void writeNull() {
      this.writeNull(3, 5, 0);
   }

   public void writeNull(int tagging, int tag) {
      this.writeNull(tagging, tag, 128);
   }

   public void writeNull(int tagging, int tag, int clsFlags) {
      this.writeTagAndLength(tagging, clsFlags, tag, 5, false, 0);
   }

   public void writeInteger(int value) {
      this.writeInteger(value, 3, 2, 0, 2);
   }

   public void writeInteger(int value, int tagging, int tag) {
      this.writeInteger(value, tagging, tag, 128, 2);
   }

   public void writeInteger(int value, int tagging, int tag, int clsFlags) {
      this.writeInteger(value, tagging, tag, clsFlags, 2);
   }

   private void writeInteger(int value, int tagging, int tag, int clsFlags, int baseTag) {
      this.writeTagAndLength(tagging, clsFlags, tag, baseTag, false, this.predictEncodedIntegerLength(value));
      this.writeEncodedInteger(value);
   }

   public void writeEnumerated(int value) {
      this.writeInteger(value, 3, 2, 0, 10);
   }

   public void writeEnumerated(int value, int tagging, int tag) {
      this.writeInteger(value, tagging, tag, 128, 10);
   }

   public void writeEnumerated(int value, int tagging, int tag, int clsFlags) {
      this.writeInteger(value, tagging, tag, clsFlags, 10);
   }

   public void writeInteger(byte[] value) {
      this.writeInteger(value, 3, 2, 0);
   }

   public void writeInteger(byte[] value, int tagging, int tag) {
      this.writeInteger(value, tagging, tag, 128);
   }

   public void writeInteger(byte[] value, int tagging, int tag, int clsFlags) {
      if (value == null) {
         throw new IllegalArgumentException();
      }

      int encodedLength = this.predictEncodedIntegerLength(value);
      boolean addZero = false;
      if (encodedLength != value.length) {
         addZero = true;
      }

      this.writeTagAndLength(tagging, clsFlags, tag, 2, false, encodedLength);
      if (addZero) {
         this.writeByte(0);
      }

      this.writeRawByteArray(value);
   }

   public void writeInteger(ASN1SignedByteArray signedByteArray) {
      this.writeInteger(signedByteArray, 3, 2, 0);
   }

   public void writeInteger(ASN1SignedByteArray signedByteArray, int tagging, int tag, int clsFlags) {
      if (signedByteArray == null) {
         throw new IllegalArgumentException();
      }

      byte[] value = signedByteArray.getValue();
      int encodedLength = value.length;
      if (signedByteArray.isPositive() && value.length > 0 && (value[0] & 128) != 0) {
         encodedLength++;
      }

      this.writeTagAndLength(tagging, clsFlags, tag, 2, false, encodedLength);
      if (encodedLength != value.length) {
         this.writeByte(0);
      }

      this.writeRawByteArray(value);
   }

   public void writeSequence(ASN1OutputStream sequence) {
      this.writeSequence(sequence, 3, 16, 0);
   }

   public void writeSequence(ASN1OutputStream sequence, int tagging, int tag) {
      this.writeSequence(sequence, tagging, tag, 128);
   }

   public void writeSequence(ASN1OutputStream sequence, int tagging, int tag, int clsFlags) {
      if (sequence == null) {
         throw new IllegalArgumentException();
      }

      byte[] value = sequence.toByteArray();
      if (value == null) {
         throw new IllegalArgumentException();
      }

      this.writeTagAndLengthAndByteArray(tagging, clsFlags, tag, 16, true, value);
   }

   public void writeSet(ASN1OutputStream set) {
      this.writeSet(set, 3, 17, 0);
   }

   public void writeSet(ASN1OutputStream set, int tagging, int tag) {
      this.writeSet(set, tagging, tag, 128);
   }

   public void writeSet(ASN1OutputStream set, int tagging, int tag, int clsFlags) {
      if (set == null) {
         throw new IllegalArgumentException();
      }

      byte[] value = set.toByteArray();
      if (value == null) {
         throw new IllegalArgumentException();
      }

      this.writeTagAndLengthAndByteArray(tagging, clsFlags, tag, 17, true, value);
   }

   public void writePrintableString(String value) {
      this.writePrintableString(value, 3, 19, 0);
   }

   public void writePrintableString(String value, int tagging, int tag) {
      this.writePrintableString(value, tagging, tag, 128);
   }

   public void writePrintableString(String value, int tagging, int tag, int clsFlags) {
      this.writeString(value, tagging, clsFlags, tag, 19);
   }

   private void writeString(String value, int tagging, int clsFlags, int tag, int baseTag) {
      if (value == null) {
         throw new IllegalArgumentException();
      }

      this.writeTagAndLengthAndByteArray(tagging, clsFlags, tag, baseTag, false, value.getBytes());
   }

   public void writeIA5String(String value) {
      this.writeIA5String(value, 3, 22, 0);
   }

   public void writeIA5String(String value, int tagging, int tag) {
      this.writeIA5String(value, tagging, tag, 128);
   }

   public void writeIA5String(String value, int tagging, int tag, int clsFlags) {
      this.writeString(value, tagging, clsFlags, tag, 22);
   }

   public void writeT61String(String value) {
      this.writeT61String(value, 3, 20, 0);
   }

   public void writeT61String(String value, int tagging, int tag) {
      this.writeT61String(value, tagging, tag, 128);
   }

   public void writeT61String(String value, int tagging, int tag, int clsFlags) {
      this.writeString(value, tagging, clsFlags, tag, 20);
   }

   public void writeUTF8String(String value) {
      this.writeUTF8String(value, 3, 12, 0);
   }

   public void writeUTF8String(String value, int tagging, int tag) {
      this.writeUTF8String(value, tagging, tag, 128);
   }

   public void writeUTF8String(String value, int tagging, int tag, int clsFlags) {
      if (value == null) {
         throw new IllegalArgumentException();
      }

      this.writeTagAndLengthAndByteArray(tagging, clsFlags, tag, 12, false, value.getBytes("UTF8"));
   }

   public void writeBMPString(String value) {
      this.writeBMPString(value, 3, 30, 0);
   }

   public void writeBMPString(String value, int tagging, int tag) {
      this.writeBMPString(value, tagging, tag, 128);
   }

   public void writeBMPString(String value, int tagging, int tag, int clsFlags) {
      if (value == null) {
         throw new IllegalArgumentException();
      }

      char[] characters = new char[value.length()];
      value.getChars(0, value.length(), characters, 0);
      byte[] data = new byte[characters.length << 1];

      for (int i = 0; i < characters.length; i++) {
         data[i << 1] = (byte)(characters[i] >> '\b' & 0xFF);
         data[(i << 1) + 1] = (byte)(characters[i] & 0xFF);
      }

      this.writeTagAndLengthAndByteArray(tagging, clsFlags, tag, 30, false, data);
   }

   public void writeString(String value) {
      this.writeString(value, 3, 12, 0);
   }

   public void writeString(String value, int tagging, int tag) {
      this.writeString(value, tagging, tag, 128);
   }

   public void writeString(String value, int tagging, int tag, int clsFlags) {
      this.writeUTF8String(value, tagging, tag, clsFlags);
   }

   public void writeOctetString(byte[] value) {
      this.writeOctetString(value, 3, 4, 0);
   }

   public void writeOctetString(byte[] value, int tagging, int tag) {
      this.writeOctetString(value, tagging, tag, 128);
   }

   public void writeOctetString(byte[] value, int tagging, int tag, int clsFlags) {
      if (value == null) {
         throw new IllegalArgumentException();
      }

      this.writeTagAndLengthAndByteArray(tagging, clsFlags, tag, 4, false, value);
   }

   public void writeTime(long date) {
      if (date >= -631152000000L && date < 2524608000000L) {
         this.writeUTCTime(date);
      } else {
         this.writeGeneralizedTime(date);
      }
   }

   public void writeUTCTime(long date) {
      this.writeUTCTime(date, 3, 23);
   }

   public void writeUTCTime(long date, int tagging, int tag) {
      this.writeUTCTime(date, tagging, tag, 128);
   }

   public void writeUTCTime(long date, int tagging, int tag, int clsFlags) {
      this.writeTagAndLengthAndByteArray(tagging, clsFlags, tag, 23, false, this.parseDate(date, true));
   }

   public void writeGeneralizedTime(long date) {
      this.writeGeneralizedTime(date, 3, 24);
   }

   public void writeGeneralizedTime(long date, int tagging, int tag) {
      this.writeGeneralizedTime(date, tagging, tag, 128);
   }

   public void writeGeneralizedTime(long date, int tagging, int tag, int clsFlags) {
      this.writeTagAndLengthAndByteArray(tagging, clsFlags, tag, 24, false, this.parseDate(date, false));
   }

   private byte[] parseDate(long date, boolean useUTC) {
      byte[] time = null;
      int i = 0;
      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DateTimeUtilities.GMT));
      calendar.setTime(new Date(date));
      int length = useUTC ? 13 : 15;
      time = new byte[length];
      time[length - 1] = 90;
      int year = calendar.get(1);
      if (useUTC) {
         time[i++] = (byte)(48 + year / 10 % 10);
         time[i++] = (byte)(48 + year % 10);
      } else {
         time[i++] = (byte)(48 + year / 1000 % 10);
         time[i++] = (byte)(48 + year / 100 % 10);
         time[i++] = (byte)(48 + year / 10 % 10);
         time[i++] = (byte)(48 + year % 10);
      }

      int month = calendar.get(2) + 1;
      time[i++] = (byte)(48 + month / 10 % 10);
      time[i++] = (byte)(48 + month % 10);
      int day = calendar.get(5);
      time[i++] = (byte)(48 + day / 10 % 10);
      time[i++] = (byte)(48 + day % 10);
      int hour = calendar.get(11);
      time[i++] = (byte)(48 + hour / 10 % 10);
      time[i++] = (byte)(48 + hour % 10);
      int minute = calendar.get(12);
      time[i++] = (byte)(48 + minute / 10 % 10);
      time[i++] = (byte)(48 + minute % 10);
      int second = calendar.get(13);
      time[i++] = (byte)(48 + second / 10 % 10);
      time[i++] = (byte)(48 + second % 10);
      return time;
   }

   public void writeOID(OID value) {
      this.writeOID(value, 3, 6);
   }

   public void writeOID(OID value, int tagging, int tag) {
      this.writeOID(value, tagging, tag, 128);
   }

   public void writeOID(OID value, int tagging, int tag, int clsFlags) {
      if (value == null) {
         throw new IllegalArgumentException();
      }

      this.writeTagAndLengthAndByteArray(tagging, clsFlags, tag, 6, false, value.toByteArray());
   }

   public void writeBitString(ASN1BitSet value) {
      this.writeBitString(value, 3, 3, 0);
   }

   public void writeBitString(ASN1BitSet value, int tagging, int tag) {
      this.writeBitString(value, tagging, tag, 128);
   }

   public void writeBitString(ASN1BitSet value, int tagging, int tag, int clsFlags) {
      if (value == null) {
         throw new IllegalArgumentException();
      }

      byte[] data = value.toByteArray();
      int actualLength = data.length + 1;
      this.writeTagAndLength(tagging, clsFlags, tag, 3, false, actualLength);
      int numbits = value.getLength();
      int padding = data.length * 8 - numbits;
      this.writeByte((byte)padding);
      this.writeRawByteArray(data);
   }

   public void writeBitString(byte[] value) {
      this.writeBitString(value, 3, 3, 0);
   }

   public void writeBitString(byte[] value, int tagging, int tag) {
      this.writeBitString(value, tagging, tag, 128);
   }

   public void writeBitString(byte[] value, int tagging, int tag, int clsFlags) {
      if (value == null) {
         throw new IllegalArgumentException();
      }

      int actualLength = value.length + 1;
      this.writeTagAndLength(tagging, clsFlags, tag, 3, false, actualLength);
      this.writeByte(0);
      this.writeRawByteArray(value);
   }

   private int predictEncodedLength(int length) {
      return this.encodeLength(length, true);
   }

   private void writeEncodedLength(int length) {
      this.encodeLength(length, false);
   }

   private int encodeLength(int length, boolean predictOnly) {
      int bytesWritten = 0;
      if (length <= 127) {
         bytesWritten++;
         if (!predictOnly) {
            this.writeByte(length);
            return bytesWritten;
         }
      } else {
         int mask = -16777216;
         int shift = 24;
         int numLengthBytes = 1;
         boolean writeSucceedingBytes = false;

         for (int i = 0; i < 4; i++) {
            if (!writeSucceedingBytes && (length & mask) != 0) {
               numLengthBytes = 4 - i;
               bytesWritten++;
               if (!predictOnly) {
                  this.writeByte(128 | numLengthBytes);
               }

               writeSucceedingBytes = true;
            }

            if (writeSucceedingBytes) {
               bytesWritten++;
               if (!predictOnly) {
                  this.writeByte((length & mask) >> shift);
               }

               for (int j = i + 1; j < 4; j++) {
                  mask >>>= 8;
                  shift -= 8;
                  bytesWritten++;
                  if (!predictOnly) {
                     this.writeByte((length & mask) >> shift);
                  }
               }
               break;
            }

            mask >>>= 8;
            shift -= 8;
         }
      }

      return bytesWritten;
   }

   private void writeEncodedInteger(int value) {
      this.encodeInteger(value, false);
   }

   private int predictEncodedIntegerLength(int value) {
      return this.encodeInteger(value, true);
   }

   private int predictEncodedIntegerLength(byte[] value) {
      int length = value.length;
      if (value.length > 0 && value[0] < 0) {
         length++;
      }

      return length;
   }

   private int encodeInteger(int value, boolean predictOnly) {
      int mask = -16777216;
      int shift = 24;
      int numBytesWritten = 0;
      if (value < 0) {
         for (int i = 0; i < 4; i++) {
            if ((numBytesWritten != 0 || (value & mask) == mask) && numBytesWritten <= 0) {
               if (i == 3) {
                  if (!predictOnly) {
                     this.writeByte(-1);
                  }

                  numBytesWritten++;
               }
            } else {
               if (numBytesWritten == 0 && ((byte)(value >> shift) & -128) == 0) {
                  if (!predictOnly) {
                     this.writeByte(-1);
                  }

                  numBytesWritten++;
               }

               if (!predictOnly) {
                  this.writeByte((byte)(value >> shift));
               }

               numBytesWritten++;
            }

            mask >>>= 8;
            shift -= 8;
         }
      } else {
         for (int i = 0; i < 4; i++) {
            if (numBytesWritten == 0 && (value & mask) != 0) {
               if ((byte)(value >>> shift) < 0) {
                  if (!predictOnly) {
                     this.writeByte(0);
                  }

                  numBytesWritten++;
               }

               if (!predictOnly) {
                  this.writeByte((byte)(value >> shift));
               }

               numBytesWritten++;
            } else if (numBytesWritten > 0) {
               if (!predictOnly) {
                  this.writeByte((byte)(value >> shift));
               }

               numBytesWritten++;
            } else if (i == 3) {
               if (!predictOnly) {
                  this.writeByte(0);
               }

               numBytesWritten++;
            }

            mask >>>= 8;
            shift -= 8;
         }
      }

      return numBytesWritten;
   }

   private void writeByte(int byteToWrite) {
      this._outputStream.write(byteToWrite);
   }

   public void writeRawByteArray(byte[] byteArrayToWrite) {
      this._outputStream.write(byteArrayToWrite);
   }

   public void writeStreamWithTag(ASN1OutputStream stream, int tagging, int tag) {
      if (stream == null) {
         throw new IllegalArgumentException();
      }

      byte[] value = stream.toByteArray();
      if (value == null) {
         throw new IllegalArgumentException();
      }

      tag &= 31;
      if (tagging == 1) {
         tag = tag | 128 | 32;
      } else if (tagging == 2) {
         tag |= 128;
      }

      this.writeByte(tag);
      this.writeEncodedLength(value.length);
      this.writeRawByteArray(value);
   }
}
